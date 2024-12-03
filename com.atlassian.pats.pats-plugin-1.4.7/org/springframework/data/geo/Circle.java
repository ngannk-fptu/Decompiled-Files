/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.geo;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Shape;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class Circle
implements Shape {
    private static final long serialVersionUID = 5215611530535947924L;
    private final Point center;
    private final Distance radius;

    @PersistenceConstructor
    public Circle(Point center, Distance radius) {
        Assert.notNull((Object)center, (String)"Center point must not be null!");
        Assert.notNull((Object)radius, (String)"Radius must not be null!");
        Assert.isTrue((radius.getValue() >= 0.0 ? 1 : 0) != 0, (String)"Radius must not be negative!");
        this.center = center;
        this.radius = radius;
    }

    public Circle(Point center, double radius) {
        this(center, new Distance(radius));
    }

    public Circle(double centerX, double centerY, double radius) {
        this(new Point(centerX, centerY), new Distance(radius));
    }

    public Point getCenter() {
        return this.center;
    }

    public Distance getRadius() {
        return this.radius;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Circle)) {
            return false;
        }
        Circle circle = (Circle)o;
        if (!ObjectUtils.nullSafeEquals((Object)this.center, (Object)circle.center)) {
            return false;
        }
        return ObjectUtils.nullSafeEquals((Object)this.radius, (Object)circle.radius);
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode((Object)this.center);
        result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.radius);
        return result;
    }

    public String toString() {
        return String.format("Circle: [center=%s, radius=%s]", this.center, this.radius);
    }
}

