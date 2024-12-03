/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.geo;

import java.io.Serializable;
import java.util.Locale;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class Point
implements Serializable {
    private static final long serialVersionUID = 3583151228933783558L;
    private final double x;
    private final double y;

    @PersistenceConstructor
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point point) {
        Assert.notNull((Object)point, (String)"Source point must not be null!");
        this.x = point.x;
        this.y = point.y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public int hashCode() {
        int result = 1;
        long temp = Double.doubleToLongBits(this.x);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.y);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        Point other = (Point)obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        return Double.doubleToLongBits(this.y) == Double.doubleToLongBits(other.y);
    }

    public String toString() {
        return String.format(Locale.ENGLISH, "Point [x=%f, y=%f]", this.x, this.y);
    }
}

