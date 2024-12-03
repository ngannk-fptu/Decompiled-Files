/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.tinymceplugin.placeholder;

import java.awt.Color;
import org.apache.commons.lang3.StringUtils;

public final class StyledString {
    private final String value;
    private final Color color;

    public StyledString(String value) {
        this(value, Color.BLACK);
    }

    public StyledString(String value, Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Color is required.");
        }
        this.value = StringUtils.defaultString((String)value);
        this.color = color;
    }

    public String getValue() {
        return this.value;
    }

    public Color getColor() {
        return this.color;
    }
}

