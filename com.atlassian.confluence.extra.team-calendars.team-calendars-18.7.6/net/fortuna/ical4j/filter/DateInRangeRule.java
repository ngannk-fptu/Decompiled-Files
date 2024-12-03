/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.filter;

import java.util.Date;
import java.util.function.Predicate;
import net.fortuna.ical4j.model.DateRange;

public class DateInRangeRule
implements Predicate<Date> {
    private final DateRange range;
    private final int inclusiveMask;

    public DateInRangeRule(DateRange range, int inclusiveMask) {
        this.range = range;
        this.inclusiveMask = inclusiveMask;
    }

    @Override
    public boolean test(Date date) {
        return this.range.includes(date, this.inclusiveMask);
    }
}

