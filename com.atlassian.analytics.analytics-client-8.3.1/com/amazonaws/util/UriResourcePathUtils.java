/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.Request;
import java.net.URI;
import java.net.URISyntaxException;

public final class UriResourcePathUtils {
    public static String addStaticQueryParamtersToRequest(Request<?> request, String uriResourcePath) {
        if (request == null || uriResourcePath == null) {
            return null;
        }
        String resourcePath = uriResourcePath;
        int index = resourcePath.indexOf("?");
        if (index != -1) {
            String queryString = resourcePath.substring(index + 1);
            resourcePath = resourcePath.substring(0, index);
            for (String s : queryString.split("[;&]")) {
                index = s.indexOf("=");
                if (index != -1) {
                    request.addParameter(s.substring(0, index), s.substring(index + 1));
                    continue;
                }
                request.addParameter(s, null);
            }
        }
        return resourcePath;
    }

    public static URI updateUriHost(URI uri, String newHostPrefix) {
        try {
            return new URI(uri.getScheme(), uri.getUserInfo(), newHostPrefix + uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

