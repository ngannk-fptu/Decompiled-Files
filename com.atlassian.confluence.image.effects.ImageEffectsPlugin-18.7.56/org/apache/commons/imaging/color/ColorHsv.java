/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.color;

public final class ColorHsv {
    public static final ColorHsv BLACK = new ColorHsv(0.0, 0.0, 0.0);
    public static final ColorHsv WHITE = new ColorHsv(0.0, 0.0, 100.0);
    public static final ColorHsv RED = new ColorHsv(0.0, 100.0, 100.0);
    public static final ColorHsv GREEN = new ColorHsv(120.0, 100.0, 100.0);
    public static final ColorHsv BLUE = new ColorHsv(240.0, 100.0, 100.0);
    public final double H;
    public final double S;
    public final double V;

    public ColorHsv(double H, double S, double V) {
        this.H = H;
        this.S = S;
        this.V = V;
    }

    public String toString() {
        return "{H: " + this.H + ", S: " + this.S + ", V: " + this.V + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ColorHsv colorHsv = (ColorHsv)o;
        if (Double.compare(colorHsv.H, this.H) != 0) {
            return false;
        }
        if (Double.compare(colorHsv.S, this.S) != 0) {
            return false;
        }
        return Double.compare(colorHsv.V, this.V) == 0;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.H);
        int result = (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.S);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.V);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }
}

