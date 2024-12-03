/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.date;

import org.jfree.date.AnnualDateRule;
import org.jfree.date.DayAndMonthRule;
import org.jfree.date.SerialDate;

public class RelativeDayOfWeekRule
extends AnnualDateRule {
    private AnnualDateRule subrule;
    private int dayOfWeek;
    private int relative;

    public RelativeDayOfWeekRule() {
        this(new DayAndMonthRule(), 2, 1);
    }

    public RelativeDayOfWeekRule(AnnualDateRule subrule, int dayOfWeek, int relative) {
        this.subrule = subrule;
        this.dayOfWeek = dayOfWeek;
        this.relative = relative;
    }

    public AnnualDateRule getSubrule() {
        return this.subrule;
    }

    public void setSubrule(AnnualDateRule subrule) {
        this.subrule = subrule;
    }

    public int getDayOfWeek() {
        return this.dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getRelative() {
        return this.relative;
    }

    public void setRelative(int relative) {
        this.relative = relative;
    }

    public Object clone() throws CloneNotSupportedException {
        RelativeDayOfWeekRule duplicate = (RelativeDayOfWeekRule)super.clone();
        duplicate.subrule = (AnnualDateRule)duplicate.getSubrule().clone();
        return duplicate;
    }

    public SerialDate getDate(int year) {
        if (year < 1900 || year > 9999) {
            throw new IllegalArgumentException("RelativeDayOfWeekRule.getDate(): year outside valid range.");
        }
        SerialDate result = null;
        SerialDate base = this.subrule.getDate(year);
        if (base != null) {
            switch (this.relative) {
                case -1: {
                    result = SerialDate.getPreviousDayOfWeek(this.dayOfWeek, base);
                    break;
                }
                case 0: {
                    result = SerialDate.getNearestDayOfWeek(this.dayOfWeek, base);
                    break;
                }
                case 1: {
                    result = SerialDate.getFollowingDayOfWeek(this.dayOfWeek, base);
                    break;
                }
            }
        }
        return result;
    }
}

