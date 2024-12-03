/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.service.scheduler;

import com.atlassian.business.insights.core.service.scheduler.ScheduleConfig;
import com.atlassian.business.insights.core.util.DateConversionUtil;
import java.time.DayOfWeek;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class ExportScheduleNextRunTimeCalculator {
    private ExportScheduleNextRunTimeCalculator() {
    }

    public static ZonedDateTime getSameOrNextRunWithinRepeatWeeks(@Nonnull ZonedDateTime nextRunTime, @Nonnull ScheduleConfig config) {
        boolean isFirstWeekOrWithinRepeatWeeks;
        Objects.requireNonNull(nextRunTime, "nextRunTime must not be null");
        Objects.requireNonNull(config, "config must not be null");
        boolean bl = isFirstWeekOrWithinRepeatWeeks = ExportScheduleNextRunTimeCalculator.getNumberOfWeeksModInterval(nextRunTime, config) == 0L;
        if (isFirstWeekOrWithinRepeatWeeks) {
            return nextRunTime;
        }
        return ExportScheduleNextRunTimeCalculator.getNextRunAtTheEarliestEligibleDayWithinRepeatWeek(nextRunTime, config);
    }

    private static ZonedDateTime getNextRunAtTheEarliestEligibleDayWithinRepeatWeek(ZonedDateTime nextRunTime, ScheduleConfig config) {
        long weeksDiff = ExportScheduleNextRunTimeCalculator.getNumberOfWeeksModInterval(nextRunTime, config);
        int remainingWeeksTillFirstEligibleWeek = (int)((long)config.getRepeatIntervalInWeeks() - weeksDiff % (long)config.getRepeatIntervalInWeeks());
        DayOfWeek firstScheduledDateInWeek = ZonedDateTime.parse(config.getScheduleStartDate()).getDayOfWeek();
        return nextRunTime.plus(Period.ofWeeks(remainingWeeksTillFirstEligibleWeek)).with(TemporalAdjusters.previousOrSame(firstScheduledDateInWeek));
    }

    private static long getNumberOfWeeksModInterval(ZonedDateTime scheduledRun, ScheduleConfig config) {
        ZonedDateTime scheduleStartDateTime = DateConversionUtil.parseIsoOffsetDatetimeToZonedDateTime(config.getScheduleStartDate());
        long weeksBetween = ChronoUnit.WEEKS.between(scheduleStartDateTime, scheduledRun);
        return weeksBetween % (long)config.getRepeatIntervalInWeeks();
    }
}

