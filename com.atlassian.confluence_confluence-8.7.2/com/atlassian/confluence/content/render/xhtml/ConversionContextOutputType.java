/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

public enum ConversionContextOutputType {
    PREVIEW,
    DISPLAY,
    WORD,
    PDF,
    HTML_EXPORT,
    FEED,
    EMAIL,
    DIFF;

    private final String lowerName = this.name().toLowerCase();

    public String value() {
        return this.lowerName;
    }
}

