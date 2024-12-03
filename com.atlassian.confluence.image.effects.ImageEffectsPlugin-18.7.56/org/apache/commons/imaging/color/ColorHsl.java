/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.color;

public final class ColorHsl {
    public static final ColorHsl BLACK = new ColorHsl(0.0, 0.0, 0.0);
    public static final ColorHsl WHITE = new ColorHsl(0.0, 0.0, 100.0);
    public static final ColorHsl RED = new ColorHsl(0.0, 100.0, 100.0);
    public static final ColorHsl GREEN = new ColorHsl(120.0, 100.0, 100.0);
    public static final ColorHsl BLUE = new ColorHsl(240.0, 100.0, 100.0);
    public final double H;
    public final double S;
    public final double L;

    public ColorHsl(double H, double S, double L) {
        this.H = H;
        this.S = S;
        this.L = L;
    }

    public String toString() {
        return "{H: " + this.H + ", S: " + this.S + ", L: " + this.L + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ColorHsl colorHsl = (ColorHsl)o;
        if (Double.compare(colorHsl.H, this.H) != 0) {
            return false;
        }
        if (Double.compare(colorHsl.L, this.L) != 0) {
            return false;
        }
        return Double.compare(colorHsl.S, this.S) == 0;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.H);
        int result = (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.S);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.L);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }
}

