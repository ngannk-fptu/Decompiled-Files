/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.color;

public final class ColorCieLab {
    public static final ColorCieLab BLACK = new ColorCieLab(0.0, 0.0, 0.0);
    public static final ColorCieLab WHITE = new ColorCieLab(100.0, 0.0, 0.0);
    public static final ColorCieLab RED = new ColorCieLab(53.0, 80.0, 67.0);
    public static final ColorCieLab GREEN = new ColorCieLab(88.0, -86.0, 83.0);
    public static final ColorCieLab BLUE = new ColorCieLab(32.0, 79.0, -108.0);
    public final double L;
    public final double a;
    public final double b;

    public ColorCieLab(double L, double a, double b) {
        this.L = L;
        this.a = a;
        this.b = b;
    }

    public String toString() {
        return "{L: " + this.L + ", a: " + this.a + ", b: " + this.b + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ColorCieLab that = (ColorCieLab)o;
        if (Double.compare(that.L, this.L) != 0) {
            return false;
        }
        if (Double.compare(that.a, this.a) != 0) {
            return false;
        }
        return Double.compare(that.b, this.b) == 0;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.L);
        int result = (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.a);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.b);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }
}

