package com.niceelectrical.pro;

import android.content.Context;
import android.print.PrintManager;
import android.webkit.WebView;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "NicePrint")
public class NicePrintPlugin extends Plugin {
    @PluginMethod
    public void print(PluginCall call) {
        String jobName = call.getString("jobName", "NICE ELECTRICAL");
        getActivity().runOnUiThread(() -> {
            try {
                PrintManager printManager = (PrintManager) getContext().getSystemService(Context.PRINT_SERVICE);
                WebView webView = getBridge().getWebView();
                if (printManager == null || webView == null) {
                    call.reject("Android print service is unavailable.");
                    return;
                }
                printManager.print(jobName, webView.createPrintDocumentAdapter(jobName), null);
                JSObject result = new JSObject();
                result.put("started", true);
                call.resolve(result);
            } catch (Exception ex) {
                call.reject("Could not start Android printing: " + ex.getMessage(), ex);
            }
        });
    }
}
