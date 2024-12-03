/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.score;

import com.atlassian.confluence.search.v2.score.ComposableScoreFunction;
import com.atlassian.confluence.search.v2.score.ExpDecayFunction;
import com.atlassian.confluence.search.v2.score.GaussDecayFunction;

public interface ScoreFunctionFactory {
    @Deprecated
    public ComposableScoreFunction createContentTypeScoreFunction();

    @Deprecated
    public ComposableScoreFunction createRecencyOfModificationScoreFunction();

    public GaussDecayFunction createGaussianDecayFunction();

    public ExpDecayFunction createExpDecayFunction();
}

