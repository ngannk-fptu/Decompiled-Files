/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform.recurrence;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Optional;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Iso8601;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.util.Dates;

public abstract class AbstractDateExpansionRule
implements Transformer<DateList>,
Serializable {
    private final Recur.Frequency frequency;
    private final int calendarWeekStartDay;

    public AbstractDateExpansionRule(Recur.Frequency frequency) {
        this(frequency, Optional.of(WeekDay.Day.MO));
    }

    public AbstractDateExpansionRule(Recur.Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        this.frequency = frequency;
        this.calendarWeekStartDay = WeekDay.getCalendarDay(WeekDay.getWeekDay(weekStartDay.orElse(WeekDay.Day.MO)));
    }

    protected Recur.Frequency getFrequency() {
        return this.frequency;
    }

    protected Calendar getCalendarInstance(Date date, boolean lenient) {
        Calendar cal = Dates.getCalendarInstance(date);
        cal.setMinimalDaysInFirstWeek(4);
        cal.setFirstDayOfWeek(this.calendarWeekStartDay);
        cal.setLenient(lenient);
        cal.setTime(date);
        return cal;
    }

    protected static Date getTime(Date referenceDate, Calendar cal) {
        DateTime zonedDate = new DateTime(referenceDate);
        ((Iso8601)zonedDate).setTime(cal.getTime().getTime());
        return zonedDate;
    }
}

