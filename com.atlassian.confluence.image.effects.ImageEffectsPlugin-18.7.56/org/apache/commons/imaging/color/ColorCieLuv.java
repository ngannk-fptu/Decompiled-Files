/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.color;

public final class ColorCieLuv {
    public static final ColorCieLuv BLACK = new ColorCieLuv(0.0, 0.0, 0.0);
    public static final ColorCieLuv WHITE = new ColorCieLuv(100.0, 0.0, -0.017);
    public static final ColorCieLuv RED = new ColorCieLuv(53.233, 175.053, 37.751);
    public static final ColorCieLuv GREEN = new ColorCieLuv(87.737, -83.08, 107.401);
    public static final ColorCieLuv BLUE = new ColorCieLuv(32.303, -9.4, -130.358);
    public final double L;
    public final double u;
    public final double v;

    public ColorCieLuv(double L, double u, double v) {
        this.L = L;
        this.u = u;
        this.v = v;
    }

    public String toString() {
        return "{L: " + this.L + ", u: " + this.u + ", v: " + this.v + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ColorCieLuv that = (ColorCieLuv)o;
        if (Double.compare(that.L, this.L) != 0) {
            return false;
        }
        if (Double.compare(that.u, this.u) != 0) {
            return false;
        }
        return Double.compare(that.v, this.v) == 0;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.L);
        int result = (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.u);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.v);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }
}

