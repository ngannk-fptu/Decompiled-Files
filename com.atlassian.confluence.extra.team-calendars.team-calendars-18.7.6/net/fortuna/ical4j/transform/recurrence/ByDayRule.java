/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform.recurrence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.WeekDayList;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.transform.recurrence.AbstractDateExpansionRule;
import net.fortuna.ical4j.util.Dates;

public class ByDayRule
extends AbstractDateExpansionRule {
    private final WeekDayList dayList;

    public ByDayRule(WeekDayList dayList, Recur.Frequency frequency) {
        super(frequency);
        this.dayList = dayList;
    }

    public ByDayRule(WeekDayList dayList, Recur.Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        super(frequency, weekStartDay);
        this.dayList = dayList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (this.dayList.isEmpty()) {
            return dates;
        }
        DateList weekDayDates = Dates.getDateListInstance(dates);
        Function<Date, List<Date>> transformer = null;
        switch (this.getFrequency()) {
            case WEEKLY: {
                transformer = new WeeklyExpansionFilter(dates.getType());
                break;
            }
            case MONTHLY: {
                transformer = new MonthlyExpansionFilter(dates.getType());
                break;
            }
            case YEARLY: {
                transformer = new YearlyExpansionFilter(dates.getType());
                break;
            }
            default: {
                transformer = new LimitFilter();
            }
        }
        for (Date date : dates) {
            List<Date> transformed = transformer.apply(date);
            ArrayList filtered = new ArrayList();
            this.dayList.forEach(day -> filtered.addAll(this.getOffsetDates(transformed.stream().filter(d -> this.getCalendarInstance((Date)d, true).get(7) == WeekDay.getCalendarDay(day)).collect(Collectors.toCollection(() -> Dates.getDateListInstance(weekDayDates))), day.getOffset())));
            weekDayDates.addAll(filtered);
        }
        return weekDayDates;
    }

    private List<Date> getOffsetDates(DateList dates, int offset) {
        if (offset == 0) {
            return dates;
        }
        DateList offsetDates = Dates.getDateListInstance(dates);
        int size = dates.size();
        if (offset < 0 && offset >= -size) {
            offsetDates.add(dates.get(size + offset));
        } else if (offset > 0 && offset <= size) {
            offsetDates.add(dates.get(offset - 1));
        }
        return offsetDates;
    }

    private class YearlyExpansionFilter
    implements Function<Date, List<Date>> {
        private final Value type;

        public YearlyExpansionFilter(Value type) {
            this.type = type;
        }

        @Override
        public List<Date> apply(Date date) {
            ArrayList<Date> retVal = new ArrayList<Date>();
            Calendar cal = ByDayRule.this.getCalendarInstance(date, true);
            int year = cal.get(1);
            cal.set(6, 1);
            while (cal.get(1) == year) {
                if (!ByDayRule.this.dayList.stream().map(weekDay -> WeekDay.getCalendarDay(weekDay)).filter(calDay -> cal.get(7) == calDay.intValue()).collect(Collectors.toList()).isEmpty()) {
                    retVal.add(Dates.getInstance(AbstractDateExpansionRule.getTime(date, cal), this.type));
                }
                cal.add(6, 1);
            }
            return retVal;
        }
    }

    private class MonthlyExpansionFilter
    implements Function<Date, List<Date>> {
        private final Value type;

        public MonthlyExpansionFilter(Value type) {
            this.type = type;
        }

        @Override
        public List<Date> apply(Date date) {
            ArrayList<Date> retVal = new ArrayList<Date>();
            Calendar cal = ByDayRule.this.getCalendarInstance(date, true);
            int month = cal.get(2);
            cal.set(5, 1);
            while (cal.get(2) == month) {
                if (!ByDayRule.this.dayList.stream().map(weekDay -> WeekDay.getCalendarDay(weekDay)).filter(calDay -> cal.get(7) == calDay.intValue()).collect(Collectors.toList()).isEmpty()) {
                    retVal.add(Dates.getInstance(AbstractDateExpansionRule.getTime(date, cal), this.type));
                }
                cal.add(5, 1);
            }
            return retVal;
        }
    }

    private class WeeklyExpansionFilter
    implements Function<Date, List<Date>> {
        private final Value type;

        public WeeklyExpansionFilter(Value type) {
            this.type = type;
        }

        @Override
        public List<Date> apply(Date date) {
            ArrayList<Date> retVal = new ArrayList<Date>();
            Calendar cal = ByDayRule.this.getCalendarInstance(date, true);
            int weekNo = cal.get(3);
            cal.set(7, cal.getFirstDayOfWeek());
            while (cal.get(3) == weekNo) {
                if (!ByDayRule.this.dayList.stream().map(weekDay -> WeekDay.getCalendarDay(weekDay)).filter(calDay -> cal.get(7) == calDay.intValue()).collect(Collectors.toList()).isEmpty()) {
                    retVal.add(Dates.getInstance(AbstractDateExpansionRule.getTime(date, cal), this.type));
                }
                cal.add(7, 1);
            }
            return retVal;
        }
    }

    private class LimitFilter
    implements Function<Date, List<Date>> {
        private LimitFilter() {
        }

        @Override
        public List<Date> apply(Date date) {
            Calendar cal = ByDayRule.this.getCalendarInstance(date, true);
            if (ByDayRule.this.dayList.contains(WeekDay.getWeekDay(cal))) {
                return Arrays.asList(date);
            }
            return Collections.emptyList();
        }
    }
}

