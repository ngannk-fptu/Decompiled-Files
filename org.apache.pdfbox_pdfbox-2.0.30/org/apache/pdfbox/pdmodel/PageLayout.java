/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel;

public enum PageLayout {
    SINGLE_PAGE("SinglePage"),
    ONE_COLUMN("OneColumn"),
    TWO_COLUMN_LEFT("TwoColumnLeft"),
    TWO_COLUMN_RIGHT("TwoColumnRight"),
    TWO_PAGE_LEFT("TwoPageLeft"),
    TWO_PAGE_RIGHT("TwoPageRight");

    private final String value;

    public static PageLayout fromString(String value) {
        for (PageLayout instance : PageLayout.values()) {
            if (!instance.value.equals(value)) continue;
            return instance;
        }
        throw new IllegalArgumentException(value);
    }

    private PageLayout(String value) {
        this.value = value;
    }

    public String stringValue() {
        return this.value;
    }
}

