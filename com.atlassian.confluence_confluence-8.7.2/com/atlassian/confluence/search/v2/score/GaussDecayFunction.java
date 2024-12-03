/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.score;

import com.atlassian.confluence.search.v2.score.ComposableScoreFunction;
import com.atlassian.confluence.search.v2.score.DecayParameters;
import com.atlassian.confluence.search.v2.score.FieldValueSource;

public class GaussDecayFunction
implements ComposableScoreFunction {
    private final FieldValueSource source;
    private final DecayParameters parameters;

    public GaussDecayFunction(FieldValueSource source, DecayParameters parameters) {
        this.source = source;
        this.parameters = parameters;
    }

    public FieldValueSource getSource() {
        return this.source;
    }

    public DecayParameters getParameters() {
        return this.parameters;
    }
}

