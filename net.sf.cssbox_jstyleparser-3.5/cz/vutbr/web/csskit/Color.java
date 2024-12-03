/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

public class Color {
    final int value;

    public Color(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    public Color(int red, int green, int blue, int alpha) {
        this.value = (alpha & 0xFF) << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
    }

    public int getRGB() {
        return this.value;
    }

    public int getRed() {
        return this.value >> 16 & 0xFF;
    }

    public int getGreen() {
        return this.value >> 8 & 0xFF;
    }

    public int getBlue() {
        return this.value & 0xFF;
    }

    public int getAlpha() {
        return this.value >> 24 & 0xFF;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Color color = (Color)o;
        return this.value == color.value;
    }

    public int hashCode() {
        return this.value;
    }
}

