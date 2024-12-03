/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.date;

import org.jfree.date.AnnualDateRule;
import org.jfree.date.SerialDate;

public class DayAndMonthRule
extends AnnualDateRule {
    private int dayOfMonth;
    private int month;

    public DayAndMonthRule() {
        this(1, 1);
    }

    public DayAndMonthRule(int dayOfMonth, int month) {
        this.setMonth(month);
        this.setDayOfMonth(dayOfMonth);
    }

    public int getDayOfMonth() {
        return this.dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        if (dayOfMonth < 1 || dayOfMonth > SerialDate.LAST_DAY_OF_MONTH[this.month]) {
            throw new IllegalArgumentException("DayAndMonthRule(): dayOfMonth outside valid range.");
        }
        this.dayOfMonth = dayOfMonth;
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int month) {
        if (!SerialDate.isValidMonthCode(month)) {
            throw new IllegalArgumentException("DayAndMonthRule(): month code not valid.");
        }
        this.month = month;
    }

    public SerialDate getDate(int yyyy) {
        return SerialDate.createInstance(this.dayOfMonth, this.month, yyyy);
    }
}

