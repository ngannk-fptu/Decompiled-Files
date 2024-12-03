/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel;

public enum PageMode {
    USE_NONE("UseNone"),
    USE_OUTLINES("UseOutlines"),
    USE_THUMBS("UseThumbs"),
    FULL_SCREEN("FullScreen"),
    USE_OPTIONAL_CONTENT("UseOC"),
    USE_ATTACHMENTS("UseAttachments");

    private final String value;

    public static PageMode fromString(String value) {
        for (PageMode instance : PageMode.values()) {
            if (!instance.value.equals(value)) continue;
            return instance;
        }
        throw new IllegalArgumentException(value);
    }

    private PageMode(String value) {
        this.value = value;
    }

    public String stringValue() {
        return this.value;
    }
}

