/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.install;

import com.atlassian.upm.api.util.Option;
import java.io.File;
import java.util.Arrays;

public abstract class ContentTypes {
    public static boolean isXml(File pluginFile, Option<String> contentType) {
        return ContentTypes.matchContentType(pluginFile, contentType, Arrays.asList("application/xml", "text/xml"), Arrays.asList("xml"));
    }

    protected static boolean matchContentType(String desiredType, String contentType) {
        return contentType.equals(desiredType) || contentType.startsWith(desiredType + ";");
    }

    private static boolean matchContentType(File pluginFile, Option<String> contentType, Iterable<String> matchedContentTypes, Iterable<String> matchedFileExtensions) {
        for (String ct : contentType) {
            for (String matched : matchedContentTypes) {
                if (!ContentTypes.matchContentType(matched, ct)) continue;
                return true;
            }
        }
        for (String matched : matchedFileExtensions) {
            if (!pluginFile.getName().toLowerCase().endsWith("." + matched)) continue;
            return true;
        }
        return false;
    }
}

