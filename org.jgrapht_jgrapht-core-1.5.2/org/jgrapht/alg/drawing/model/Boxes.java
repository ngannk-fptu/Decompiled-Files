/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing.model;

import java.util.Comparator;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.alg.util.Pair;

public abstract class Boxes {
    public static boolean containsPoint(Box2D box, Point2D p) {
        double maxX = box.getMinX() + box.getWidth();
        if (p.getX() > maxX) {
            return false;
        }
        if (p.getX() < box.getMinX()) {
            return false;
        }
        double maxY = box.getMinY() + box.getHeight();
        if (p.getY() > maxY) {
            return false;
        }
        return !(p.getY() < box.getMinY());
    }

    public static Pair<Box2D, Box2D> splitAlongXAxis(Box2D box) {
        double newWidth = box.getWidth() / 2.0;
        double height = box.getHeight();
        return Pair.of(Box2D.of(box.getMinX(), box.getMinY(), newWidth, height), Box2D.of(box.getMinX() + newWidth, box.getMinY(), newWidth, height));
    }

    public static Pair<Box2D, Box2D> splitAlongYAxis(Box2D box) {
        double width = box.getWidth();
        double newHeight = box.getHeight() / 2.0;
        return Pair.of(Box2D.of(box.getMinX(), box.getMinY(), width, newHeight), Box2D.of(box.getMinX(), box.getMinY() + newHeight, width, newHeight));
    }

    public static boolean containsPoint(Box2D box, Point2D p, Comparator<Double> comparator) {
        double maxX = box.getMinX() + box.getWidth();
        if (comparator.compare(p.getX(), maxX) > 0) {
            return false;
        }
        if (comparator.compare(p.getX(), box.getMinX()) < 0) {
            return false;
        }
        double maxY = box.getMinY() + box.getHeight();
        if (comparator.compare(p.getY(), maxY) > 0) {
            return false;
        }
        return comparator.compare(p.getY(), box.getMinY()) >= 0;
    }
}

