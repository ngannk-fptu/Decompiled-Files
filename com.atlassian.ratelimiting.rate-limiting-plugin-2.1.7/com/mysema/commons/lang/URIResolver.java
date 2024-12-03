/*
 * Decompiled with CFR 0.152.
 */
package com.mysema.commons.lang;

import java.net.URI;

public final class URIResolver {
    private static final String VALID_SCHEME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+.-";

    private URIResolver() {
    }

    public static boolean isAbsoluteURL(String url) {
        if (url == null) {
            return false;
        }
        int colonPos = url.indexOf(58);
        if (colonPos == -1) {
            return false;
        }
        for (int i = 0; i < colonPos; ++i) {
            if (VALID_SCHEME_CHARS.indexOf(url.charAt(i)) != -1) continue;
            return false;
        }
        return true;
    }

    public static String resolve(String base, String url) {
        if (URIResolver.isAbsoluteURL(url)) {
            return url;
        }
        if (url.startsWith("?")) {
            if (base.contains("?")) {
                return base.substring(0, base.lastIndexOf(63)) + url;
            }
            return base + url;
        }
        if (url.startsWith("#")) {
            if (base.contains("#")) {
                return base.substring(0, base.lastIndexOf(35)) + url;
            }
            return base + url;
        }
        return URI.create(base).resolve(url).toString();
    }
}

