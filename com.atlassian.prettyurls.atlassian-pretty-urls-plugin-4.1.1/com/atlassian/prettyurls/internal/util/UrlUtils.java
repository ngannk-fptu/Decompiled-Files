/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.prettyurls.internal.util;

public class UrlUtils {
    public static String startWithSlash(String uri) {
        return uri.startsWith("/") ? uri : "/" + uri;
    }

    public static String removePrecedingSlash(String uri) {
        return uri.startsWith("/") ? uri.substring(1) : uri;
    }

    public static String removeTrailingSlash(String uri) {
        return uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri;
    }

    public static String prependPath(String path, String uriStr) {
        path = UrlUtils.removeTrailingSlash(UrlUtils.startWithSlash(path));
        uriStr = UrlUtils.removePrecedingSlash(uriStr);
        return path + "/" + uriStr;
    }
}

