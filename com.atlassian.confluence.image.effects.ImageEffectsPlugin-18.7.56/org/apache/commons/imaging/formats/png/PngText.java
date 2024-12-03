/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png;

public abstract class PngText {
    public final String keyword;
    public final String text;

    public PngText(String keyword, String text) {
        this.keyword = keyword;
        this.text = text;
    }

    public static class Itxt
    extends PngText {
        public final String languageTag;
        public final String translatedKeyword;

        public Itxt(String keyword, String text, String languageTag, String translatedKeyword) {
            super(keyword, text);
            this.languageTag = languageTag;
            this.translatedKeyword = translatedKeyword;
        }
    }

    public static class Ztxt
    extends PngText {
        public Ztxt(String keyword, String text) {
            super(keyword, text);
        }
    }

    public static class Text
    extends PngText {
        public Text(String keyword, String text) {
            super(keyword, text);
        }
    }
}

