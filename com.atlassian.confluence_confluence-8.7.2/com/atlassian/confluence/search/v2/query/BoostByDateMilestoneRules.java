/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import com.atlassian.confluence.search.v2.score.LongFieldValueSource;
import com.atlassian.confluence.search.v2.score.StaircaseFunction;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BoostByDateMilestoneRules {
    @VisibleForTesting
    public static final float BOOST_TODAY = 1.5f;
    @VisibleForTesting
    public static final float BOOST_YESTERDAY = 1.3f;
    @VisibleForTesting
    public static final float BOOST_WEEK_AGO = 1.25f;
    @VisibleForTesting
    public static final float BOOST_MONTH_AGO = 1.2f;
    @VisibleForTesting
    public static final float BOOST_THREE_MONTH_AGO = 1.15f;
    @VisibleForTesting
    public static final float BOOST_SIX_MONTH_AGO = 1.1f;
    @VisibleForTesting
    public static final float BOOST_ONE_YEAR_AGO = 1.05f;
    private static final Supplier<DateMilestone> dateMilestoneSupplier = Lazy.timeToLive(DateMilestone::new, (long)1L, (TimeUnit)TimeUnit.DAYS);

    private BoostByDateMilestoneRules() {
    }

    public static List<BoostFactor> get() {
        DateMilestone dateMilestone = (DateMilestone)dateMilestoneSupplier.get();
        return ImmutableList.of((Object)new BoostFactor(dateMilestone.getStartOfToday(), 1.5f), (Object)new BoostFactor(dateMilestone.getStartOfYesterday(), 1.3f), (Object)new BoostFactor(dateMilestone.getStartOfOneWeekAgo(), 1.25f), (Object)new BoostFactor(dateMilestone.getStartOfOneMonthAgo(), 1.2f), (Object)new BoostFactor(dateMilestone.getStartOfThreeMonthsAgo(), 1.15f), (Object)new BoostFactor(dateMilestone.getStartOfSixMonthsAgo(), 1.1f), (Object)new BoostFactor(dateMilestone.getStartOfOneYearAgo(), 1.05f));
    }

    public static StaircaseFunction createBoostingFunction() {
        ImmutableMap.Builder stairCaseBuilder = ImmutableMap.builder();
        BoostByDateMilestoneRules.get().forEach(x -> stairCaseBuilder.put((Object)x.getMilestone(), (Object)Float.valueOf(x.getBoost())));
        return new StaircaseFunction(new LongFieldValueSource(SearchFieldNames.LAST_MODIFICATION_DATE), (Map<? extends Number, ? extends Number>)stairCaseBuilder.build());
    }

    public static class DateMilestone {
        private final long startOfToday;
        private final long startOfYesterday;
        private final long startOfOneWeekAgo;
        private final long startOfOneMonthAgo;
        private final long startOfThreeMonthsAgo;
        private final long startOfSixMonthsAgo;
        private final long startOfOneYearAgo;

        public DateMilestone() {
            this(Calendar.getInstance());
        }

        public DateMilestone(Calendar cal) {
            cal.set(11, 0);
            cal.set(12, 0);
            cal.set(13, 0);
            cal.set(14, 0);
            this.startOfToday = this.convertToLongFormat(cal);
            cal.add(6, -1);
            this.startOfYesterday = this.convertToLongFormat(cal);
            cal.add(6, -6);
            this.startOfOneWeekAgo = this.convertToLongFormat(cal);
            cal.add(6, 7);
            cal.add(2, -1);
            this.startOfOneMonthAgo = this.convertToLongFormat(cal);
            cal.add(2, -2);
            this.startOfThreeMonthsAgo = this.convertToLongFormat(cal);
            cal.add(2, -3);
            this.startOfSixMonthsAgo = this.convertToLongFormat(cal);
            cal.add(2, 6);
            cal.add(1, -1);
            this.startOfOneYearAgo = this.convertToLongFormat(cal);
        }

        public long getStartOfToday() {
            return this.startOfToday;
        }

        public long getStartOfYesterday() {
            return this.startOfYesterday;
        }

        public long getStartOfOneWeekAgo() {
            return this.startOfOneWeekAgo;
        }

        public long getStartOfOneMonthAgo() {
            return this.startOfOneMonthAgo;
        }

        public long getStartOfThreeMonthsAgo() {
            return this.startOfThreeMonthsAgo;
        }

        public long getStartOfSixMonthsAgo() {
            return this.startOfSixMonthsAgo;
        }

        public long getStartOfOneYearAgo() {
            return this.startOfOneYearAgo;
        }

        private long convertToLongFormat(Calendar cal) {
            return Long.parseLong(LuceneUtils.dateToString(cal.getTime(), LuceneUtils.Resolution.MILLISECOND));
        }
    }

    public static class BoostFactor {
        private final long milestone;
        private final float boost;

        public BoostFactor(long milestone, float boost) {
            this.milestone = milestone;
            this.boost = boost;
        }

        public long getMilestone() {
            return this.milestone;
        }

        public float getBoost() {
            return this.boost;
        }
    }
}

