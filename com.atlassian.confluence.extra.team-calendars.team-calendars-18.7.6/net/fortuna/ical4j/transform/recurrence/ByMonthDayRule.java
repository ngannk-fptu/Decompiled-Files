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
import java.util.EnumSet;
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

public class ByMonthDayRule
extends AbstractDateExpansionRule {
    private transient Logger log = LoggerFactory.getLogger(ByMonthDayRule.class);
    private final NumberList monthDayList;

    public ByMonthDayRule(NumberList monthDayList, Recur.Frequency frequency) {
        super(frequency);
        this.monthDayList = monthDayList;
    }

    public ByMonthDayRule(NumberList monthDayList, Recur.Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        super(frequency, weekStartDay);
        this.monthDayList = monthDayList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (this.monthDayList.isEmpty()) {
            return dates;
        }
        DateList monthDayDates = Dates.getDateListInstance(dates);
        for (Date date : dates) {
            if (EnumSet.of(Recur.Frequency.MONTHLY, Recur.Frequency.YEARLY).contains((Object)this.getFrequency())) {
                monthDayDates.addAll((Collection<? extends Date>)new ExpansionFilter(monthDayDates.getType()).apply(date));
                continue;
            }
            Optional<Date> limit = new LimitFilter().apply(date);
            if (!limit.isPresent()) continue;
            monthDayDates.add(limit.get());
        }
        return monthDayDates;
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
            Calendar cal = ByMonthDayRule.this.getCalendarInstance(date, false);
            Iterator iterator = ByMonthDayRule.this.monthDayList.iterator();
            while (iterator.hasNext()) {
                int monthDay = (Integer)iterator.next();
                if (monthDay == 0 || monthDay < -31 || monthDay > 31) {
                    if (!ByMonthDayRule.this.log.isTraceEnabled()) continue;
                    ByMonthDayRule.this.log.trace("Invalid day of month: " + monthDay);
                    continue;
                }
                int numDaysInMonth = cal.getActualMaximum(5);
                if (monthDay > 0) {
                    if (numDaysInMonth < monthDay) continue;
                    cal.set(5, monthDay);
                } else {
                    if (numDaysInMonth < -monthDay) continue;
                    cal.set(5, numDaysInMonth);
                    cal.add(5, monthDay + 1);
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
            Calendar cal = ByMonthDayRule.this.getCalendarInstance(date, true);
            if (ByMonthDayRule.this.monthDayList.contains(cal.get(5))) {
                return Optional.of(date);
            }
            return Optional.empty();
        }
    }
}

