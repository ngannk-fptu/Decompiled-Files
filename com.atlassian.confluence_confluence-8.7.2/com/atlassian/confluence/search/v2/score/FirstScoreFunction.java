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

public final class FirstScoreFunction
implements ScoreFunction {
    private List<ComposableScoreFunction> functions;

    public FirstScoreFunction(List<? extends ComposableScoreFunction> functions) {
        this.functions = ImmutableList.copyOf(functions);
    }

    public FirstScoreFunction(ComposableScoreFunction ... functions) {
        this.functions = ImmutableList.copyOf((Object[])functions);
    }

    public List<ComposableScoreFunction> getFunctions() {
        return this.functions;
    }
}

