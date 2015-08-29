package com.citu.qrcodescanner.util;

/**
 * Created by metalgear8019 on 8/22/2015.
 */
public class ScannedItemUtil {
    public final static String[] TYPES = {"URL", "Plain Text", "Contact Info"};
    public final static String[] ACTIONS = {"Open in browser", "Copy to clipboard"};

    public static int getAction(String data) {
        int action = 1;
        if (data.startsWith("http://") || data.startsWith("https://"))
            action = 0;
        return action;
    }
}
