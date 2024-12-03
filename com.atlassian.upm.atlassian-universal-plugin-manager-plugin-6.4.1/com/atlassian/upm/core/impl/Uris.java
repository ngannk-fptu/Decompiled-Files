/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.impl;

import com.atlassian.upm.core.Sys;
import java.net.URI;

public abstract class Uris {
    private static final String HTTP_SCHEME = "http";
    private static final String HTTPS_SCHEME = "https";
    private static final String FILE_SCHEME = "file";

    public static boolean hasHttpScheme(URI uri) {
        return uri.getScheme().equalsIgnoreCase(HTTP_SCHEME);
    }

    public static boolean hasHttpsScheme(URI uri) {
        return uri.getScheme().equalsIgnoreCase(HTTPS_SCHEME);
    }

    public static boolean hasFileScheme(URI uri) {
        return uri.getScheme().equalsIgnoreCase(FILE_SCHEME);
    }

    public static boolean isFromMpac(URI uri) {
        if (Uris.isFakeMpac(uri)) {
            return true;
        }
        return uri.getHost().equals(URI.create(Sys.getMpacBaseUrl()).getHost());
    }

    public static boolean isAtlassianURI(URI uri) {
        if (Uris.isFakeMpac(uri) || Uris.isFakeMac(uri)) {
            return true;
        }
        return uri.getHost().endsWith(".atlassian.com");
    }

    private static boolean isFakeMpac(URI uri) {
        return Sys.isUpmDebugModeEnabled() && uri.toASCIIString().contains(":3990/upm/rest/fakempac/1.0");
    }

    private static boolean isFakeMac(URI uri) {
        return Sys.isUpmDebugModeEnabled() && (uri.toASCIIString().contains(":3990/upm/rest/fakemac/1.0") || uri.toASCIIString().contains(":3990/upm/plugins/servlet/fakemac"));
    }
}

