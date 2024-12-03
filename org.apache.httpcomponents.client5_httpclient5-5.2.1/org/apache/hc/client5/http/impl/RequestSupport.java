/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.net.PercentCodec
 *  org.apache.hc.core5.net.URIBuilder
 */
package org.apache.hc.client5.http.impl;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.net.PercentCodec;
import org.apache.hc.core5.net.URIBuilder;

@Internal
public final class RequestSupport {
    public static String extractPathPrefix(HttpRequest request) {
        String path = request.getPath();
        try {
            URIBuilder uriBuilder = new URIBuilder(path);
            uriBuilder.setFragment(null);
            uriBuilder.clearParameters();
            uriBuilder.normalizeSyntax();
            List pathSegments = uriBuilder.getPathSegments();
            if (!pathSegments.isEmpty()) {
                pathSegments.remove(pathSegments.size() - 1);
            }
            if (pathSegments.isEmpty()) {
                return "/";
            }
            StringBuilder buf = new StringBuilder();
            buf.append('/');
            for (String pathSegment : pathSegments) {
                PercentCodec.encode((StringBuilder)buf, (CharSequence)pathSegment, (Charset)StandardCharsets.US_ASCII);
                buf.append('/');
            }
            return buf.toString();
        }
        catch (URISyntaxException ex) {
            return path;
        }
    }
}

