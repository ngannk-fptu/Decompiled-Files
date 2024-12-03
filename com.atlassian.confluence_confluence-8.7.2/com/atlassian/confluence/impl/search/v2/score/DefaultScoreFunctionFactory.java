/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 */
package com.atlassian.confluence.impl.search.v2.score;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.impl.search.v2.score.AbstractScoreFunctionFactory;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import com.atlassian.confluence.search.v2.score.ComposableScoreFunction;
import com.atlassian.confluence.search.v2.score.DecayParameters;
import com.atlassian.confluence.search.v2.score.DocValuesFieldValueSource;
import com.atlassian.confluence.search.v2.score.ExpDecayFunction;
import com.atlassian.confluence.search.v2.score.FieldValueFactorFunction;
import com.atlassian.confluence.search.v2.score.FieldValueSource;
import com.atlassian.confluence.search.v2.score.GaussDecayFunction;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

@Deprecated(since="8.7.0", forRemoval=true)
public class DefaultScoreFunctionFactory
extends AbstractScoreFunctionFactory {
    @VisibleForTesting
    public static final ChronoUnit MODIFICATION_DATE_TRUNCATE_GRANULARITY = ChronoUnit.MINUTES;
    private static final Function<byte[], Double> modificationDateToMillisExtractor = bytes -> {
        String dateInIndexStringFormat = new String((byte[])bytes, StandardCharsets.UTF_8);
        Date dateInIndexFormat = LuceneUtils.stringToDate(dateInIndexStringFormat);
        Date truncatedDateInIndexFormat = DefaultScoreFunctionFactory.trunc(dateInIndexFormat, MODIFICATION_DATE_TRUNCATE_GRANULARITY);
        return truncatedDateInIndexFormat.getTime();
    };
    private static final FieldValueSource MODIFICATION_DATE_MILLIS_SOURCE = new DocValuesFieldValueSource(SearchFieldMappings.LAST_MODIFICATION_DATE.getName(), modificationDateToMillisExtractor);

    private static Date trunc(Date date, ChronoUnit granularity) {
        Instant instant = date.toInstant();
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        ZonedDateTime truncatedZonedDateTime = zonedDateTime.truncatedTo(granularity);
        Instant truncatedInstant = truncatedZonedDateTime.toInstant();
        return Date.from(truncatedInstant);
    }

    @Override
    public ComposableScoreFunction createContentTypeScoreFunction() {
        return new FieldValueFactorFunction(new DocValuesFieldValueSource(SearchFieldNames.TYPE, contentTypeScoreExtractor));
    }

    @Override
    public GaussDecayFunction createGaussianDecayFunction() {
        double ORIGIN = ((Long)nowTimestampSupplier.get()).longValue();
        DecayParameters decayParams = DecayParameters.builder(ORIGIN, GAUSSIAN_SCALE).decay(0.32).offset(0.0).build();
        return new GaussDecayFunction(MODIFICATION_DATE_MILLIS_SOURCE, decayParams);
    }

    @Override
    public ExpDecayFunction createExpDecayFunction() {
        double ORIGIN = ((Long)nowTimestampSupplier.get()).longValue();
        DecayParameters decayParams = DecayParameters.builder(ORIGIN, EXP_SCALE).decay(0.92).offset(0.0).build();
        return new ExpDecayFunction(MODIFICATION_DATE_MILLIS_SOURCE, decayParams);
    }
}

