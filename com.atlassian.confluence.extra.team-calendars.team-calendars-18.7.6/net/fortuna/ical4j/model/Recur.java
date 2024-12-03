/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.WeekDayList;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.transform.recurrence.ByDayRule;
import net.fortuna.ical4j.transform.recurrence.ByHourRule;
import net.fortuna.ical4j.transform.recurrence.ByMinuteRule;
import net.fortuna.ical4j.transform.recurrence.ByMonthDayRule;
import net.fortuna.ical4j.transform.recurrence.ByMonthRule;
import net.fortuna.ical4j.transform.recurrence.BySecondRule;
import net.fortuna.ical4j.transform.recurrence.BySetPosRule;
import net.fortuna.ical4j.transform.recurrence.ByWeekNoRule;
import net.fortuna.ical4j.transform.recurrence.ByYearDayRule;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Configurator;
import net.fortuna.ical4j.util.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Recur
implements Serializable {
    private static final long serialVersionUID = -7333226591784095142L;
    private static final String FREQ = "FREQ";
    private static final String UNTIL = "UNTIL";
    private static final String COUNT = "COUNT";
    private static final String INTERVAL = "INTERVAL";
    private static final String BYSECOND = "BYSECOND";
    private static final String BYMINUTE = "BYMINUTE";
    private static final String BYHOUR = "BYHOUR";
    private static final String BYDAY = "BYDAY";
    private static final String BYMONTHDAY = "BYMONTHDAY";
    private static final String BYYEARDAY = "BYYEARDAY";
    private static final String BYWEEKNO = "BYWEEKNO";
    private static final String BYMONTH = "BYMONTH";
    private static final String BYSETPOS = "BYSETPOS";
    private static final String WKST = "WKST";
    @Deprecated
    public static final String SECONDLY = "SECONDLY";
    @Deprecated
    public static final String MINUTELY = "MINUTELY";
    @Deprecated
    public static final String HOURLY = "HOURLY";
    @Deprecated
    public static final String DAILY = "DAILY";
    @Deprecated
    public static final String WEEKLY = "WEEKLY";
    @Deprecated
    public static final String MONTHLY = "MONTHLY";
    @Deprecated
    public static final String YEARLY = "YEARLY";
    public static final String KEY_MAX_INCREMENT_COUNT = "net.fortuna.ical4j.recur.maxincrementcount";
    private static int maxIncrementCount = Configurator.getIntProperty("net.fortuna.ical4j.recur.maxincrementcount").orElse(1000);
    private transient Logger log = LoggerFactory.getLogger(Recur.class);
    private Frequency frequency;
    private Date until;
    private Integer count;
    private Integer interval;
    private NumberList secondList;
    private NumberList minuteList;
    private NumberList hourList;
    private WeekDayList dayList;
    private NumberList monthDayList;
    private NumberList yearDayList;
    private NumberList weekNoList;
    private NumberList monthList;
    private NumberList setPosList;
    private Map<String, Transformer<DateList>> transformers;
    private WeekDay.Day weekStartDay;
    private int calendarWeekStartDay = 2;
    private Map<String, String> experimentalValues = new HashMap<String, String>();
    private int calIncField;

    private Recur() {
        this.initTransformers();
    }

    public Recur(String aValue) throws ParseException {
        Iterator<String> tokens = Arrays.asList(aValue.split("[;=]")).iterator();
        while (tokens.hasNext()) {
            String token = tokens.next();
            if (FREQ.equals(token)) {
                this.frequency = Frequency.valueOf(this.nextToken(tokens, token));
                continue;
            }
            if (UNTIL.equals(token)) {
                String untilString = this.nextToken(tokens, token);
                if (untilString != null && untilString.contains("T")) {
                    this.until = new DateTime(untilString);
                    ((DateTime)this.until).setUtc(true);
                    continue;
                }
                this.until = new Date(untilString);
                continue;
            }
            if (COUNT.equals(token)) {
                this.count = Integer.parseInt(this.nextToken(tokens, token));
                continue;
            }
            if (INTERVAL.equals(token)) {
                this.interval = Integer.parseInt(this.nextToken(tokens, token));
                continue;
            }
            if (BYSECOND.equals(token)) {
                this.secondList = new NumberList(this.nextToken(tokens, token), 0, 59, false);
                continue;
            }
            if (BYMINUTE.equals(token)) {
                this.minuteList = new NumberList(this.nextToken(tokens, token), 0, 59, false);
                continue;
            }
            if (BYHOUR.equals(token)) {
                this.hourList = new NumberList(this.nextToken(tokens, token), 0, 23, false);
                continue;
            }
            if (BYDAY.equals(token)) {
                this.dayList = new WeekDayList(this.nextToken(tokens, token));
                continue;
            }
            if (BYMONTHDAY.equals(token)) {
                this.monthDayList = new NumberList(this.nextToken(tokens, token), 1, 31, true);
                continue;
            }
            if (BYYEARDAY.equals(token)) {
                this.yearDayList = new NumberList(this.nextToken(tokens, token), 1, 366, true);
                continue;
            }
            if (BYWEEKNO.equals(token)) {
                this.weekNoList = new NumberList(this.nextToken(tokens, token), 1, 53, true);
                continue;
            }
            if (BYMONTH.equals(token)) {
                this.monthList = new NumberList(this.nextToken(tokens, token), 1, 12, false);
                continue;
            }
            if (BYSETPOS.equals(token)) {
                this.setPosList = new NumberList(this.nextToken(tokens, token), 1, 366, true);
                continue;
            }
            if (WKST.equals(token)) {
                this.weekStartDay = WeekDay.Day.valueOf(this.nextToken(tokens, token));
                this.calendarWeekStartDay = WeekDay.getCalendarDay(WeekDay.getWeekDay(this.weekStartDay));
                continue;
            }
            if (CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed")) {
                this.experimentalValues.put(token, this.nextToken(tokens, token));
                continue;
            }
            throw new IllegalArgumentException(String.format("Invalid recurrence rule part: %s=%s", token, this.nextToken(tokens, token)));
        }
        this.validateFrequency();
        this.initTransformers();
    }

    private String nextToken(Iterator<String> tokens, String lastToken) {
        try {
            return tokens.next();
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Missing expected token, last token: " + lastToken);
        }
    }

    @Deprecated
    public Recur(String frequency, Date until) {
        this(Frequency.valueOf(frequency), until);
    }

    public Recur(Frequency frequency, Date until) {
        this.frequency = frequency;
        this.until = until;
        this.validateFrequency();
        this.initTransformers();
    }

    @Deprecated
    public Recur(String frequency, int count) {
        this(Frequency.valueOf(frequency), count);
    }

    public Recur(Frequency frequency, int count) {
        this.frequency = frequency;
        this.count = count;
        this.validateFrequency();
        this.initTransformers();
    }

    private void initTransformers() {
        this.transformers = new HashMap<String, Transformer<DateList>>();
        if (this.secondList != null) {
            this.transformers.put(BYSECOND, new BySecondRule(this.secondList, this.frequency, Optional.ofNullable(this.weekStartDay)));
        } else {
            this.secondList = new NumberList(0, 59, false);
        }
        if (this.minuteList != null) {
            this.transformers.put(BYMINUTE, new ByMinuteRule(this.minuteList, this.frequency, Optional.ofNullable(this.weekStartDay)));
        } else {
            this.minuteList = new NumberList(0, 59, false);
        }
        if (this.hourList != null) {
            this.transformers.put(BYHOUR, new ByHourRule(this.hourList, this.frequency, Optional.ofNullable(this.weekStartDay)));
        } else {
            this.hourList = new NumberList(0, 23, false);
        }
        if (this.monthDayList != null) {
            this.transformers.put(BYMONTHDAY, new ByMonthDayRule(this.monthDayList, this.frequency, Optional.ofNullable(this.weekStartDay)));
        } else {
            this.monthDayList = new NumberList(1, 31, true);
        }
        if (this.yearDayList != null) {
            this.transformers.put(BYYEARDAY, new ByYearDayRule(this.yearDayList, this.frequency, Optional.ofNullable(this.weekStartDay)));
        } else {
            this.yearDayList = new NumberList(1, 366, true);
        }
        if (this.weekNoList != null) {
            this.transformers.put(BYWEEKNO, new ByWeekNoRule(this.weekNoList, this.frequency, Optional.ofNullable(this.weekStartDay)));
        } else {
            this.weekNoList = new NumberList(1, 53, true);
        }
        if (this.monthList != null) {
            this.transformers.put(BYMONTH, new ByMonthRule(this.monthList, this.frequency, Optional.ofNullable(this.weekStartDay)));
        } else {
            this.monthList = new NumberList(1, 12, false);
        }
        if (this.dayList != null) {
            this.transformers.put(BYDAY, new ByDayRule(this.dayList, this.deriveFilterType(), Optional.ofNullable(this.weekStartDay)));
        } else {
            this.dayList = new WeekDayList();
        }
        if (this.setPosList != null) {
            this.transformers.put(BYSETPOS, new BySetPosRule(this.setPosList));
        } else {
            this.setPosList = new NumberList(1, 366, true);
        }
    }

    private Frequency deriveFilterType() {
        if (this.frequency == Frequency.DAILY || !this.getYearDayList().isEmpty() || !this.getMonthDayList().isEmpty()) {
            return Frequency.DAILY;
        }
        if (this.frequency == Frequency.WEEKLY || !this.getWeekNoList().isEmpty()) {
            return Frequency.WEEKLY;
        }
        if (this.frequency == Frequency.MONTHLY || !this.getMonthList().isEmpty()) {
            return Frequency.MONTHLY;
        }
        if (this.frequency == Frequency.YEARLY) {
            return Frequency.YEARLY;
        }
        return null;
    }

    public final WeekDayList getDayList() {
        return this.dayList;
    }

    public final NumberList getHourList() {
        return this.hourList;
    }

    public final NumberList getMinuteList() {
        return this.minuteList;
    }

    public final NumberList getMonthDayList() {
        return this.monthDayList;
    }

    public final NumberList getMonthList() {
        return this.monthList;
    }

    public final NumberList getSecondList() {
        return this.secondList;
    }

    public final NumberList getSetPosList() {
        return this.setPosList;
    }

    public final NumberList getWeekNoList() {
        return this.weekNoList;
    }

    public final NumberList getYearDayList() {
        return this.yearDayList;
    }

    public final int getCount() {
        return Optional.ofNullable(this.count).orElse(-1);
    }

    public final Map<String, String> getExperimentalValues() {
        return this.experimentalValues;
    }

    public final Frequency getFrequency() {
        return this.frequency;
    }

    public final int getInterval() {
        return Optional.ofNullable(this.interval).orElse(-1);
    }

    public final Date getUntil() {
        return this.until;
    }

    public final WeekDay.Day getWeekStartDay() {
        return this.weekStartDay;
    }

    @Deprecated
    public final void setWeekStartDay(WeekDay.Day weekStartDay) {
        this.weekStartDay = weekStartDay;
        if (weekStartDay != null) {
            this.calendarWeekStartDay = WeekDay.getCalendarDay(WeekDay.getWeekDay(weekStartDay));
        }
    }

    public final String toString() {
        StringBuilder b = new StringBuilder();
        b.append(FREQ);
        b.append('=');
        b.append((Object)this.frequency);
        if (this.weekStartDay != null) {
            b.append(';');
            b.append(WKST);
            b.append('=');
            b.append((Object)this.weekStartDay);
        }
        if (this.until != null) {
            b.append(';');
            b.append(UNTIL);
            b.append('=');
            b.append(this.until);
        }
        if (this.count != null) {
            b.append(';');
            b.append(COUNT);
            b.append('=');
            b.append(this.count);
        }
        if (this.interval != null) {
            b.append(';');
            b.append(INTERVAL);
            b.append('=');
            b.append(this.interval);
        }
        if (!this.monthList.isEmpty()) {
            b.append(';');
            b.append(BYMONTH);
            b.append('=');
            b.append(this.monthList);
        }
        if (!this.weekNoList.isEmpty()) {
            b.append(';');
            b.append(BYWEEKNO);
            b.append('=');
            b.append(this.weekNoList);
        }
        if (!this.yearDayList.isEmpty()) {
            b.append(';');
            b.append(BYYEARDAY);
            b.append('=');
            b.append(this.yearDayList);
        }
        if (!this.monthDayList.isEmpty()) {
            b.append(';');
            b.append(BYMONTHDAY);
            b.append('=');
            b.append(this.monthDayList);
        }
        if (!this.dayList.isEmpty()) {
            b.append(';');
            b.append(BYDAY);
            b.append('=');
            b.append(this.dayList);
        }
        if (!this.hourList.isEmpty()) {
            b.append(';');
            b.append(BYHOUR);
            b.append('=');
            b.append(this.hourList);
        }
        if (!this.minuteList.isEmpty()) {
            b.append(';');
            b.append(BYMINUTE);
            b.append('=');
            b.append(this.minuteList);
        }
        if (!this.secondList.isEmpty()) {
            b.append(';');
            b.append(BYSECOND);
            b.append('=');
            b.append(this.secondList);
        }
        if (!this.setPosList.isEmpty()) {
            b.append(';');
            b.append(BYSETPOS);
            b.append('=');
            b.append(this.setPosList);
        }
        return b.toString();
    }

    public final DateList getDates(Date periodStart, Date periodEnd, Value value) {
        return this.getDates(periodStart, periodStart, periodEnd, value, -1);
    }

    public final DateList getDates(Date seed, Period period, Value value) {
        return this.getDates(seed, period.getStart(), period.getEnd(), value, -1);
    }

    public final DateList getDates(Date seed, Date periodStart, Date periodEnd, Value value) {
        return this.getDates(seed, periodStart, periodEnd, value, -1);
    }

    public final DateList getDates(Date seed, Date periodStart, Date periodEnd, Value value, int maxCount) {
        DateList dates = new DateList(value);
        if (seed instanceof DateTime) {
            if (((DateTime)seed).isUtc()) {
                dates.setUtc(true);
            } else {
                dates.setTimeZone(((DateTime)seed).getTimeZone());
            }
        }
        Calendar cal = this.getCalendarInstance(seed, true);
        Calendar rootSeed = (Calendar)cal.clone();
        if (this.count == null) {
            Calendar seededCal = (Calendar)cal.clone();
            while (seededCal.getTime().before(periodStart)) {
                cal.setTime(seededCal.getTime());
                this.increment(seededCal);
            }
        }
        HashSet<java.util.Date> invalidCandidates = new HashSet<java.util.Date>();
        int noCandidateIncrementCount = 0;
        java.util.Date candidate = null;
        while (maxCount < 0 || dates.size() < maxCount) {
            DateList candidates;
            Date candidateSeed = Dates.getInstance(cal.getTime(), value);
            if (this.getUntil() != null && candidate != null && candidate.after(this.getUntil()) || periodEnd != null && candidate != null && candidate.after(periodEnd) || this.getCount() >= 1 && dates.size() + invalidCandidates.size() >= this.getCount()) break;
            if (candidateSeed instanceof DateTime) {
                if (dates.isUtc()) {
                    ((DateTime)candidateSeed).setUtc(true);
                } else {
                    ((DateTime)candidateSeed).setTimeZone(dates.getTimeZone());
                }
            }
            if (!(candidates = this.getCandidates(rootSeed, candidateSeed, value)).isEmpty()) {
                noCandidateIncrementCount = 0;
                Collections.sort(candidates);
                for (Date candidate1 : candidates) {
                    candidate = candidate1;
                    if (candidate.before(seed)) continue;
                    if (candidate.before(periodStart) || candidate.after(periodEnd)) {
                        invalidCandidates.add(candidate);
                        continue;
                    }
                    if (this.getCount() < 1 || dates.size() + invalidCandidates.size() < this.getCount()) {
                        if (candidate.before(periodStart) || candidate.after(periodEnd) || this.getUntil() != null && candidate.after(this.getUntil())) continue;
                        dates.add((Date)candidate);
                        continue;
                    }
                    break;
                }
            } else if (maxIncrementCount > 0 && ++noCandidateIncrementCount > maxIncrementCount) break;
            this.increment(cal);
        }
        Collections.sort(dates);
        return dates;
    }

    public final Date getNextDate(Date seed, Date startDate) {
        Calendar cal = this.getCalendarInstance(seed, true);
        Calendar rootSeed = (Calendar)cal.clone();
        if (this.count == null) {
            Calendar seededCal = (Calendar)cal.clone();
            while (seededCal.getTime().before(startDate)) {
                cal.setTime(seededCal.getTime());
                this.increment(seededCal);
            }
        }
        int invalidCandidateCount = 0;
        int noCandidateIncrementCount = 0;
        java.util.Date candidate = null;
        Value value = seed instanceof DateTime ? Value.DATE_TIME : Value.DATE;
        while (true) {
            DateList candidates;
            Date candidateSeed = Dates.getInstance(cal.getTime(), value);
            if (this.getUntil() != null && candidate != null && candidate.after(this.getUntil()) || this.getCount() > 0 && invalidCandidateCount >= this.getCount()) break;
            if (Value.DATE_TIME.equals(value)) {
                if (((DateTime)seed).isUtc()) {
                    ((DateTime)candidateSeed).setUtc(true);
                } else {
                    ((DateTime)candidateSeed).setTimeZone(((DateTime)seed).getTimeZone());
                }
            }
            if (!(candidates = this.getCandidates(rootSeed, candidateSeed, value)).isEmpty()) {
                noCandidateIncrementCount = 0;
                Collections.sort(candidates);
                for (Date candidate1 : candidates) {
                    candidate = candidate1;
                    if (candidate.before(seed)) continue;
                    if (!candidate.after(startDate)) {
                        ++invalidCandidateCount;
                        continue;
                    }
                    if (this.getCount() <= 0 || invalidCandidateCount < this.getCount()) {
                        if (this.getUntil() != null && candidate.after(this.getUntil())) continue;
                        return candidate;
                    }
                    break;
                }
            } else if (maxIncrementCount > 0 && ++noCandidateIncrementCount > maxIncrementCount) break;
            this.increment(cal);
        }
        return null;
    }

    private void increment(Calendar cal) {
        int calInterval = this.getInterval() >= 1 ? this.getInterval() : 1;
        cal.add(this.calIncField, calInterval);
    }

    private DateList getCandidates(Calendar rootSeed, Date date, Value value) {
        DateList dates = new DateList(value);
        if (date instanceof DateTime) {
            if (((DateTime)date).isUtc()) {
                dates.setUtc(true);
            } else {
                dates.setTimeZone(((DateTime)date).getTimeZone());
            }
        }
        dates.add(date);
        if (this.transformers.get(BYMONTH) != null) {
            dates = this.transformers.get(BYMONTH).transform(dates);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Dates after BYMONTH processing: " + dates);
            }
        }
        if (this.transformers.get(BYWEEKNO) != null) {
            dates = this.transformers.get(BYWEEKNO).transform(dates);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Dates after BYWEEKNO processing: " + dates);
            }
        }
        if (this.transformers.get(BYYEARDAY) != null) {
            dates = this.transformers.get(BYYEARDAY).transform(dates);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Dates after BYYEARDAY processing: " + dates);
            }
        }
        if (this.transformers.get(BYMONTHDAY) != null) {
            dates = this.transformers.get(BYMONTHDAY).transform(dates);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Dates after BYMONTHDAY processing: " + dates);
            }
        } else if (this.frequency == Frequency.MONTHLY && this.dayList.isEmpty() || this.frequency == Frequency.YEARLY && this.yearDayList.isEmpty() && this.weekNoList.isEmpty() && this.dayList.isEmpty()) {
            NumberList implicitMonthDayList = new NumberList();
            implicitMonthDayList.add(rootSeed.get(5));
            ByMonthDayRule implicitRule = new ByMonthDayRule(implicitMonthDayList, this.frequency, Optional.ofNullable(this.weekStartDay));
            dates = implicitRule.transform(dates);
        }
        if (this.transformers.get(BYDAY) != null) {
            dates = this.transformers.get(BYDAY).transform(dates);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Dates after BYDAY processing: " + dates);
            }
        } else if (this.frequency == Frequency.WEEKLY || this.frequency == Frequency.YEARLY && this.yearDayList.isEmpty() && !this.weekNoList.isEmpty() && this.monthDayList.isEmpty()) {
            ByDayRule implicitRule = new ByDayRule(new WeekDayList(WeekDay.getWeekDay(rootSeed)), this.deriveFilterType(), Optional.ofNullable(this.weekStartDay));
            dates = implicitRule.transform(dates);
        }
        if (this.transformers.get(BYHOUR) != null) {
            dates = this.transformers.get(BYHOUR).transform(dates);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Dates after BYHOUR processing: " + dates);
            }
        }
        if (this.transformers.get(BYMINUTE) != null) {
            dates = this.transformers.get(BYMINUTE).transform(dates);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Dates after BYMINUTE processing: " + dates);
            }
        }
        if (this.transformers.get(BYSECOND) != null) {
            dates = this.transformers.get(BYSECOND).transform(dates);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Dates after BYSECOND processing: " + dates);
            }
        }
        if (this.transformers.get(BYSETPOS) != null) {
            dates = this.transformers.get(BYSETPOS).transform(dates);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Dates after SETPOS processing: " + dates);
            }
        }
        return dates;
    }

    private void validateFrequency() {
        if (this.frequency == null) {
            throw new IllegalArgumentException("A recurrence rule MUST contain a FREQ rule part.");
        }
        if (Frequency.SECONDLY.equals((Object)this.getFrequency())) {
            this.calIncField = 13;
        } else if (Frequency.MINUTELY.equals((Object)this.getFrequency())) {
            this.calIncField = 12;
        } else if (Frequency.HOURLY.equals((Object)this.getFrequency())) {
            this.calIncField = 11;
        } else if (Frequency.DAILY.equals((Object)this.getFrequency())) {
            this.calIncField = 6;
        } else if (Frequency.WEEKLY.equals((Object)this.getFrequency())) {
            this.calIncField = 3;
        } else if (Frequency.MONTHLY.equals((Object)this.getFrequency())) {
            this.calIncField = 2;
        } else if (Frequency.YEARLY.equals((Object)this.getFrequency())) {
            this.calIncField = 1;
        } else {
            throw new IllegalArgumentException("Invalid FREQ rule part '" + (Object)((Object)this.frequency) + "' in recurrence rule");
        }
    }

    @Deprecated
    public final void setCount(int count) {
        this.count = count;
        this.until = null;
    }

    @Deprecated
    public final void setFrequency(String frequency) {
        this.frequency = Frequency.valueOf(frequency);
        this.validateFrequency();
    }

    @Deprecated
    public final void setInterval(int interval) {
        this.interval = interval;
    }

    @Deprecated
    public final void setUntil(Date until) {
        this.until = until;
        this.count = -1;
    }

    private Calendar getCalendarInstance(Date date, boolean lenient) {
        Calendar cal = Dates.getCalendarInstance(date);
        cal.setMinimalDaysInFirstWeek(4);
        cal.setFirstDayOfWeek(this.calendarWeekStartDay);
        cal.setLenient(lenient);
        cal.setTime(date);
        return cal;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.log = LoggerFactory.getLogger(Recur.class);
    }

    public static class Builder {
        private Frequency frequency;
        private Date until;
        private Integer count;
        private Integer interval;
        private NumberList secondList;
        private NumberList minuteList;
        private NumberList hourList;
        private WeekDayList dayList;
        private NumberList monthDayList;
        private NumberList yearDayList;
        private NumberList weekNoList;
        private NumberList monthList;
        private NumberList setPosList;
        private WeekDay.Day weekStartDay;

        public Builder frequency(Frequency frequency) {
            this.frequency = frequency;
            return this;
        }

        public Builder until(Date until) {
            this.until = until;
            return this;
        }

        public Builder count(Integer count) {
            this.count = count;
            return this;
        }

        public Builder interval(Integer interval) {
            this.interval = interval;
            return this;
        }

        public Builder secondList(NumberList secondList) {
            this.secondList = secondList;
            return this;
        }

        public Builder minuteList(NumberList minuteList) {
            this.minuteList = minuteList;
            return this;
        }

        public Builder hourList(NumberList hourList) {
            this.hourList = hourList;
            return this;
        }

        public Builder dayList(WeekDayList dayList) {
            this.dayList = dayList;
            return this;
        }

        public Builder monthDayList(NumberList monthDayList) {
            this.monthDayList = monthDayList;
            return this;
        }

        public Builder yearDayList(NumberList yearDayList) {
            this.yearDayList = yearDayList;
            return this;
        }

        public Builder weekNoList(NumberList weekNoList) {
            this.weekNoList = weekNoList;
            return this;
        }

        public Builder monthList(NumberList monthList) {
            this.monthList = monthList;
            return this;
        }

        public Builder setPosList(NumberList setPosList) {
            this.setPosList = setPosList;
            return this;
        }

        public Builder weekStartDay(WeekDay.Day weekStartDay) {
            this.weekStartDay = weekStartDay;
            return this;
        }

        public Recur build() {
            Recur recur = new Recur();
            recur.frequency = this.frequency;
            recur.until = this.until;
            recur.count = this.count;
            recur.interval = this.interval;
            recur.secondList = this.secondList;
            recur.minuteList = this.minuteList;
            recur.hourList = this.hourList;
            recur.dayList = this.dayList;
            recur.monthDayList = this.monthDayList;
            recur.yearDayList = this.yearDayList;
            recur.weekNoList = this.weekNoList;
            recur.monthList = this.monthList;
            recur.setPosList = this.setPosList;
            recur.weekStartDay = this.weekStartDay;
            recur.validateFrequency();
            recur.initTransformers();
            return recur;
        }
    }

    public static enum Frequency {
        SECONDLY,
        MINUTELY,
        HOURLY,
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY;

    }
}

