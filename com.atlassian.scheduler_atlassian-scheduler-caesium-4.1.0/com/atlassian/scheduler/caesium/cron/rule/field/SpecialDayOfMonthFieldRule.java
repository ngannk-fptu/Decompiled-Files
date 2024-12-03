/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.LocalDate
 */
package com.atlassian.scheduler.caesium.cron.rule.field;

import com.atlassian.scheduler.caesium.cron.rule.field.SpecialDayFieldRule;
import org.joda.time.LocalDate;

public class SpecialDayOfMonthFieldRule
extends SpecialDayFieldRule {
    private static final long serialVersionUID = -8821720295413352749L;
    private final int target;
    private final boolean nearestWeekday;

    public SpecialDayOfMonthFieldRule(int target, boolean nearestWeekday) {
        this.target = target;
        this.nearestWeekday = nearestWeekday;
    }

    @Override
    int calculateMatchingDay(int year, int month) {
        LocalDate firstOfMonth = new LocalDate(year, month, 1);
        int lastDay = firstOfMonth.dayOfMonth().getMaximumValue();
        int target = this.target;
        if (target <= 0) {
            return this.calculateRelativeToLastDay(firstOfMonth, lastDay, target);
        }
        if (target <= lastDay) {
            return this.applyNearestWeekdayRule(firstOfMonth, target, target);
        }
        return -1;
    }

    private int calculateRelativeToLastDay(LocalDate firstOfMonth, int lastDay, int offset) {
        int day = lastDay + offset;
        if (day > 0) {
            return this.applyNearestWeekdayRule(firstOfMonth, day, day);
        }
        if (this.nearestWeekday) {
            return SpecialDayOfMonthFieldRule.calculateNearestWeekday(firstOfMonth);
        }
        return -1;
    }

    private int applyNearestWeekdayRule(LocalDate firstOfMonth, int idealDay, int orElse) {
        if (this.nearestWeekday) {
            return SpecialDayOfMonthFieldRule.calculateNearestWeekday(firstOfMonth.withDayOfMonth(idealDay));
        }
        return orElse;
    }

    private static int calculateNearestWeekday(LocalDate idealDate) {
        switch (idealDate.getDayOfWeek()) {
            case 6: {
                return SpecialDayOfMonthFieldRule.calculateNearestWeekdayFromSaturday(idealDate);
            }
            case 7: {
                return SpecialDayOfMonthFieldRule.calculateNearestWeekdayFromSunday(idealDate);
            }
        }
        return idealDate.dayOfMonth().get();
    }

    private static int calculateNearestWeekdayFromSaturday(LocalDate idealDate) {
        int day = idealDate.getDayOfMonth();
        if (day == 1) {
            return 3;
        }
        return day - 1;
    }

    private static int calculateNearestWeekdayFromSunday(LocalDate idealDate) {
        int day = idealDate.getDayOfMonth();
        if (day == idealDate.dayOfMonth().getMaximumValue()) {
            return day - 2;
        }
        return day + 1;
    }

    @Override
    protected void appendTo(StringBuilder sb) {
        if (this.target > 0) {
            sb.append(this.target);
        } else {
            sb.append('L');
            if (this.target < 0) {
                sb.append(this.target);
            }
        }
        if (this.nearestWeekday) {
            sb.append('W');
        }
    }
}

