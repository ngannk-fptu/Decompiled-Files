/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.color;

public final class ColorXyz {
    public static final ColorXyz BLACK = new ColorXyz(0.0, 0.0, 0.0);
    public static final ColorXyz WHITE = new ColorXyz(95.05, 100.0, 108.9);
    public static final ColorXyz RED = new ColorXyz(41.24, 21.26, 1.93);
    public static final ColorXyz GREEN = new ColorXyz(35.76, 71.52, 11.92);
    public static final ColorXyz BLUE = new ColorXyz(18.05, 7.22, 95.05);
    public final double X;
    public final double Y;
    public final double Z;

    public ColorXyz(double X, double Y, double Z) {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    public String toString() {
        return "{X: " + this.X + ", Y: " + this.Y + ", Z: " + this.Z + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ColorXyz colorXyz = (ColorXyz)o;
        if (Double.compare(colorXyz.X, this.X) != 0) {
            return false;
        }
        if (Double.compare(colorXyz.Y, this.Y) != 0) {
            return false;
        }
        return Double.compare(colorXyz.Z, this.Z) == 0;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.X);
        int result = (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.Y);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.Z);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }
}

