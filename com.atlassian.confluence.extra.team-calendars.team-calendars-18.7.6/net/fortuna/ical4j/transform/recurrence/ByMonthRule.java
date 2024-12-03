/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform.recurrence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.transform.recurrence.AbstractDateExpansionRule;
import net.fortuna.ical4j.util.Dates;

public class ByMonthRule
extends AbstractDateExpansionRule {
    private final NumberList monthList;

    public ByMonthRule(NumberList monthList, Recur.Frequency frequency) {
        this(monthList, frequency, Optional.empty());
    }

    public ByMonthRule(NumberList monthList, Recur.Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        super(frequency, weekStartDay);
        this.monthList = monthList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (this.monthList.isEmpty()) {
            return dates;
        }
        DateList monthlyDates = Dates.getDateListInstance(dates);
        for (Date date : dates) {
            if (this.getFrequency() == Recur.Frequency.YEARLY) {
                monthlyDates.addAll((Collection<? extends Date>)new ExpansionFilter(monthlyDates.getType()).apply(date));
                continue;
            }
            Optional<Date> limit = new LimitFilter().apply(date);
            if (!limit.isPresent()) continue;
            monthlyDates.add(limit.get());
        }
        return monthlyDates;
    }

    private class ExpansionFilter
    implements Function<Date, List<Date>> {
        private final Value type;

        public ExpansionFilter(Value type) {
            this.type = type;
        }

        @Override
        public List<Date> apply(Date date) {
            ArrayList<Date> retVal = new ArrayList<Date>();
            Calendar cal = ByMonthRule.this.getCalendarInstance(date, true);
            ByMonthRule.this.monthList.forEach(month -> {
                cal.roll(2, month - 1 - cal.get(2));
                retVal.add(Dates.getInstance(AbstractDateExpansionRule.getTime(date, cal), this.type));
            });
            return retVal;
        }
    }

    private class LimitFilter
    implements Function<Date, Optional<Date>> {
        private LimitFilter() {
        }

        @Override
        public Optional<Date> apply(Date date) {
            Calendar cal = ByMonthRule.this.getCalendarInstance(date, true);
            if (ByMonthRule.this.monthList.contains(cal.get(2) + 1)) {
                return Optional.of(date);
            }
            return Optional.empty();
        }
    }
}

