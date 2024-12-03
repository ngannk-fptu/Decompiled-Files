/*
 * Decompiled with CFR 0.152.
 */
package com.graphbuilder.math.func;

import com.graphbuilder.math.func.Function;

public class PowFunction
implements Function {
    public double of(double[] d, int numParam) {
        return Math.pow(d[0], d[1]);
    }

    public boolean acceptNumParam(int numParam) {
        return numParam == 2;
    }

    public String toString() {
        return "pow(x, y)";
    }
}

