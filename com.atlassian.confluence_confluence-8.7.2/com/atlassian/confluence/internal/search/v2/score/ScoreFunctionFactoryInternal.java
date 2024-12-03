/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search.v2.score;

import com.atlassian.confluence.search.v2.score.ScoreFunction;
import com.atlassian.confluence.search.v2.score.ScoreFunctionFactory;

public interface ScoreFunctionFactoryInternal
extends ScoreFunctionFactory {
    public ScoreFunction createContentTypeScoreFunction_v2();
}

