/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl;

import java.net.URISyntaxException;
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
            List<String> pathSegments = uriBuilder.getPathSegments();
            if (!pathSegments.isEmpty()) {
                pathSegments.remove(pathSegments.size() - 1);
            }
            if (pathSegments.isEmpty()) {
                return "/";
            }
            StringBuilder buf = new StringBuilder();
            buf.append('/');
            for (String pathSegment : pathSegments) {
                PercentCodec.encode(buf, pathSegment, StandardCharsets.US_ASCII);
                buf.append('/');
            }
            return buf.toString();
        }
        catch (URISyntaxException ex) {
            return path;
        }
    }
}

