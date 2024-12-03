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

public class ByMinuteRule
extends AbstractDateExpansionRule {
    private final NumberList minuteList;

    public ByMinuteRule(NumberList minuteList, Recur.Frequency frequency) {
        super(frequency);
        this.minuteList = minuteList;
    }

    public ByMinuteRule(NumberList minuteList, Recur.Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        super(frequency, weekStartDay);
        this.minuteList = minuteList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (this.minuteList.isEmpty()) {
            return dates;
        }
        DateList minutelyDates = Dates.getDateListInstance(dates);
        for (Date date : dates) {
            if (EnumSet.of(Recur.Frequency.HOURLY, Recur.Frequency.DAILY, Recur.Frequency.WEEKLY, Recur.Frequency.MONTHLY, Recur.Frequency.YEARLY).contains((Object)this.getFrequency())) {
                minutelyDates.addAll((Collection<? extends Date>)new ExpansionFilter(minutelyDates.getType()).apply(date));
                continue;
            }
            Optional<Date> limit = new LimitFilter().apply(date);
            if (!limit.isPresent()) continue;
            minutelyDates.add(limit.get());
        }
        return minutelyDates;
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
            Calendar cal = ByMinuteRule.this.getCalendarInstance(date, true);
            ByMinuteRule.this.minuteList.forEach(minute -> {
                cal.set(12, (int)minute);
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
            Calendar cal = ByMinuteRule.this.getCalendarInstance(date, true);
            if (ByMinuteRule.this.minuteList.contains(cal.get(12))) {
                return Optional.of(date);
            }
            return Optional.empty();
        }
    }
}

