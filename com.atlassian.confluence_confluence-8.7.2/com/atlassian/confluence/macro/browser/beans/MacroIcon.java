/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.browser.beans;

public class MacroIcon {
    private static final int MAX_HEIGHT = 80;
    private static final int MAX_WIDTH = 80;
    private final String location;
    private final boolean relative;
    private final int height;
    private final int width;

    public MacroIcon(String location, boolean relative) {
        this(location, relative, 80, 80);
    }

    public MacroIcon(String location, boolean relative, int height, int width) {
        if (height > 80) {
            throw new IllegalArgumentException("Icon height must not exceed 80");
        }
        if (width > 80) {
            throw new IllegalArgumentException("Icon width must not exceed 80");
        }
        this.location = location;
        this.relative = relative;
        this.height = height;
        this.width = width;
    }

    public String getLocation() {
        return this.location;
    }

    public boolean isRelative() {
        return this.relative;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }
}

