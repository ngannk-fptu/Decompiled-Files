/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.host.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

public class HostUtils {
    private HostUtils() {
    }

    public static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String appendRelativePath(URI uri, String path) {
        URI extra = URI.create(path);
        String result = uri.resolve(extra.getPath() + (String)(extra.getRawQuery() != null ? "?" + extra.getRawQuery() : "")).toASCIIString();
        int fragmentIndex = path.indexOf(35);
        return result + (fragmentIndex == -1 ? "" : path.substring(fragmentIndex));
    }
}

