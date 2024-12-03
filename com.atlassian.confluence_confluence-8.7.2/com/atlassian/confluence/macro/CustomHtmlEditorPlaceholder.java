/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import java.util.Map;

public interface CustomHtmlEditorPlaceholder {
    public String getCustomPlaceholder(Map<String, String> var1, String var2, ConversionContext var3) throws PlaceholderGenerationException;

    public static class PlaceholderGenerationException
    extends Exception {
        public PlaceholderGenerationException() {
        }

        public PlaceholderGenerationException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public PlaceholderGenerationException(String message) {
            super(message);
        }

        public PlaceholderGenerationException(Throwable cause) {
            super(cause);
        }
    }
}

