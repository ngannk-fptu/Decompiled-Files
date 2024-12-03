/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.score;

import com.atlassian.confluence.search.v2.score.ComposableScoreFunction;

public class ConstantScoreFunction
implements ComposableScoreFunction {
    private final double weight;

    public ConstantScoreFunction(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return this.weight;
    }
}

