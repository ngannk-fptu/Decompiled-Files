/*
 * Decompiled with CFR 0.152.
 */
package com.graphbuilder.math.func;

import com.graphbuilder.math.func.Function;

public class SumFunction
implements Function {
    public double of(double[] d, int numParam) {
        double sum = 0.0;
        for (int i = 0; i < numParam; ++i) {
            sum += d[i];
        }
        return sum;
    }

    public boolean acceptNumParam(int numParam) {
        return numParam > 0;
    }

    public String toString() {
        return "sum(x1, x2, ..., xn)";
    }
}

