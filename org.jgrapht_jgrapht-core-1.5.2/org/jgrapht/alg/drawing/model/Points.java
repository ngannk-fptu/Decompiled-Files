/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing.model;

import java.util.function.BiFunction;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.alg.util.ToleranceDoubleComparator;

public abstract class Points {
    private static final ToleranceDoubleComparator TOLERANCE_DOUBLE_COMPARATOR = new ToleranceDoubleComparator(1.0E-9);

    public static double length(Point2D v) {
        return Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY());
    }

    public static Point2D add(Point2D a, Point2D b) {
        return Point2D.of(a.getX() + b.getX(), a.getY() + b.getY());
    }

    public static Point2D subtract(Point2D a, Point2D b) {
        return Point2D.of(a.getX() - b.getX(), a.getY() - b.getY());
    }

    public static Point2D negate(Point2D a) {
        return Points.scalarMultiply(a, -1.0);
    }

    public static Point2D scalarMultiply(Point2D a, double scalar) {
        return Points.scalarMultiply(a, scalar, (x, s) -> x * s);
    }

    public static <S> Point2D scalarMultiply(Point2D a, S scalar, BiFunction<Double, S, Double> mult) {
        return Point2D.of(mult.apply(a.getX(), scalar), mult.apply(a.getY(), scalar));
    }

    public static boolean equals(Point2D p1, Point2D p2) {
        int xEquals = TOLERANCE_DOUBLE_COMPARATOR.compare(p1.getX(), p2.getX());
        if (xEquals != 0) {
            return false;
        }
        return TOLERANCE_DOUBLE_COMPARATOR.compare(p1.getY(), p2.getY()) == 0;
    }
}

