/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.geo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Shape;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class Polygon
implements Iterable<Point>,
Shape {
    private static final long serialVersionUID = -2705040068154648988L;
    private final List<Point> points;

    public Polygon(Point x, Point y, Point z, Point ... others) {
        Assert.notNull((Object)x, (String)"X coordinate must not be null!");
        Assert.notNull((Object)y, (String)"Y coordinate must not be null!");
        Assert.notNull((Object)z, (String)"Z coordinate must not be null!");
        Assert.notNull((Object)others, (String)"Others must not be null!");
        ArrayList<Point> points = new ArrayList<Point>(3 + others.length);
        points.addAll(Arrays.asList(x, y, z));
        points.addAll(Arrays.asList(others));
        this.points = Collections.unmodifiableList(points);
    }

    @PersistenceConstructor
    public Polygon(List<? extends Point> points) {
        Assert.notNull(points, (String)"Points must not be null!");
        ArrayList<Point> pointsToSet = new ArrayList<Point>(points.size());
        for (Point point : points) {
            Assert.notNull((Object)point, (String)"Single Point in Polygon must not be null!");
            pointsToSet.add(point);
        }
        this.points = Collections.unmodifiableList(pointsToSet);
    }

    public List<Point> getPoints() {
        return this.points;
    }

    @Override
    public Iterator<Point> iterator() {
        return this.points.iterator();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Polygon)) {
            return false;
        }
        Polygon that = (Polygon)o;
        return ObjectUtils.nullSafeEquals(this.points, that.points);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.points);
    }

    public String toString() {
        return String.format("Polygon: [%s]", StringUtils.collectionToCommaDelimitedString(this.points));
    }
}

