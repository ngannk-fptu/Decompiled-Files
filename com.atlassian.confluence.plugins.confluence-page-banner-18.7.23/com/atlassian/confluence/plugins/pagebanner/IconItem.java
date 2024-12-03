/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.pagebanner;

public class IconItem {
    private final int height;
    private final int width;
    private final String url;

    public IconItem(int height, int width, String url) {
        this.height = height;
        this.width = width;
        this.url = url;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public String getUrl() {
        return this.url;
    }
}

