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

public class BySecondRule
extends AbstractDateExpansionRule {
    private final NumberList secondList;

    public BySecondRule(NumberList secondList, Recur.Frequency frequency) {
        super(frequency);
        this.secondList = secondList;
    }

    public BySecondRule(NumberList secondList, Recur.Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        super(frequency, weekStartDay);
        this.secondList = secondList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (this.secondList.isEmpty()) {
            return dates;
        }
        DateList secondlyDates = Dates.getDateListInstance(dates);
        for (Date date : dates) {
            if (this.getFrequency() == Recur.Frequency.SECONDLY) {
                Optional<Date> limit = new LimitFilter().apply(date);
                if (!limit.isPresent()) continue;
                secondlyDates.add(limit.get());
                continue;
            }
            secondlyDates.addAll((Collection<? extends Date>)new ExpansionFilter(secondlyDates.getType()).apply(date));
        }
        return secondlyDates;
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
            Calendar cal = BySecondRule.this.getCalendarInstance(date, true);
            BySecondRule.this.secondList.forEach(second -> {
                cal.set(13, (int)second);
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
            Calendar cal = BySecondRule.this.getCalendarInstance(date, true);
            if (BySecondRule.this.secondList.contains(cal.get(13))) {
                return Optional.of(date);
            }
            return Optional.empty();
        }
    }
}

