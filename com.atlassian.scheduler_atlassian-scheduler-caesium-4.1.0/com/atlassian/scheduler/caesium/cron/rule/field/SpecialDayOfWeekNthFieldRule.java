/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.joda.time.LocalDate
 *  org.joda.time.ReadablePartial
 */
package com.atlassian.scheduler.caesium.cron.rule.field;

import com.atlassian.scheduler.caesium.cron.rule.field.DayOfWeekConstantConverter;
import com.atlassian.scheduler.caesium.cron.rule.field.SpecialDayFieldRule;
import com.google.common.base.Preconditions;
import org.joda.time.LocalDate;
import org.joda.time.ReadablePartial;

public class SpecialDayOfWeekNthFieldRule
extends SpecialDayFieldRule {
    private static final long serialVersionUID = 6051673567197325589L;
    private final int isoDayOfWeek;
    private final int nth;

    public SpecialDayOfWeekNthFieldRule(int cronDayOfWeek, int nth) {
        Preconditions.checkArgument((cronDayOfWeek >= 1 && cronDayOfWeek <= 7 ? 1 : 0) != 0, (Object)"cronDayOfWeek must be in the range [1,7]");
        Preconditions.checkArgument((nth >= 1 && nth <= 5 ? 1 : 0) != 0, (Object)"nth must be in the range [1,5]");
        this.isoDayOfWeek = DayOfWeekConstantConverter.cronToIso(cronDayOfWeek);
        this.nth = nth;
    }

    @Override
    int calculateMatchingDay(int year, int month) {
        LocalDate lastDayOfMonth;
        LocalDate firstDayOfMonth = new LocalDate(year, month, 1);
        LocalDate date = this.calculateNthDayOfWeekFromFirstOfMonth(firstDayOfMonth);
        if (date.isAfter((ReadablePartial)(lastDayOfMonth = firstDayOfMonth.dayOfMonth().withMaximumValue()))) {
            return -1;
        }
        return date.getDayOfMonth();
    }

    private LocalDate calculateNthDayOfWeekFromFirstOfMonth(LocalDate firstDayOfMonth) {
        LocalDate date = firstDayOfMonth.withDayOfWeek(this.isoDayOfWeek);
        if (date.isBefore((ReadablePartial)firstDayOfMonth)) {
            return date.plusWeeks(this.nth);
        }
        return date.plusWeeks(this.nth - 1);
    }

    @Override
    protected void appendTo(StringBuilder sb) {
        sb.append(DayOfWeekConstantConverter.isoToName(this.isoDayOfWeek)).append('#').append(this.nth);
    }
}

