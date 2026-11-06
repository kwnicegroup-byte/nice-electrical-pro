package my.niceelectrical.pro;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient.FileChooserParams;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int FILE_CHOOSER_REQUEST = 1001;
    private static final String APP_URL = "https://kwnicegroup-byte.github.io/nice-electrical-pro/v3/?v=20261106-apk";
    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(true);
        settings.setSupportMultipleWindows(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        webView.addJavascriptInterface(new PrintBridge(), "AndroidPrint");

        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String host = uri.getHost();
                if (host != null && (host.equals("kwnicegroup-byte.github.io") || host.equals("github.io"))) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); }
                catch (ActivityNotFoundException ignored) {}
                return true;
            }
            @Override public void onPageFinished(WebView view, String url) {
                view.evaluateJavascript("if(!window.__nicePrintPatched){window.__nicePrintPatched=true;window.print=function(){if(window.AndroidPrint){window.AndroidPrint.print();}};}", null);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = callback;
                Intent intent = params.createIntent();
                try { startActivityForResult(intent, FILE_CHOOSER_REQUEST); }
                catch (ActivityNotFoundException e) { fileCallback = null; callback.onReceiveValue(null); return false; }
                return true;
            }
        });
        if (savedInstanceState == null) webView.loadUrl(APP_URL);
        else webView.restoreState(savedInstanceState);
    }

    private class PrintBridge {
        @JavascriptInterface public void print() {
            runOnUiThread(() -> {
                try {
                    PrintManager manager = (PrintManager) getSystemService(PRINT_SERVICE);
                    manager.print("NICE_ELECTRICAL_PRO", webView.createPrintDocumentAdapter("NICE ELECTRICAL PRO"), new PrintAttributes.Builder().build());
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Unable to open print service", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_REQUEST && fileCallback != null) {
            Uri[] results = (resultCode == RESULT_OK && data != null && data.getData() != null) ? new Uri[]{data.getData()} : null;
            fileCallback.onReceiveValue(results);
            fileCallback = null;
        }
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (fileCallback != null) fileCallback.onReceiveValue(null);
        if (webView != null) { webView.destroy(); webView = null; }
        super.onDestroy();
    }
}
