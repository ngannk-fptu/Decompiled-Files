/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.palette;

class ColorCount {
    public final int argb;
    public int count;
    public final int alpha;
    public final int red;
    public final int green;
    public final int blue;

    ColorCount(int argb) {
        this.argb = argb;
        this.alpha = 0xFF & argb >> 24;
        this.red = 0xFF & argb >> 16;
        this.green = 0xFF & argb >> 8;
        this.blue = 0xFF & argb >> 0;
    }

    public int hashCode() {
        return this.argb;
    }

    public boolean equals(Object o) {
        if (o instanceof ColorCount) {
            ColorCount other = (ColorCount)o;
            return other.argb == this.argb;
        }
        return false;
    }
}

