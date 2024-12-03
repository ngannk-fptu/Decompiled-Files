/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.whitelist.core.matcher;

import java.net.URI;
import java.net.URISyntaxException;

public class MatcherUtils {
    public static URI normalizeUri(URI uri) throws URISyntaxException {
        if (uri.getScheme() == null) {
            throw new URISyntaxException(uri.toString(), "Expected scheme at index 0: " + uri, 0);
        }
        if (uri.getHost() == null) {
            int index = uri.getScheme().length() + "://".length();
            throw new URISyntaxException(uri.toString(), "Expected host at index " + index + ": " + uri, index);
        }
        if (uri.getUserInfo() == null && uri.getFragment() == null) {
            return uri;
        }
        return new URI(uri.getScheme().toLowerCase(), null, uri.getHost().toLowerCase(), uri.getPort(), uri.getPath(), uri.getQuery(), null);
    }

    public static URI normalizeUriUnchecked(URI uri) {
        try {
            return MatcherUtils.normalizeUri(uri);
        }
        catch (URISyntaxException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
        }
    }

    public static boolean compare(URI base, URI supplied) {
        try {
            URI normalizedUri = MatcherUtils.normalizeUri(supplied);
            return base.getHost().equals(normalizedUri.getHost()) && base.getScheme().equals(normalizedUri.getScheme()) && base.getPort() == normalizedUri.getPort() && supplied.getPath().startsWith(base.getPath());
        }
        catch (URISyntaxException e) {
            return false;
        }
    }

    public static boolean compare(String base, URI supplied) {
        return MatcherUtils.compare(URI.create(base), supplied);
    }
}

