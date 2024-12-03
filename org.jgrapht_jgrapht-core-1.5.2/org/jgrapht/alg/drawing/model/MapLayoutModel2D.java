/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;

public class MapLayoutModel2D<V>
implements LayoutModel2D<V> {
    protected Box2D drawableArea;
    protected Map<V, Point2D> points;
    protected Set<V> fixed;

    public MapLayoutModel2D(Box2D drawableArea) {
        this.drawableArea = drawableArea;
        this.points = new LinkedHashMap<V, Point2D>();
        this.fixed = new HashSet<V>();
    }

    @Override
    public Box2D getDrawableArea() {
        return this.drawableArea;
    }

    @Override
    public void setDrawableArea(Box2D drawableArea) {
        this.drawableArea = drawableArea;
    }

    @Override
    public Iterator<Map.Entry<V, Point2D>> iterator() {
        return this.points.entrySet().iterator();
    }

    @Override
    public Point2D get(V vertex) {
        return this.points.get(vertex);
    }

    @Override
    public Point2D put(V vertex, Point2D point) {
        boolean isFixed = this.fixed.contains(vertex);
        if (!isFixed) {
            return this.points.put((Point2D)vertex, point);
        }
        return this.points.putIfAbsent((Point2D)vertex, point);
    }

    @Override
    public void setFixed(V vertex, boolean fixed) {
        if (fixed) {
            this.fixed.add(vertex);
        } else {
            this.fixed.remove(vertex);
        }
    }

    @Override
    public boolean isFixed(V vertex) {
        return this.fixed.contains(vertex);
    }
}

