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
import java.util.List;

public final class MultiplyScoreFunction
implements ScoreFunction {
    private List<ComposableScoreFunction> functions;

    public MultiplyScoreFunction(ComposableScoreFunction function1, ComposableScoreFunction function2, ComposableScoreFunction ... functions) {
        this.functions = ImmutableList.builder().add((Object[])new ComposableScoreFunction[]{function1, function2}).add((Object[])functions).build();
    }

    public List<ComposableScoreFunction> getFunctions() {
        return this.functions;
    }
}

