/*
 * Decompiled with CFR 0.152.
 */
package com.graphbuilder.math.func;

import com.graphbuilder.math.func.Function;

public class SinhFunction
implements Function {
    public double of(double[] d, int numParam) {
        return (Math.pow(Math.E, d[0]) - Math.pow(Math.E, -d[0])) / 2.0;
    }

    public boolean acceptNumParam(int numParam) {
        return numParam == 1;
    }

    public String toString() {
        return "sinh(x)";
    }
}

