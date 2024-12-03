/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

import java.io.Serializable;

public interface WeightCombiner {
    public static final WeightCombiner SUM = (WeightCombiner & Serializable)(a, b) -> a + b;
    public static final WeightCombiner MULT = (WeightCombiner & Serializable)(a, b) -> a * b;
    public static final WeightCombiner MIN = Math::min;
    public static final WeightCombiner MAX = Math::max;
    public static final WeightCombiner FIRST = (WeightCombiner & Serializable)(a, b) -> a;
    public static final WeightCombiner SECOND = (WeightCombiner & Serializable)(a, b) -> b;

    public double combine(double var1, double var3);
}

