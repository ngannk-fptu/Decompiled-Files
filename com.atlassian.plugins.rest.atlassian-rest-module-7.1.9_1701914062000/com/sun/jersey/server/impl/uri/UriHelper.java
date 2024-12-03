/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri;

import java.net.URI;
import java.util.LinkedList;
import javax.ws.rs.core.UriBuilder;

public final class UriHelper {
    public static URI normalize(URI uri, boolean preserveContdSlashes) {
        if (!uri.getRawPath().contains("//")) {
            return uri.normalize();
        }
        String np = UriHelper.removeDotSegments(uri.getRawPath(), preserveContdSlashes);
        if (np.equals(uri.getRawPath())) {
            return uri;
        }
        return UriBuilder.fromUri(uri).replacePath(np).build(new Object[0]);
    }

    private static String removeLeadingSlashesIfNeeded(String path, boolean preserveSlashes) {
        if (preserveSlashes) {
            return path;
        }
        String trimmed = path;
        while (trimmed.startsWith("/")) {
            trimmed = trimmed.substring(1);
        }
        return trimmed;
    }

    public static String removeDotSegments(String path, boolean preserveContdSlashes) {
        if (null == path) {
            return null;
        }
        LinkedList<String> outputSegments = new LinkedList<String>();
        while (path.length() > 0) {
            int slashStartSearchIndex;
            if (path.startsWith("../")) {
                path = UriHelper.removeLeadingSlashesIfNeeded(path.substring(3), preserveContdSlashes);
                continue;
            }
            if (path.startsWith("./")) {
                path = UriHelper.removeLeadingSlashesIfNeeded(path.substring(2), preserveContdSlashes);
                continue;
            }
            if (path.startsWith("/./")) {
                path = "/" + UriHelper.removeLeadingSlashesIfNeeded(path.substring(3), preserveContdSlashes);
                continue;
            }
            if ("/.".equals(path)) {
                path = "/";
                continue;
            }
            if (path.startsWith("/../")) {
                path = "/" + UriHelper.removeLeadingSlashesIfNeeded(path.substring(4), preserveContdSlashes);
                if (outputSegments.isEmpty()) continue;
                outputSegments.remove(outputSegments.size() - 1);
                continue;
            }
            if ("/..".equals(path)) {
                path = "/";
                if (outputSegments.isEmpty()) continue;
                outputSegments.remove(outputSegments.size() - 1);
                continue;
            }
            if ("..".equals(path) || ".".equals(path)) {
                path = "";
                continue;
            }
            if (path.startsWith("/")) {
                path = "/" + UriHelper.removeLeadingSlashesIfNeeded(path.substring(1), preserveContdSlashes);
                slashStartSearchIndex = 1;
            } else {
                slashStartSearchIndex = 0;
            }
            int segLength = path.indexOf(47, slashStartSearchIndex);
            if (-1 == segLength) {
                segLength = path.length();
            }
            outputSegments.add(path.substring(0, segLength));
            path = path.substring(segLength);
        }
        StringBuffer result = new StringBuffer();
        for (String segment : outputSegments) {
            result.append(segment);
        }
        return result.toString();
    }
}

