/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.color;

public final class ColorCieLch {
    public static final ColorCieLch BLACK = new ColorCieLch(0.0, 0.0, 0.0);
    public static final ColorCieLch WHITE = new ColorCieLch(100.0, 0.0, 297.0);
    public static final ColorCieLch RED = new ColorCieLch(53.0, 80.0, 67.0);
    public static final ColorCieLch GREEN = new ColorCieLch(88.0, -86.0, 83.0);
    public static final ColorCieLch BLUE = new ColorCieLch(32.0, 79.0, -108.0);
    public final double L;
    public final double C;
    public final double H;

    public ColorCieLch(double L, double C, double H) {
        this.L = L;
        this.C = C;
        this.H = H;
    }

    public String toString() {
        return "{L: " + this.L + ", C: " + this.C + ", H: " + this.H + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ColorCieLch that = (ColorCieLch)o;
        if (Double.compare(that.C, this.C) != 0) {
            return false;
        }
        if (Double.compare(that.H, this.H) != 0) {
            return false;
        }
        return Double.compare(that.L, this.L) == 0;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.L);
        int result = (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.C);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.H);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }
}

