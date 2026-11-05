package com.niceelectrical.pro;

import android.content.Context;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "NicePrint")
public class NicePrintPlugin extends Plugin {
    @PluginMethod
    public void print(PluginCall call) {
        final String jobName = call.getString("jobName", "NICE ELECTRICAL");
        final String html = call.getString("html", "");
        if (html == null || html.trim().isEmpty()) {
            call.reject("Quotation document is empty.");
            return;
        }
        getActivity().runOnUiThread(() -> {
            try {
                final PrintManager printManager = (PrintManager) getContext().getSystemService(Context.PRINT_SERVICE);
                if (printManager == null) {
                    call.reject("Android print service is unavailable.");
                    return;
                }
                final WebView printView = new WebView(getContext());
                printView.getSettings().setJavaScriptEnabled(false);
                printView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        try {
                            printManager.print(jobName, view.createPrintDocumentAdapter(jobName), null);
                            JSObject result = new JSObject();
                            result.put("started", true);
                            call.resolve(result);
                        } catch (Exception ex) {
                            call.reject("Could not start Android printing: " + ex.getMessage(), ex);
                        }
                    }
                });
                printView.loadDataWithBaseURL(null, html, "text/HTML", "UTF-8", null);
            } catch (Exception ex) {
                call.reject("Could not prepare print document: " + ex.getMessage(), ex);
            }
        });
    }
}
