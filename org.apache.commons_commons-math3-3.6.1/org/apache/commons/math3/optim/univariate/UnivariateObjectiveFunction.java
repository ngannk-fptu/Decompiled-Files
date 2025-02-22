/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.optim.univariate;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.OptimizationData;

public class UnivariateObjectiveFunction
implements OptimizationData {
    private final UnivariateFunction function;

    public UnivariateObjectiveFunction(UnivariateFunction f) {
        this.function = f;
    }

    public UnivariateFunction getObjectiveFunction() {
        return this.function;
    }
}

