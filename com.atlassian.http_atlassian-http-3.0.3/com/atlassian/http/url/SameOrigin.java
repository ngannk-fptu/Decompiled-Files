/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.http.url;

import com.google.common.annotations.VisibleForTesting;
import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;

public class SameOrigin {
    public static boolean isSameOrigin(URI uri, URI origin) throws MalformedURLException {
        if (uri == null || origin == null) {
            return false;
        }
        return SameOrigin.check(uri, origin);
    }

    public static boolean isSameOrigin(URL url, URL origin) {
        if (url == null || origin == null) {
            return false;
        }
        try {
            return SameOrigin.check(url.toURI(), origin.toURI());
        }
        catch (URISyntaxException e) {
            return false;
        }
    }

    private static String normaliseHost(URI uri) {
        String host = SameOrigin.getHost(uri);
        if (host == null) {
            return null;
        }
        String asciiHost = IDN.toASCII(host).toLowerCase(Locale.US);
        if (!IDN.toUnicode(asciiHost).equals(host.toLowerCase(Locale.US))) {
            return host;
        }
        return asciiHost;
    }

    private static String getHost(URI uri) {
        String host = uri.getHost();
        return host != null ? host : SameOrigin.getHostFromAuthority(uri);
    }

    private static String getHostFromAuthority(URI uri) {
        String authority = uri.getAuthority();
        if (authority == null) {
            return null;
        }
        int index = authority.indexOf(":");
        if (index != -1) {
            authority = authority.substring(0, index);
        }
        return authority;
    }

    private static boolean check(URI url, URI origin) {
        if (!url.isAbsolute() || !origin.isAbsolute() || url.isOpaque() || origin.isOpaque()) {
            return false;
        }
        return Objects.equals(origin.getScheme(), url.getScheme()) && SameOrigin.getPortForUri(origin) == SameOrigin.getPortForUri(url) && Objects.equals(SameOrigin.normaliseHost(origin), SameOrigin.normaliseHost(url));
    }

    @VisibleForTesting
    static int getPortForUrl(URL url) {
        int port = url.getPort();
        return port != -1 ? port : url.getDefaultPort();
    }

    private static int getPortForUri(URI uri) {
        URL url;
        int uriPort = uri.getPort();
        if (uriPort != -1) {
            return uriPort;
        }
        try {
            url = uri.toURL();
        }
        catch (IllegalArgumentException | MalformedURLException e) {
            return uriPort;
        }
        return SameOrigin.getPortForUrl(url);
    }
}

