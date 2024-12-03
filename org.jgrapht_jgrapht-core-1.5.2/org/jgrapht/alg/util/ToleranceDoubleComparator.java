/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.util;

import java.io.Serializable;
import java.util.Comparator;

public class ToleranceDoubleComparator
implements Comparator<Double>,
Serializable {
    private static final long serialVersionUID = -3819451375975842372L;
    public static final double DEFAULT_EPSILON = 1.0E-9;
    private final double epsilon;

    public ToleranceDoubleComparator() {
        this(1.0E-9);
    }

    public ToleranceDoubleComparator(double epsilon) {
        if (epsilon <= 0.0) {
            throw new IllegalArgumentException("Tolerance must be positive");
        }
        this.epsilon = epsilon;
    }

    @Override
    public int compare(Double o1, Double o2) {
        if (Math.abs(o1 - o2) < this.epsilon) {
            return 0;
        }
        return Double.compare(o1, o2);
    }
}

