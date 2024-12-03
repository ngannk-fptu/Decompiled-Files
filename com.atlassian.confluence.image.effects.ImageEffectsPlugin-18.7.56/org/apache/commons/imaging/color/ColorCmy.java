/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.color;

public final class ColorCmy {
    public static final ColorCmy CYAN = new ColorCmy(100.0, 0.0, 0.0);
    public static final ColorCmy MAGENTA = new ColorCmy(0.0, 100.0, 0.0);
    public static final ColorCmy YELLOW = new ColorCmy(0.0, 0.0, 100.0);
    public static final ColorCmy BLACK = new ColorCmy(100.0, 100.0, 100.0);
    public static final ColorCmy WHITE = new ColorCmy(0.0, 0.0, 0.0);
    public static final ColorCmy RED = new ColorCmy(0.0, 100.0, 100.0);
    public static final ColorCmy GREEN = new ColorCmy(100.0, 0.0, 100.0);
    public static final ColorCmy BLUE = new ColorCmy(100.0, 100.0, 0.0);
    public final double C;
    public final double M;
    public final double Y;

    public ColorCmy(double C, double M, double Y) {
        this.C = C;
        this.M = M;
        this.Y = Y;
    }

    public String toString() {
        return "{C: " + this.C + ", M: " + this.M + ", Y: " + this.Y + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ColorCmy colorCmy = (ColorCmy)o;
        if (Double.compare(colorCmy.C, this.C) != 0) {
            return false;
        }
        if (Double.compare(colorCmy.M, this.M) != 0) {
            return false;
        }
        return Double.compare(colorCmy.Y, this.Y) == 0;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.C);
        int result = (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.M);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.Y);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }
}

