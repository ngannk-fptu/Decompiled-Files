/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.flyingpdf.util;

import com.atlassian.annotations.Internal;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

@Internal
public class UrlUtils {
    public static final Pattern pageDisplayUrlPattern = Pattern.compile("/display/([a-zA-Z0-9~]+)/([^/\\?]+)(\\?|\\z)");

    public static String getFullUrl(String baseUrl, String contextPath) {
        String url = baseUrl;
        if (StringUtils.isNotBlank((CharSequence)contextPath)) {
            int i = url.lastIndexOf(contextPath);
            url = i != -1 ? url.substring(0, i) : "";
        }
        return url;
    }

    public static String decodeTitle(String title) {
        if (title == null) {
            return null;
        }
        try {
            return URLDecoder.decode(title, "UTF8");
        }
        catch (UnsupportedEncodingException e) {
            return title;
        }
    }

    public static String encodeTitle(String title) {
        if (title == null) {
            return null;
        }
        try {
            return URLEncoder.encode(title, "UTF8");
        }
        catch (UnsupportedEncodingException e) {
            return title;
        }
    }
}

