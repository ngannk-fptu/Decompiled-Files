/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.scheduler.caesium.cron.rule.field;

import com.atlassian.scheduler.caesium.cron.rule.DateTimeTemplate;
import com.atlassian.scheduler.caesium.cron.rule.field.AbstractFieldRule;

abstract class SpecialDayFieldRule
extends AbstractFieldRule {
    private static final long serialVersionUID = -5909551168217552683L;

    SpecialDayFieldRule() {
        super(DateTimeTemplate.Field.DAY);
    }

    @Override
    public boolean matches(DateTimeTemplate dateTime) {
        return this.get(dateTime) == this.getMatchingDay(dateTime);
    }

    @Override
    public boolean first(DateTimeTemplate dateTime) {
        int day = this.getMatchingDay(dateTime);
        if (day == -1) {
            return false;
        }
        this.set(dateTime, day);
        return true;
    }

    @Override
    public boolean next(DateTimeTemplate dateTime) {
        int day = this.getMatchingDay(dateTime);
        if (day == -1 || this.get(dateTime) >= day) {
            return false;
        }
        this.set(dateTime, day);
        return true;
    }

    private int getMatchingDay(DateTimeTemplate dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonth();
        return this.calculateMatchingDay(year, month);
    }

    abstract int calculateMatchingDay(int var1, int var2);
}

