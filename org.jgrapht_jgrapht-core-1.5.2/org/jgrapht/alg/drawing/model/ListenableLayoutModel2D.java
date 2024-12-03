/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;

public class ListenableLayoutModel2D<V>
implements LayoutModel2D<V> {
    protected LayoutModel2D<V> model;
    protected List<BiConsumer<V, Point2D>> listeners;

    public ListenableLayoutModel2D(LayoutModel2D<V> model) {
        this.model = Objects.requireNonNull(model);
        this.listeners = new ArrayList<BiConsumer<V, Point2D>>();
    }

    @Override
    public Box2D getDrawableArea() {
        return this.model.getDrawableArea();
    }

    @Override
    public void setDrawableArea(Box2D drawableArea) {
        this.model.setDrawableArea(drawableArea);
    }

    @Override
    public Iterator<Map.Entry<V, Point2D>> iterator() {
        return this.model.iterator();
    }

    @Override
    public Point2D get(V vertex) {
        return this.model.get(vertex);
    }

    @Override
    public Point2D put(V vertex, Point2D point) {
        if (!this.model.isFixed(vertex)) {
            Point2D oldValue = this.model.put(vertex, point);
            this.notifyListeners(vertex, point);
            return oldValue;
        }
        return this.model.get(vertex);
    }

    @Override
    public void setFixed(V vertex, boolean fixed) {
        this.model.setFixed(vertex, fixed);
    }

    @Override
    public boolean isFixed(V vertex) {
        return this.model.isFixed(vertex);
    }

    public BiConsumer<V, Point2D> addListener(BiConsumer<V, Point2D> listener) {
        this.listeners.add(listener);
        return listener;
    }

    public boolean removeListener(BiConsumer<V, Point2D> listener) {
        return this.listeners.remove(listener);
    }

    protected void notifyListeners(V vertex, Point2D point) {
        for (BiConsumer<V, Point2D> listener : this.listeners) {
            listener.accept(vertex, point);
        }
    }
}

