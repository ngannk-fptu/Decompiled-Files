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
import java.util.Calendar;
import java.util.Optional;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.transform.recurrence.AbstractDateExpansionRule;
import net.fortuna.ical4j.util.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByWeekNoRule
extends AbstractDateExpansionRule {
    private transient Logger log = LoggerFactory.getLogger(ByWeekNoRule.class);
    private final NumberList weekNoList;

    public ByWeekNoRule(NumberList weekNoList, Recur.Frequency frequency) {
        super(frequency);
        this.weekNoList = weekNoList;
    }

    public ByWeekNoRule(NumberList weekNoList, Recur.Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        super(frequency, weekStartDay);
        this.weekNoList = weekNoList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (this.weekNoList.isEmpty()) {
            return dates;
        }
        DateList weekNoDates = Dates.getDateListInstance(dates);
        Calendar initCal = this.getCalendarInstance(dates.get(0), true);
        for (Date date : dates) {
            int numWeeksInYear = initCal.getActualMaximum(3);
            for (Integer weekNo : this.weekNoList) {
                if (weekNo == 0 || weekNo < -53 || weekNo > 53) {
                    if (!this.log.isTraceEnabled()) continue;
                    this.log.trace("Invalid week of year: " + weekNo);
                    continue;
                }
                Calendar cal = this.getCalendarInstance(date, true);
                if (weekNo > 0) {
                    if (numWeeksInYear < weekNo) continue;
                    cal.set(3, weekNo);
                } else {
                    if (numWeeksInYear < -weekNo.intValue()) continue;
                    cal.set(3, numWeeksInYear);
                    cal.add(3, weekNo + 1);
                }
                weekNoDates.add(Dates.getInstance(ByWeekNoRule.getTime(date, cal), weekNoDates.getType()));
            }
        }
        return weekNoDates;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.log = LoggerFactory.getLogger(Recur.class);
    }
}

