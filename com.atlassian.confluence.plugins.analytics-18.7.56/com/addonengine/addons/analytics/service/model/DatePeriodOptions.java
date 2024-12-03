/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service.model;

import com.addonengine.addons.analytics.service.model.DatePeriodOptionsKt;
import com.addonengine.addons.analytics.service.model.PeriodOption;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b\u000e\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\b\u0010\u001f\u001a\u00020\nH\u0002J\b\u0010 \u001a\u00020\nH\u0002J\b\u0010!\u001a\u00020\nH\u0002R\u0011\u0010\f\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000eR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082D\u00a2\u0006\u0002\n\u0000R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u000eR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0018\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u000eR\u0011\u0010\u001a\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0012\u00a8\u0006\""}, d2={"Lcom/addonengine/addons/analytics/service/model/DatePeriodOptions;", "", "fromDate", "Ljava/time/OffsetDateTime;", "toDate", "period", "Lcom/addonengine/addons/analytics/service/model/PeriodOption;", "timezone", "Ljava/time/ZoneId;", "now", "Ljava/time/ZonedDateTime;", "(Ljava/time/OffsetDateTime;Ljava/time/OffsetDateTime;Lcom/addonengine/addons/analytics/service/model/PeriodOption;Ljava/time/ZoneId;Ljava/time/ZonedDateTime;)V", "fillFrom", "getFillFrom", "()Ljava/time/ZonedDateTime;", "fillTo", "getFillTo", "getFromDate", "()Ljava/time/OffsetDateTime;", "hardLimitOfAllowedDataPoints", "", "getNow", "getPeriod", "()Lcom/addonengine/addons/analytics/service/model/PeriodOption;", "queryFrom", "getQueryFrom", "queryTo", "getQueryTo", "getTimezone", "()Ljava/time/ZoneId;", "getToDate", "calculateFillTo", "calculateQueryFrom", "calculateQueryTo", "analytics"})
public final class DatePeriodOptions {
    @NotNull
    private final OffsetDateTime fromDate;
    @NotNull
    private final OffsetDateTime toDate;
    @NotNull
    private final PeriodOption period;
    @NotNull
    private final ZoneId timezone;
    @NotNull
    private final ZonedDateTime now;
    @NotNull
    private final ZonedDateTime queryFrom;
    @NotNull
    private final ZonedDateTime queryTo;
    @NotNull
    private final ZonedDateTime fillFrom;
    @NotNull
    private final ZonedDateTime fillTo;
    private final int hardLimitOfAllowedDataPoints;

    public DatePeriodOptions(@NotNull OffsetDateTime fromDate, @NotNull OffsetDateTime toDate, @NotNull PeriodOption period, @NotNull ZoneId timezone, @NotNull ZonedDateTime now) {
        Intrinsics.checkNotNullParameter((Object)fromDate, (String)"fromDate");
        Intrinsics.checkNotNullParameter((Object)toDate, (String)"toDate");
        Intrinsics.checkNotNullParameter((Object)((Object)period), (String)"period");
        Intrinsics.checkNotNullParameter((Object)timezone, (String)"timezone");
        Intrinsics.checkNotNullParameter((Object)now, (String)"now");
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.period = period;
        this.timezone = timezone;
        this.now = now;
        this.queryFrom = this.calculateQueryFrom();
        this.queryTo = this.calculateQueryTo();
        this.fillFrom = this.queryFrom;
        this.fillTo = this.calculateFillTo();
        this.hardLimitOfAllowedDataPoints = 400;
        ChronoUnit chronoPeriod = DatePeriodOptionsKt.access$getChronoUnitFromPeriodOption(this.period);
        long numberOfDataPoints = this.fillFrom.until(this.fillTo, chronoPeriod);
        if (numberOfDataPoints > (long)this.hardLimitOfAllowedDataPoints) {
            throw new IllegalArgumentException("The current date range and period exceed the maximum number of data points the server is allowed to create");
        }
    }

