/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.search.v2.score;

import com.atlassian.confluence.impl.search.v2.score.AbstractScoreFunctionFactory;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import com.atlassian.confluence.search.v2.score.ComposableScoreFunction;
import com.atlassian.confluence.search.v2.score.DecayParameters;
import com.atlassian.confluence.search.v2.score.DoubleFieldValueSource;
import com.atlassian.confluence.search.v2.score.ExpDecayFunction;
import com.atlassian.confluence.search.v2.score.FieldValueSource;
import com.atlassian.confluence.search.v2.score.GaussDecayFunction;
import java.util.Date;

public class OpenSearchScoreFunctionFactory
extends AbstractScoreFunctionFactory {
    private static final FieldValueSource MODIFICATION_DATE_MILLIS_SOURCE = new DoubleFieldValueSource(SearchFieldMappings.LAST_MODIFICATION_DATE.getName());
    private static final String MILLIS = "ms";

    @Override
    public ComposableScoreFunction createContentTypeScoreFunction() {
        throw new UnsupportedOperationException();
    }

    @Override
    public GaussDecayFunction createGaussianDecayFunction() {
        String origin = LuceneUtils.dateToString(new Date((Long)nowTimestampSupplier.get()));
        DecayParameters decayParams = DecayParameters.builder(origin, GAUSSIAN_SCALE).decay(0.32).offset(0.0).unit(MILLIS).build();
        return new GaussDecayFunction(MODIFICATION_DATE_MILLIS_SOURCE, decayParams);
    }

    @Override
    public ExpDecayFunction createExpDecayFunction() {
        String origin = LuceneUtils.dateToString(new Date((Long)nowTimestampSupplier.get()));
        DecayParameters decayParams = DecayParameters.builder(origin, EXP_SCALE).decay(0.92).offset(0.0).unit(MILLIS).build();
        return new ExpDecayFunction(MODIFICATION_DATE_MILLIS_SOURCE, decayParams);
    }
}

