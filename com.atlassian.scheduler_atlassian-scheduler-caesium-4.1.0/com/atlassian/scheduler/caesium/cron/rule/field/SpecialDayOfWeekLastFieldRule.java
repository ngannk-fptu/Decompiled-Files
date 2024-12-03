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

public class SpecialDayOfWeekLastFieldRule
extends SpecialDayFieldRule {
    private static final long serialVersionUID = 4480059862309727633L;
    private final int isoDayOfWeek;

    public SpecialDayOfWeekLastFieldRule(int cronDayOfWeek) {
        Preconditions.checkArgument((cronDayOfWeek >= 1 && cronDayOfWeek <= 7 ? 1 : 0) != 0, (Object)"cronDayOfWeek must be in the range [1,7]");
        this.isoDayOfWeek = DayOfWeekConstantConverter.cronToIso(cronDayOfWeek);
    }

    @Override
    int calculateMatchingDay(int year, int month) {
        LocalDate lastDayOfMonth = new LocalDate(year, month, 1).dayOfMonth().withMaximumValue();
        LocalDate date = lastDayOfMonth.withDayOfWeek(this.isoDayOfWeek);
        if (date.isAfter((ReadablePartial)lastDayOfMonth)) {
            date = date.minusWeeks(1);
        }
        return date.getDayOfMonth();
    }

    @Override
    protected void appendTo(StringBuilder sb) {
        sb.append(DayOfWeekConstantConverter.isoToCron(this.isoDayOfWeek)).append('L');
    }
}

