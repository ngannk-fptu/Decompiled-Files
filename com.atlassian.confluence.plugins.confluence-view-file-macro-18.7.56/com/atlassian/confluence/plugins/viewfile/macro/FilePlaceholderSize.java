/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.viewfile.macro;

public enum FilePlaceholderSize {
    SMALL(150),
    MEDIUM(250),
    LARGE(400);

    private int height;

    private FilePlaceholderSize(int height) {
        this.height = height;
    }

    public int getHeight() {
        return this.height;
    }

    public static FilePlaceholderSize from(String heightAsString) {
        int height;
        try {
            height = Integer.parseInt(heightAsString);
        }
        catch (NumberFormatException e) {
            return null;
        }
        return FilePlaceholderSize.from(height);
    }

    public static FilePlaceholderSize from(int height) {
        for (FilePlaceholderSize o : FilePlaceholderSize.values()) {
            if (o.getHeight() != height) continue;
            return o;
        }
        return null;
    }
}

