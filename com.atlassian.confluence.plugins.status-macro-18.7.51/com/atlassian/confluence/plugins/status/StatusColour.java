/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.status;

import java.awt.Color;

public enum StatusColour {
    GREY(Color.WHITE, new Color(4346478)),
    GREEN(Color.WHITE, new Color(26180), "aui-lozenge-success"),
    RED(Color.WHITE, new Color(12527104), "aui-lozenge-error"),
    YELLOW(new Color(1518413), new Color(1518413), "aui-lozenge-moved"),
    BLUE(Color.WHITE, new Color(477094), "aui-lozenge-current");

    public static StatusColour DEFAULT;
    private final String cssClassName;
    private final Color defaultTextColor;
    private final Color subtleTextColor;

    private StatusColour(Color defaultTextColor, Color subtleTextColor) {
        this(defaultTextColor, subtleTextColor, null);
    }

    private StatusColour(Color defaultTextColor, Color subtleTextColor, String cssClassName) {
        this.defaultTextColor = defaultTextColor;
        this.subtleTextColor = subtleTextColor;
        this.cssClassName = cssClassName;
    }

    public Color forText(boolean subtle) {
        return subtle ? this.subtleTextColor : this.defaultTextColor;
    }

    public String correspondingLozengeCssClass() {
        return this.cssClassName;
    }

    public static StatusColour fromString(String name) {
        try {
            return StatusColour.valueOf(name.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return DEFAULT;
        }
    }

    static {
        DEFAULT = GREY;
    }
}

