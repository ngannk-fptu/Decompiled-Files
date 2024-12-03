/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.search.v2.score;

import com.atlassian.confluence.search.v2.score.ComposableScoreFunction;
import com.atlassian.confluence.search.v2.score.ScoreFunction;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;

public final class AverageScoreFunction
implements ScoreFunction {
    private List<ComposableScoreFunction> functions;
    private List<Double> weights;

    private AverageScoreFunction(Builder builder) {
        this.functions = ImmutableList.copyOf(builder.functions);
        this.weights = ImmutableList.copyOf(builder.weights);
    }

    public List<ComposableScoreFunction> getFunctions() {
        return this.functions;
    }

    public List<Double> getWeights() {
        return this.weights;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<ComposableScoreFunction> functions = new ArrayList<ComposableScoreFunction>();
        private final List<Double> weights = new ArrayList<Double>();

        private Builder() {
        }

        public Builder add(ComposableScoreFunction function, double weight) {
            this.functions.add(function);
            this.weights.add(weight);
            return this;
        }

        public AverageScoreFunction build() {
            return new AverageScoreFunction(this);
        }
    }
}

