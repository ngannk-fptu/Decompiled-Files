/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.tomcat.util.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

public class RequestUtil {
    private RequestUtil() {
    }

    public static String normalize(String path) {
        return RequestUtil.normalize(path, true);
    }

    public static String normalize(String path, boolean replaceBackSlash) {
        int index;
        if (path == null) {
            return null;
        }
        String normalized = path;
        if (replaceBackSlash && normalized.indexOf(92) >= 0) {
            normalized = normalized.replace('\\', '/');
        }
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        boolean addedTrailingSlash = false;
        if (normalized.endsWith("/.") || normalized.endsWith("/..")) {
            normalized = normalized + "/";
            addedTrailingSlash = true;
        }
        while ((index = normalized.indexOf("//")) >= 0) {
            normalized = normalized.substring(0, index) + normalized.substring(index + 1);
        }
        while ((index = normalized.indexOf("/./")) >= 0) {
            normalized = normalized.substring(0, index) + normalized.substring(index + 2);
        }
        while ((index = normalized.indexOf("/../")) >= 0) {
            if (index == 0) {
                return null;
            }
            int index2 = normalized.lastIndexOf(47, index - 1);
            normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
        }
        if (normalized.length() > 1 && addedTrailingSlash) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    public static boolean isSameOrigin(HttpServletRequest request, String origin) {
        StringBuilder target = new StringBuilder();
        String scheme = request.getScheme();
        if (scheme == null) {
            return false;
        }
        scheme = scheme.toLowerCase(Locale.ENGLISH);
        target.append(scheme);
        target.append("://");
        String host = request.getServerName();
        if (host == null) {
            return false;
        }
        target.append(host);
        int port = request.getServerPort();
        if (target.length() == origin.length()) {
            if (("http".equals(scheme) || "ws".equals(scheme)) && port != 80 || ("https".equals(scheme) || "wss".equals(scheme)) && port != 443) {
                target.append(':');
                target.append(port);
            }
        } else {
            target.append(':');
            target.append(port);
        }
        return origin.equals(target.toString());
    }

    public static boolean isValidOrigin(String origin) {
        URI originURI;
        if (origin.contains("%")) {
            return false;
        }
        if ("null".equals(origin)) {
            return true;
        }
        if (origin.startsWith("file://")) {
            return true;
        }
        try {
            originURI = new URI(origin);
        }
        catch (URISyntaxException e) {
            return false;
        }
        return originURI.getScheme() != null;
    }
}

