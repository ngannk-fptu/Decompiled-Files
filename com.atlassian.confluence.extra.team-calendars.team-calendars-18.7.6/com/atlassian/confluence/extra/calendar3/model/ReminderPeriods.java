/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.fugue.Option;

public enum ReminderPeriods {
    FIVE_MINS(5),
    TEN_MINS(10),
    TWENTY_MINS(20),
    THIRTY_MINS(30),
    ONE_HOUR(60),
    TWO_HOURS(120),
    FOUR_HOURS(240),
    EIGHT_HOURS(480),
    ONE_DAY(1440),
    TWO_DAYS(2880),
    THREE_DAYS(4320),
    ONE_WEEK(10080),
    TWO_WEEKS(20160),
    THREE_WEEKS(30240),
    FOUR_WEEKS(40320);

    private long milisecond;
    private int mins;

    private ReminderPeriods(int mins) {
        this.milisecond = (long)mins * 60L * 1000L;
        this.mins = mins;
    }

    public int getMins() {
        return this.mins;
    }

    public long getMilisecond() {
        return this.milisecond;
    }

    public static Option<ReminderPeriods> toReminderPeriod(long periodInMillisecond) {
        int mins = Math.round(periodInMillisecond / 60000L);
        for (ReminderPeriods period : ReminderPeriods.values()) {
            if (period.getMins() != mins) continue;
            return Option.some((Object)((Object)period));
        }
        return Option.none();
    }

    public static Option<ReminderPeriods> toReminderPeriod(int periodInMins) {
        for (ReminderPeriods period : ReminderPeriods.values()) {
            if (period.getMins() != periodInMins) continue;
            return Option.some((Object)((Object)period));
        }
        return Option.none();
    }
}

