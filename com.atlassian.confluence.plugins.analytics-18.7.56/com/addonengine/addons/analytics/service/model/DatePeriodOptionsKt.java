/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.sequences.Sequence
 *  kotlin.sequences.SequencesKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.model;

import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.PeriodActivity;
import com.addonengine.addons.analytics.service.model.PeriodOption;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u00000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\u001a\"\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001\u001a\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002\u001a \u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\b\u001a\u00020\tH\u0002\u001a\u0016\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\b\u001a\u00020\t\u00a8\u0006\u0010"}, d2={"fillInMissingDates", "", "Lcom/addonengine/addons/analytics/service/model/PeriodActivity;", "datePeriodOptions", "Lcom/addonengine/addons/analytics/service/model/DatePeriodOptions;", "items", "getChronoUnitFromPeriodOption", "Ljava/time/temporal/ChronoUnit;", "period", "Lcom/addonengine/addons/analytics/service/model/PeriodOption;", "plus", "Ljava/time/ZonedDateTime;", "date", "amountToAdd", "", "startOf", "analytics"})
@SourceDebugExtension(value={"SMAP\nDatePeriodOptions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DatePeriodOptions.kt\ncom/addonengine/addons/analytics/service/model/DatePeriodOptionsKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,152:1\n1208#2,2:153\n1238#2,4:155\n*S KotlinDebug\n*F\n+ 1 DatePeriodOptions.kt\ncom/addonengine/addons/analytics/service/model/DatePeriodOptionsKt\n*L\n143#1:153,2\n143#1:155,4\n*E\n"})
public final class DatePeriodOptionsKt {
    @NotNull
    public static final ZonedDateTime startOf(@NotNull ZonedDateTime date, @NotNull PeriodOption period) {
        ZonedDateTime zonedDateTime;
        Intrinsics.checkNotNullParameter((Object)date, (String)"date");
        Intrinsics.checkNotNullParameter((Object)((Object)period), (String)"period");
        switch (WhenMappings.$EnumSwitchMapping$0[period.ordinal()]) {
            case 1: {
                ZonedDateTime zonedDateTime2 = date.truncatedTo(ChronoUnit.HOURS);
                zonedDateTime = zonedDateTime2;
                Intrinsics.checkNotNullExpressionValue((Object)zonedDateTime2, (String)"truncatedTo(...)");
                break;
            }
            case 2: {
                ZonedDateTime zonedDateTime3 = date.truncatedTo(ChronoUnit.DAYS);
                zonedDateTime = zonedDateTime3;
                Intrinsics.checkNotNullExpressionValue((Object)zonedDateTime3, (String)"truncatedTo(...)");
                break;
            }
            case 3: {
                ZonedDateTime zonedDateTime4 = date.with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS);
                zonedDateTime = zonedDateTime4;
                Intrinsics.checkNotNullExpressionValue((Object)zonedDateTime4, (String)"truncatedTo(...)");
                break;
            }
            case 4: {
                ZonedDateTime zonedDateTime5 = date.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
                zonedDateTime = zonedDateTime5;
                Intrinsics.checkNotNullExpressionValue((Object)zonedDateTime5, (String)"withDayOfMonth(...)");
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        return zonedDateTime;
    }

    private static final ZonedDateTime plus(ZonedDateTime date, long amountToAdd, PeriodOption period) {
        ZonedDateTime zonedDateTime;
        switch (WhenMappings.$EnumSwitchMapping$0[period.ordinal()]) {
            case 1: {
                ZonedDateTime zonedDateTime2 = date.plusHours(amountToAdd);
                zonedDateTime = zonedDateTime2;
                Intrinsics.checkNotNullExpressionValue((Object)zonedDateTime2, (String)"plusHours(...)");
                break;
            }
            case 2: {
                ZonedDateTime zonedDateTime3 = date.plusDays(amountToAdd);
                zonedDateTime = zonedDateTime3;
                Intrinsics.checkNotNullExpressionValue((Object)zonedDateTime3, (String)"plusDays(...)");
                break;
            }
            case 3: {
                ZonedDateTime zonedDateTime4 = date.plusWeeks(amountToAdd);
                zonedDateTime = zonedDateTime4;
                Intrinsics.checkNotNullExpressionValue((Object)zonedDateTime4, (String)"plusWeeks(...)");
                break;
            }
            case 4: {
                ZonedDateTime zonedDateTime5 = date.plusMonths(amountToAdd);
                zonedDateTime = zonedDateTime5;
                Intrinsics.checkNotNullExpressionValue((Object)zonedDateTime5, (String)"plusMonths(...)");
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        return zonedDateTime;
    }

    private static final ChronoUnit getChronoUnitFromPeriodOption(PeriodOption period) {
        ChronoUnit chronoUnit;
        switch (WhenMappings.$EnumSwitchMapping$0[period.ordinal()]) {
            case 1: {
                chronoUnit = ChronoUnit.HOURS;
                break;
            }
            case 2: {
                chronoUnit = ChronoUnit.DAYS;
                break;
            }
            case 3: {
                chronoUnit = ChronoUnit.WEEKS;
                break;
            }
            case 4: {
                chronoUnit = ChronoUnit.MONTHS;
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        return chronoUnit;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public static final List<PeriodActivity> fillInMissingDates(@NotNull DatePeriodOptions datePeriodOptions, @NotNull List<PeriodActivity> items) {
        void $this$associateByTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)datePeriodOptions, (String)"datePeriodOptions");
        Intrinsics.checkNotNullParameter(items, (String)"items");
        ChronoUnit chronoUnit = DatePeriodOptionsKt.getChronoUnitFromPeriodOption(datePeriodOptions.getPeriod());
        int periodsToGenerate = (int)chronoUnit.between(datePeriodOptions.getFillFrom(), datePeriodOptions.getFillTo()) + 1;
        Iterable $this$associateBy$iv = items;
        boolean $i$f$associateBy = false;
        int capacity$iv = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy$iv, (int)10)), (int)16);
        Iterable iterable = $this$associateBy$iv;
        Map destination$iv$iv = new LinkedHashMap(capacity$iv);
        boolean $i$f$associateByTo = false;
        for (Object element$iv$iv : $this$associateByTo$iv$iv) {
            PeriodActivity periodActivity = (PeriodActivity)element$iv$iv;
            Map map = destination$iv$iv;
            boolean bl = false;
            PeriodActivity it = (PeriodActivity)element$iv$iv;
            Instant instant = it.getDate();
            boolean bl2 = false;
            Long l = it.getTotal();
            map.put(instant, l);
        }
        Map dateToTotalMap = destination$iv$iv;
        return SequencesKt.toList((Sequence)SequencesKt.map((Sequence)SequencesKt.take((Sequence)SequencesKt.generateSequence((Object)datePeriodOptions.getFillFrom(), (Function1)((Function1)new Function1<ZonedDateTime, ZonedDateTime>(chronoUnit){
            final /* synthetic */ ChronoUnit $chronoUnit;
            {
                this.$chronoUnit = $chronoUnit;
                super(1);
            }

            @Nullable
            public final ZonedDateTime invoke(@NotNull ZonedDateTime it) {
                Intrinsics.checkNotNullParameter((Object)it, (String)"it");
                return it.plus(1L, this.$chronoUnit);
            }
        })), (int)periodsToGenerate), (Function1)((Function1)new Function1<ZonedDateTime, PeriodActivity>((Map<Instant, Long>)dateToTotalMap){
            final /* synthetic */ Map<Instant, Long> $dateToTotalMap;
            {
                this.$dateToTotalMap = $dateToTotalMap;
                super(1);
            }

            @NotNull
            public final PeriodActivity invoke(@NotNull ZonedDateTime it) {
                Intrinsics.checkNotNullParameter((Object)it, (String)"it");
                Instant date = it.toInstant();
                Intrinsics.checkNotNull((Object)date);
                Long l = this.$dateToTotalMap.get(date);
                return new PeriodActivity(date, l != null ? l : 0L);
            }
        })));
    }

    public static final /* synthetic */ ChronoUnit access$getChronoUnitFromPeriodOption(PeriodOption period) {
        return DatePeriodOptionsKt.getChronoUnitFromPeriodOption(period);
    }

    public static final /* synthetic */ ZonedDateTime access$plus(ZonedDateTime date, long amountToAdd, PeriodOption period) {
        return DatePeriodOptionsKt.plus(date, amountToAdd, period);
    }

    @Metadata(mv={1, 9, 0}, k=3, xi=48)
    public final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[PeriodOption.values().length];
            try {
                nArray[PeriodOption.HOUR.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[PeriodOption.DAY.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[PeriodOption.WEEK.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[PeriodOption.MONTH.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

