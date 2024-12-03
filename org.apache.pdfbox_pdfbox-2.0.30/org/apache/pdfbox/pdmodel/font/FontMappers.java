/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.font;

import org.apache.pdfbox.pdmodel.font.FontMapper;
import org.apache.pdfbox.pdmodel.font.FontMapperImpl;

public final class FontMappers {
    private static FontMapper instance;

    private FontMappers() {
    }

    public static FontMapper instance() {
        if (instance == null) {
            instance = DefaultFontMapper.INSTANCE;
        }
        return instance;
    }

    public static synchronized void set(FontMapper fontMapper) {
        instance = fontMapper;
    }

    private static class DefaultFontMapper {
        private static final FontMapper INSTANCE = new FontMapperImpl();

        private DefaultFontMapper() {
        }
    }
}