    public /* synthetic */ DatePeriodOptions(OffsetDateTime offsetDateTime, OffsetDateTime offsetDateTime2, PeriodOption periodOption, ZoneId zoneId, ZonedDateTime zonedDateTime, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 0x10) != 0) {
            ZonedDateTime zonedDateTime2 = ZonedDateTime.now(zoneId);
            Intrinsics.checkNotNullExpressionValue((Object)zonedDateTime2, (String)"now(...)");
            zonedDateTime = zonedDateTime2;
        }
        this(offsetDateTime, offsetDateTime2, periodOption, zoneId, zonedDateTime);
    }

    @NotNull
    public final OffsetDateTime getFromDate() {
        return this.fromDate;
    }

    @NotNull
    public final OffsetDateTime getToDate() {
        return this.toDate;
    }

    @NotNull
    public final PeriodOption getPeriod() {
        return this.period;
    }

    @NotNull
    public final ZoneId getTimezone() {
        return this.timezone;
    }

    @NotNull
    public final ZonedDateTime getNow() {
        return this.now;
    }

    @NotNull
    public final ZonedDateTime getQueryFrom() {
        return this.queryFrom;
    }

    @NotNull
    public final ZonedDateTime getQueryTo() {
        return this.queryTo;
    }

    @NotNull
    public final ZonedDateTime getFillFrom() {
        return this.fillFrom;
    }

    @NotNull
    public final ZonedDateTime getFillTo() {
        return this.fillTo;
    }

    private final ZonedDateTime calculateQueryFrom() {
        ZonedDateTime zonedDateTime;
        ZonedDateTime dateAdjustedForUserTimeZone = this.fromDate.atZoneSameInstant(this.timezone);
        if (WhenMappings.$EnumSwitchMapping$0[this.period.ordinal()] == 1) {
            Intrinsics.checkNotNull((Object)dateAdjustedForUserTimeZone);
            zonedDateTime = DatePeriodOptionsKt.startOf(dateAdjustedForUserTimeZone, PeriodOption.DAY);
        } else {
            Intrinsics.checkNotNull((Object)dateAdjustedForUserTimeZone);
            zonedDateTime = DatePeriodOptionsKt.startOf(dateAdjustedForUserTimeZone, this.period);
        }
        return zonedDateTime;
    }

    private final ZonedDateTime calculateQueryTo() {
        ZonedDateTime zonedDateTime;
        ZonedDateTime dateAdjustedForUserTimeZone = this.toDate.atZoneSameInstant(this.timezone);
        if (WhenMappings.$EnumSwitchMapping$0[this.period.ordinal()] == 1) {
            Intrinsics.checkNotNull((Object)dateAdjustedForUserTimeZone);
            zonedDateTime = DatePeriodOptionsKt.access$plus(DatePeriodOptionsKt.startOf(dateAdjustedForUserTimeZone, PeriodOption.DAY), 1L, PeriodOption.DAY);
        } else {
            Intrinsics.checkNotNull((Object)dateAdjustedForUserTimeZone);
            zonedDateTime = DatePeriodOptionsKt.access$plus(DatePeriodOptionsKt.startOf(dateAdjustedForUserTimeZone, this.period), 1L, this.period);
        }
        return zonedDateTime;
    }

    private final ZonedDateTime calculateFillTo() {
        ZonedDateTime zonedDateTime;
        ZonedDateTime d = this.toDate.atZoneSameInstant(this.timezone);
        if (this.period == PeriodOption.HOUR) {
            ZonedDateTime oneHourBeforeMidnight = this.queryTo.minusHours(1L);
            ZonedDateTime truncatedNow = DatePeriodOptionsKt.startOf(this.now, PeriodOption.HOUR);
            Intrinsics.checkNotNull((Object)oneHourBeforeMidnight);
            zonedDateTime = (ZonedDateTime)ComparisonsKt.minOf((Comparable)oneHourBeforeMidnight, (Comparable)truncatedNow);
        } else {
            Intrinsics.checkNotNull((Object)d);
            zonedDateTime = DatePeriodOptionsKt.startOf(d, this.period);
        }
        return zonedDateTime;
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
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

