/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.color;

public final class ColorHunterLab {
    public static final ColorHunterLab BLACK = new ColorHunterLab(0.0, 0.0, 0.0);
    public static final ColorHunterLab WHITE = new ColorHunterLab(100.0, -5.336, 5.433);
    public static final ColorHunterLab RED = new ColorHunterLab(46.109, 78.962, 29.794);
    public static final ColorHunterLab GREEN = new ColorHunterLab(84.569, -72.518, 50.842);
    public static final ColorHunterLab BLUE = new ColorHunterLab(26.87, 72.885, -190.923);
    public final double L;
    public final double a;
    public final double b;

    public ColorHunterLab(double L, double a, double b) {
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
        ColorHunterLab that = (ColorHunterLab)o;
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

