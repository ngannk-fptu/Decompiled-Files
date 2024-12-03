/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.util.concurrent.Supplier
 */
package com.atlassian.confluence.impl.search.v2.score;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.internal.search.v2.score.ScoreFunctionFactoryInternal;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.search.v2.score.ComposableScoreFunction;
import com.atlassian.confluence.search.v2.score.ConstantScoreFunction;
import com.atlassian.confluence.search.v2.score.ExpDecayFunction;
import com.atlassian.confluence.search.v2.score.FilteredScoreFunction;
import com.atlassian.confluence.search.v2.score.FirstScoreFunction;
import com.atlassian.confluence.search.v2.score.GaussDecayFunction;
import com.atlassian.confluence.search.v2.score.ScoreFunction;
import com.atlassian.confluence.search.v2.score.SumScoreFunction;
import com.atlassian.util.concurrent.Supplier;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractScoreFunctionFactory
implements ScoreFunctionFactoryInternal {
    protected static final Map<String, Double> typeToBoostMap = Map.of("page", 1.5, "blogpost", 1.3, "userinfo", 1.5, "spacedesc", 1.5, "personalspacedesc", 1.5);
    protected static final double DEFAULT_CONTENT_TYPE_BOOST = 1.0;
    protected static final Function<byte[], Double> contentTypeScoreExtractor = contentTypeBytes -> {
        String contentType = new String((byte[])contentTypeBytes, StandardCharsets.UTF_8);
        return typeToBoostMap.getOrDefault(contentType, 1.0);
    };
    protected static final double CONSTANT_BOOST = 1.9;
    static Supplier<Long> nowTimestampSupplier = System::currentTimeMillis;
    @VisibleForTesting
    public static final double GAUSSIAN_DECAY = 0.32;
    @VisibleForTesting
    public static final int GAUSSIAN_SCALE_DAYS = 4;
    protected static final double GAUSSIAN_SCALE = Duration.ofDays(4L).toMillis();
    protected static final double GAUSSIAN_OFFSET = 0.0;
    @VisibleForTesting
    public static final double EXP_DECAY = 0.92;
    @VisibleForTesting
    public static final int EXP_SCALE_DAYS = 20;
    protected static final double EXP_SCALE = Duration.ofDays(20L).toMillis();
    protected static final double EXP_OFFSET = 0.0;

    @Override
    public ScoreFunction createContentTypeScoreFunction_v2() {
        List functions = typeToBoostMap.entrySet().stream().map(e -> new FilteredScoreFunction(new TermQuery(SearchFieldNames.TYPE, (String)e.getKey()), new ConstantScoreFunction((Double)e.getValue()))).collect(Collectors.toList());
        return new FirstScoreFunction(functions);
    }

    @Override
    public ComposableScoreFunction createRecencyOfModificationScoreFunction() {
        ExpDecayFunction expDecayScoreFunction = this.createExpDecayFunction();
        GaussDecayFunction gaussianDecayScoreFunction = this.createGaussianDecayFunction();
        return new SumScoreFunction(Arrays.asList(expDecayScoreFunction, gaussianDecayScoreFunction), Collections.singletonList(1.9));
    }

    @VisibleForTesting
    public static void setNowTimestampSupplier(Supplier<Long> supplier) {
        nowTimestampSupplier = supplier;
    }
}

