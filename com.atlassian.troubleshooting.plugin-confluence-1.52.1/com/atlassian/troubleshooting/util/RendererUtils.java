/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.util;

public class RendererUtils {
    private RendererUtils() {
        throw new UnsupportedOperationException("This is a utility class");
    }

    public static String renderLink(String url, String urlText) {
        return String.format("<a href=\"%s\" target=\"_blank\">%s</a>", url, urlText);
    }
}

