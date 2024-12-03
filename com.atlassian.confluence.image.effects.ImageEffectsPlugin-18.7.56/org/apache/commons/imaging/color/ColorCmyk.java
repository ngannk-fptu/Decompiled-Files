/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.color;

public final class ColorCmyk {
    public static final ColorCmyk CYAN = new ColorCmyk(100.0, 0.0, 0.0, 0.0);
    public static final ColorCmyk MAGENTA = new ColorCmyk(0.0, 100.0, 0.0, 0.0);
    public static final ColorCmyk YELLOW = new ColorCmyk(0.0, 0.0, 100.0, 0.0);
    public static final ColorCmyk BLACK = new ColorCmyk(0.0, 0.0, 0.0, 100.0);
    public static final ColorCmyk WHITE = new ColorCmyk(0.0, 0.0, 0.0, 0.0);
    public static final ColorCmyk RED = new ColorCmyk(0.0, 100.0, 100.0, 0.0);
    public static final ColorCmyk GREEN = new ColorCmyk(100.0, 0.0, 100.0, 0.0);
    public static final ColorCmyk BLUE = new ColorCmyk(100.0, 100.0, 0.0, 0.0);
    public final double C;
    public final double M;
    public final double Y;
    public final double K;

    public ColorCmyk(double C, double M, double Y, double K) {
        this.C = C;
        this.M = M;
        this.Y = Y;
        this.K = K;
    }

    public String toString() {
        return "{C: " + this.C + ", M: " + this.M + ", Y: " + this.Y + ", K: " + this.K + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ColorCmyk colorCmyk = (ColorCmyk)o;
        if (Double.compare(colorCmyk.C, this.C) != 0) {
            return false;
        }
        if (Double.compare(colorCmyk.K, this.K) != 0) {
            return false;
        }
        if (Double.compare(colorCmyk.M, this.M) != 0) {
            return false;
        }
        return Double.compare(colorCmyk.Y, this.Y) == 0;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.C);
        int result = (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.M);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.Y);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.K);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }
}

