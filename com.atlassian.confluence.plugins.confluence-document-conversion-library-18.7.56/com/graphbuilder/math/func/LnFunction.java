/*
 * Decompiled with CFR 0.152.
 */
package com.graphbuilder.math.func;

import com.graphbuilder.math.func.Function;

public class LnFunction
implements Function {
    public double of(double[] d, int numParam) {
        return Math.log(d[0]);
    }

    public boolean acceptNumParam(int numParam) {
        return numParam == 1;
    }

    public String toString() {
        return "ln(x)";
    }
}

