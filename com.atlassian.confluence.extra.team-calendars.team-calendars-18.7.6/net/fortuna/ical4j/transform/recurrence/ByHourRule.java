/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform.recurrence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumSet;
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

public class ByHourRule
extends AbstractDateExpansionRule {
    private final NumberList hourList;

    public ByHourRule(NumberList hourList, Recur.Frequency frequency) {
        super(frequency);
        this.hourList = hourList;
    }

    public ByHourRule(NumberList hourList, Recur.Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        super(frequency, weekStartDay);
        this.hourList = hourList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (this.hourList.isEmpty()) {
            return dates;
        }
        DateList hourlyDates = Dates.getDateListInstance(dates);
        for (Date date : dates) {
            if (EnumSet.of(Recur.Frequency.DAILY, Recur.Frequency.WEEKLY, Recur.Frequency.MONTHLY, Recur.Frequency.YEARLY).contains((Object)this.getFrequency())) {
                hourlyDates.addAll((Collection<? extends Date>)new ExpansionFilter(hourlyDates.getType()).apply(date));
                continue;
            }
            Optional<Date> limit = new LimitFilter().apply(date);
            if (!limit.isPresent()) continue;
            hourlyDates.add(limit.get());
        }
        return hourlyDates;
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
            Calendar cal = ByHourRule.this.getCalendarInstance(date, true);
            ByHourRule.this.hourList.forEach(hour -> {
                cal.set(11, (int)hour);
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
            Calendar cal = ByHourRule.this.getCalendarInstance(date, true);
            if (ByHourRule.this.hourList.contains(cal.get(11))) {
                return Optional.of(date);
            }
            return Optional.empty();
        }
    }
}

