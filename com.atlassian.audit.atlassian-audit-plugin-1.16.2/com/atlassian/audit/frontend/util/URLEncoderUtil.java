/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.audit.frontend.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class URLEncoderUtil {
    private static final String ENCODING = StandardCharsets.UTF_8.name();

    public String encode(String url) throws UnsupportedEncodingException {
        return URLEncoder.encode(url, ENCODING);
    }
}

