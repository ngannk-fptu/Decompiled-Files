/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.search.v2.lucene;

public class ContentBodyMaxSizeSystemProperty {
    public static final int DEFAULT = 0x100000;
    private final int value;

    public ContentBodyMaxSizeSystemProperty() {
        int value;
        String contentBodyMaxSize = System.getProperty("atlassian.indexing.contentbody.maxsize");
        if (contentBodyMaxSize != null) {
            try {
                value = Integer.parseInt(contentBodyMaxSize);
            }
            catch (NumberFormatException e) {
                value = 0x100000;
            }
        } else {
            value = 0x100000;
        }
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}

