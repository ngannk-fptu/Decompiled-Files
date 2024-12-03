/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Set;

public final class URIUtils {
    public static URI getBaseURI(URI uri) {
        if (uri == null) {
            return null;
        }
        try {
            return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), null, null);
        }
        catch (URISyntaxException e) {
            return null;
        }
    }

    public static URI prependPath(URI uri, String pathComponent) {
        if (uri == null) {
            return null;
        }
        if (StringUtils.isBlank(pathComponent)) {
            return uri;
        }
        String origPath = uri.getPath();
        if (origPath == null || origPath.isEmpty() || origPath.equals("/")) {
            origPath = null;
        }
        String joinedPath = URIUtils.joinPathComponents(pathComponent, origPath);
        joinedPath = URIUtils.prependLeadingSlashIfMissing(joinedPath);
        try {
            return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), joinedPath, uri.getQuery(), uri.getFragment());
        }
        catch (URISyntaxException e) {
            return null;
        }
    }

    public static String prependLeadingSlashIfMissing(String s) {
        if (s == null) {
            return null;
        }
        if (s.startsWith("/")) {
            return s;
        }
        return "/" + s;
    }

    public static String stripLeadingSlashIfPresent(String s) {
        if (StringUtils.isBlank(s)) {
            return s;
        }
        if (s.startsWith("/")) {
            String tmp = s;
            while (tmp.startsWith("/")) {
                tmp = tmp.substring(1);
            }
            return tmp;
        }
        return s;
    }

    public static String joinPathComponents(String c1, String c2) {
        if (c1 == null && c2 == null) {
            return null;
        }
        if (c1 == null || c1.isEmpty()) {
            return c2;
        }
        if (c2 == null || c2.isEmpty()) {
            return c1;
        }
        if (c1.endsWith("/") && !c2.startsWith("/")) {
            return c1 + c2;
        }
        if (!c1.endsWith("/") && c2.startsWith("/")) {
            return c1 + c2;
        }
        if (c1.endsWith("/") && c2.startsWith("/")) {
            return c1 + URIUtils.stripLeadingSlashIfPresent(c2);
        }
        return c1 + "/" + c2;
    }

    public static URI stripQueryString(URI uri) {
        if (uri == null) {
            return null;
        }
        try {
            return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), null, uri.getFragment());
        }
        catch (URISyntaxException e) {
            return null;
        }
    }

    public static URI removeTrailingSlash(URI uri) {
        if (uri == null) {
            return null;
        }
        String uriString = uri.toString();
        if (uriString.charAt(uriString.length() - 1) == '/') {
            return URI.create(uriString.substring(0, uriString.length() - 1));
        }
        return uri;
    }

    public static void ensureSchemeIsHTTPS(URI uri) {
        if (uri == null) {
            return;
        }
        if (uri.getScheme() == null || !"https".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("The URI scheme must be https");
        }
    }

    public static void ensureSchemeIsHTTPSorHTTP(URI uri) {
        if (uri == null) {
            return;
        }
        if (uri.getScheme() == null || !Arrays.asList("http", "https").contains(uri.getScheme().toLowerCase())) {
            throw new IllegalArgumentException("The URI scheme must be https or http");
        }
    }

    public static void ensureSchemeIsNotProhibited(URI uri, Set<String> prohibitedURISchemes) {
        if (uri == null || uri.getScheme() == null || prohibitedURISchemes == null || prohibitedURISchemes.isEmpty()) {
            return;
        }
        if (prohibitedURISchemes.contains(uri.getScheme().toLowerCase())) {
            throw new IllegalArgumentException("The URI scheme " + uri.getScheme() + " is prohibited");
        }
    }

    private URIUtils() {
    }
}

