/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.plugins.gatekeeper.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletResponse;

public class HttpUtil {
    public static String encodeURIComponent(String s) {
        String result = null;
        try {
            result = URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20").replaceAll("\\%21", "!").replaceAll("\\%27", "'").replaceAll("\\%28", "(").replaceAll("\\%29", ")").replaceAll("\\%7E", "~");
        }
        catch (UnsupportedEncodingException e) {
            result = s;
        }
        return result;
    }

    public static void setNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, must-revalidate, max-age=5");
        response.setHeader("Pragma", "");
        response.setDateHeader("Expires", System.currentTimeMillis());
    }
}

