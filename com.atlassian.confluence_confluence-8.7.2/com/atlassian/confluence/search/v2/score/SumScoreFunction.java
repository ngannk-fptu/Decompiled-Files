/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.search.v2.score;

import com.atlassian.confluence.search.v2.score.ComposableScoreFunction;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class SumScoreFunction
implements ComposableScoreFunction {
    private List<ComposableScoreFunction> functions;
    private List<Double> constants;

    public SumScoreFunction(ComposableScoreFunction function1, ComposableScoreFunction function2, ComposableScoreFunction ... functions) {
        this((List<ComposableScoreFunction>)ImmutableList.builder().add((Object[])new ComposableScoreFunction[]{function1, function2}).add((Object[])functions).build(), Collections.emptyList());
    }

    public SumScoreFunction(@NonNull List<ComposableScoreFunction> functions, @NonNull List<Double> constants) {
        this.functions = functions;
        this.constants = constants;
    }

    public List<ComposableScoreFunction> getFunctions() {
        return this.functions;
    }

    public List<Double> getConstants() {
        return this.constants;
    }
}

