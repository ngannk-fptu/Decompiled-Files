/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing.model;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.Point2D;

public interface LayoutModel2D<V>
extends Iterable<Map.Entry<V, Point2D>> {
    public Box2D getDrawableArea();

    public void setDrawableArea(Box2D var1);

    public Point2D get(V var1);

    public Point2D put(V var1, Point2D var2);

    public void setFixed(V var1, boolean var2);

    public boolean isFixed(V var1);

    default public Map<V, Point2D> collect() {
        LinkedHashMap<V, Point2D> map = new LinkedHashMap<V, Point2D>();
        for (Map.Entry<V, Point2D> p : this) {
            map.put(p.getKey(), p.getValue());
        }
        return map;
    }

    @Override
    public Iterator<Map.Entry<V, Point2D>> iterator();
}

