/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.geo;

import org.springframework.data.geo.Point;
import org.springframework.data.geo.Shape;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class Box
implements Shape {
    private static final long serialVersionUID = 8198095179084040711L;
    private final Point first;
    private final Point second;

    public Box(Point first, Point second) {
        Assert.notNull((Object)first, (String)"First point must not be null!");
        Assert.notNull((Object)second, (String)"Second point must not be null!");
        this.first = first;
        this.second = second;
    }

    public Box(double[] first, double[] second) {
        Assert.isTrue((first.length == 2 ? 1 : 0) != 0, (String)"Point array has to have 2 elements!");
        Assert.isTrue((second.length == 2 ? 1 : 0) != 0, (String)"Point array has to have 2 elements!");
        this.first = new Point(first[0], first[1]);
        this.second = new Point(second[0], second[1]);
    }

    public Point getFirst() {
        return this.first;
    }

    public Point getSecond() {
        return this.second;
    }

    public String toString() {
        return String.format("Box [%s, %s]", this.first, this.second);
    }

    public int hashCode() {
        int result = 31;
        result += 17 * this.first.hashCode();
        return result += 17 * this.second.hashCode();
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Box)) {
            return false;
        }
        Box that = (Box)obj;
        return this.first.equals(that.first) && this.second.equals(that.second);
    }
}

