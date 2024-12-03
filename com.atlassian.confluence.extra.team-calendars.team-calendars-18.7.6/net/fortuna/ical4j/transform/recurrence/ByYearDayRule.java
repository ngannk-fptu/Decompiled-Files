/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.transform.recurrence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByYearDayRule
extends AbstractDateExpansionRule {
    private transient Logger log = LoggerFactory.getLogger(ByYearDayRule.class);
    private final NumberList yearDayList;

    public ByYearDayRule(NumberList yearDayList, Recur.Frequency frequency) {
        super(frequency);
        this.yearDayList = yearDayList;
    }

    public ByYearDayRule(NumberList yearDayList, Recur.Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        super(frequency, weekStartDay);
        this.yearDayList = yearDayList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (this.yearDayList.isEmpty()) {
            return dates;
        }
        DateList yearDayDates = Dates.getDateListInstance(dates);
        for (Date date : dates) {
            if (this.getFrequency() == Recur.Frequency.YEARLY) {
                yearDayDates.addAll((Collection<? extends Date>)new ExpansionFilter(yearDayDates.getType()).apply(date));
                continue;
            }
            Optional<Date> limit = new LimitFilter().apply(date);
            if (!limit.isPresent()) continue;
            yearDayDates.add(limit.get());
        }
        return yearDayDates;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.log = LoggerFactory.getLogger(Recur.class);
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
            Calendar cal = ByYearDayRule.this.getCalendarInstance(date, false);
            Iterator iterator = ByYearDayRule.this.yearDayList.iterator();
            while (iterator.hasNext()) {
                int yearDay = (Integer)iterator.next();
                if (yearDay == 0 || yearDay < -366 || yearDay > 366) {
                    if (!ByYearDayRule.this.log.isTraceEnabled()) continue;
                    ByYearDayRule.this.log.trace("Invalid day of year: " + yearDay);
                    continue;
                }
                int numDaysInYear = cal.getActualMaximum(6);
                if (yearDay > 0) {
                    if (numDaysInYear < yearDay) continue;
                    cal.set(6, yearDay);
                } else {
                    if (numDaysInYear < -yearDay) continue;
                    cal.set(6, numDaysInYear);
                    cal.add(6, yearDay + 1);
                }
                retVal.add(Dates.getInstance(AbstractDateExpansionRule.getTime(date, cal), this.type));
            }
            return retVal;
        }
    }

    private class LimitFilter
    implements Function<Date, Optional<Date>> {
        private LimitFilter() {
        }

        @Override
        public Optional<Date> apply(Date date) {
            Calendar cal = ByYearDayRule.this.getCalendarInstance(date, true);
            if (ByYearDayRule.this.yearDayList.contains(cal.get(6))) {
                return Optional.of(date);
            }
            return Optional.empty();
        }
    }
}

