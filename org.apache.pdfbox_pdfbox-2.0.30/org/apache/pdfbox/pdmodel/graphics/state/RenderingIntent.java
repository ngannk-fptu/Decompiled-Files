/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.state;

public enum RenderingIntent {
    ABSOLUTE_COLORIMETRIC("AbsoluteColorimetric"),
    RELATIVE_COLORIMETRIC("RelativeColorimetric"),
    SATURATION("Saturation"),
    PERCEPTUAL("Perceptual");

    private final String value;

    public static RenderingIntent fromString(String value) {
        for (RenderingIntent instance : RenderingIntent.values()) {
            if (!instance.value.equals(value)) continue;
            return instance;
        }
        return RELATIVE_COLORIMETRIC;
    }

    private RenderingIntent(String value) {
        this.value = value;
    }

    public String stringValue() {
        return this.value;
    }
}

