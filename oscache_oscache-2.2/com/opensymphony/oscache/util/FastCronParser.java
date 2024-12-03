/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.util;

import com.opensymphony.oscache.util.ValueSet;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;

public class FastCronParser {
    private static final int NUMBER_OF_CRON_FIELDS = 5;
    private static final int MINUTE = 0;
    private static final int HOUR = 1;
    private static final int DAY_OF_MONTH = 2;
    private static final int MONTH = 3;
    private static final int DAY_OF_WEEK = 4;
    private static final int[] MIN_VALUE = new int[]{0, 0, 1, 1, 0};
    private static final int[] MAX_VALUE = new int[]{59, 23, 31, 12, 6};
    private static final int[] DAYS_IN_MONTH = new int[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private String cronExpression = null;
    private long[] lookup = new long[]{Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE};
    private int[] lookupMax = new int[]{-1, -1, -1, -1, -1};
    private int[] lookupMin = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};

    public FastCronParser() {
    }

    public FastCronParser(String cronExpression) throws ParseException {
        this.setCronExpression(cronExpression);
    }

    public void setCronExpression(String cronExpression) throws ParseException {
        if (cronExpression == null) {
            throw new IllegalArgumentException("Cron time expression cannot be null");
        }
        this.cronExpression = cronExpression;
        this.parseExpression(cronExpression);
    }

    public String getCronExpression() {
        return this.cronExpression;
    }

    public boolean hasMoreRecentMatch(long time) {
        return time < this.getTimeBefore(System.currentTimeMillis());
    }

    public long getTimeBefore(long time) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date(time));
        int minute = cal.get(12);
        int hour = cal.get(11);
        int dayOfMonth = cal.get(5);
        int month = cal.get(2) + 1;
        int year = cal.get(1);
        long validMinutes = this.lookup[0];
        long validHours = this.lookup[1];
        long validDaysOfMonth = this.lookup[2];
        long validMonths = this.lookup[3];
        long validDaysOfWeek = this.lookup[4];
        boolean haveDOM = validDaysOfMonth != Long.MAX_VALUE;
        boolean haveDOW = validDaysOfWeek != Long.MAX_VALUE;
        boolean skippedNonLeapYear = false;
        while (true) {
            int i;
            int i2;
            boolean retry = false;
            if (month < 1) {
                month += 12;
                --year;
            }
            boolean found = false;
            if (validMonths != Long.MAX_VALUE) {
                for (i2 = month + 11; i2 > month - 1; --i2) {
                    int testMonth = i2 % 12 + 1;
                    if ((1L << testMonth - 1 & validMonths) == 0L) continue;
                    if (testMonth > month || skippedNonLeapYear) {
                        --year;
                    }
                    int numDays = this.numberOfDaysInMonth(testMonth, year);
                    if (haveDOM && numDays < this.lookupMin[2]) continue;
                    if (month != testMonth || skippedNonLeapYear) {
                        dayOfMonth = numDays <= this.lookupMax[2] ? numDays : this.lookupMax[2];
                        hour = this.lookupMax[1];
                        minute = this.lookupMax[0];
                        month = testMonth;
                    }
                    found = true;
                    break;
                }
                skippedNonLeapYear = false;
                if (!found) {
                    skippedNonLeapYear = true;
                    continue;
                }
            }
            if (dayOfMonth < 1) {
                dayOfMonth += this.numberOfDaysInMonth(--month, year);
                hour = this.lookupMax[1];
                continue;
            }
            if (haveDOM && !haveDOW) {
                int daysInThisMonth = this.numberOfDaysInMonth(month, year);
                int daysInPreviousMonth = this.numberOfDaysInMonth(month - 1, year);
                for (i = dayOfMonth + 30; i > dayOfMonth - 1; --i) {
                    int testDayOfMonth = i % 31 + 1;
                    if (testDayOfMonth <= dayOfMonth && testDayOfMonth > daysInThisMonth || testDayOfMonth > dayOfMonth && testDayOfMonth > daysInPreviousMonth || (1L << testDayOfMonth - 1 & validDaysOfMonth) == 0L) continue;
                    if (testDayOfMonth > dayOfMonth) {
                        --month;
                        retry = true;
                    }
                    if (dayOfMonth != testDayOfMonth) {
                        hour = this.lookupMax[1];
                        minute = this.lookupMax[0];
                    }
                    dayOfMonth = testDayOfMonth;
                    break;
                }
                if (retry) {
                    continue;
                }
            } else if (haveDOW && !haveDOM) {
                int daysLost = 0;
                int currentDOW = this.dayOfWeek(dayOfMonth, month, year);
                for (i = currentDOW + 7; i > currentDOW; --i) {
                    int testDOW = i % 7;
                    if ((1L << testDOW & validDaysOfWeek) != 0L) {
                        if ((dayOfMonth -= daysLost) < 1) {
                            dayOfMonth += this.numberOfDaysInMonth(--month, year);
                            retry = true;
                        }
                        if (currentDOW == testDOW) break;
                        hour = this.lookupMax[1];
                        minute = this.lookupMax[0];
                        break;
                    }
                    ++daysLost;
                }
                if (retry) continue;
            }
            if (hour < 0) {
                hour += 24;
                --dayOfMonth;
                continue;
            }
            if (validHours != Long.MAX_VALUE) {
                for (i2 = hour + 24; i2 > hour; --i2) {
                    int testHour = i2 % 24;
                    if ((1L << testHour & validHours) == 0L) continue;
                    if (testHour > hour) {
                        --dayOfMonth;
                        retry = true;
                    }
                    if (hour != testHour) {
                        minute = this.lookupMax[0];
                    }
                    hour = testHour;
                    break;
                }
                if (retry) continue;
            }
            if (validMinutes == Long.MAX_VALUE) break;
            for (i2 = minute + 60; i2 > minute; --i2) {
                int testMinute = i2 % 60;
                if ((1L << testMinute & validMinutes) == 0L) continue;
                if (testMinute > minute) {
                    --hour;
                    retry = true;
                }
                minute = testMinute;
                break;
            }
            if (!retry) break;
        }
        cal.set(1, year);
        cal.set(2, month - 1);
        cal.set(5, dayOfMonth);
        cal.set(11, hour);
        cal.set(12, minute);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTime().getTime();
    }

    private void parseExpression(String expression) throws ParseException {
        try {
            int i = 0;
            while (i < this.lookup.length) {
                this.lookupMin[i] = Integer.MAX_VALUE;
                this.lookupMax[i] = -1;
                this.lookup[i++] = 0L;
            }
            char[][] token = new char[5][];
            int length = expression.length();
            char[] expr = new char[length];
            expression.getChars(0, length, expr, 0);
            int field = 0;
            int startIndex = 0;
            boolean inWhitespace = true;
            for (int i2 = 0; i2 < length && field < 5; ++i2) {
                boolean haveChar;
                boolean bl = haveChar = expr[i2] != ' ' && expr[i2] != '\t';
                if (haveChar && inWhitespace) {
                    startIndex = i2;
                    inWhitespace = false;
                }
                if (i2 == length - 1) {
                    ++i2;
                }
                if ((haveChar || inWhitespace) && i2 != length) continue;
                token[field] = new char[i2 - startIndex];
                System.arraycopy(expr, startIndex, token[field], 0, i2 - startIndex);
                inWhitespace = true;
                ++field;
            }
            if (field < 5) {
                throw new ParseException("Unexpected end of expression while parsing \"" + expression + "\". Cron expressions require 5 separate fields.", length);
            }
            for (field = 0; field < 5; ++field) {
                startIndex = 0;
                boolean inDelimiter = true;
                int elementLength = token[field].length;
                for (int i3 = 0; i3 < elementLength; ++i3) {
                    boolean haveElement;
                    boolean bl = haveElement = token[field][i3] != ',';
                    if (haveElement && inDelimiter) {
                        startIndex = i3;
                        inDelimiter = false;
                    }
                    if (i3 == elementLength - 1) {
                        ++i3;
                    }
                    if ((haveElement || inDelimiter) && i3 != elementLength) continue;
                    char[] element = new char[i3 - startIndex];
                    System.arraycopy(token[field], startIndex, element, 0, i3 - startIndex);
                    this.storeExpressionValues(element, field);
                    inDelimiter = true;
                }
                if (this.lookup[field] != 0L) continue;
                throw new ParseException("Token " + new String(token[field]) + " contains no valid entries for this field.", 0);
            }
            switch (this.lookupMin[2]) {
                case 31: {
                    this.lookup[3] = this.lookup[3] & 0xAD7L;
                }
                case 30: {
                    this.lookup[3] = this.lookup[3] & 0xFFDL;
                    if (this.lookup[3] != 0L) break;
                    throw new ParseException("The cron expression \"" + expression + "\" will never match any months - the day of month field is out of range.", 0);
                }
            }
            if (this.lookup[2] != Long.MAX_VALUE && this.lookup[4] != Long.MAX_VALUE) {
                throw new ParseException("The cron expression \"" + expression + "\" is invalid. Having both a day-of-month and day-of-week field is not supported.", 0);
            }
        }
        catch (Exception e) {
            if (e instanceof ParseException) {
                throw (ParseException)e;
            }
            throw new ParseException("Illegal cron expression format (" + e.toString() + ")", 0);
        }
    }

    private void storeExpressionValues(char[] element, int field) throws ParseException {
        int i = 0;
        int start = -99;
        int end = -99;
        int interval = -1;
        boolean wantValue = true;
        boolean haveInterval = false;
        while (interval < 0 && i < element.length) {
            char ch = element[i++];
            if (i == 1 && ch == '*') {
                if (i >= element.length) {
                    this.addToLookup(-1, -1, field, 1);
                    return;
                }
                start = -1;
                end = -1;
                wantValue = false;
                continue;
            }
            if (wantValue) {
                if (ch >= '0' && ch <= '9') {
                    ValueSet vs = this.getValue(ch - 48, element, i);
                    if (start == -99) {
                        start = vs.value;
                    } else if (!haveInterval) {
                        end = vs.value;
                    } else {
                        if (end == -99) {
                            end = MAX_VALUE[field];
                        }
                        interval = vs.value;
                    }
                    i = vs.pos;
                    wantValue = false;
                    continue;
                }
                if (!haveInterval && end == -99) {
                    if (field == 3) {
                        int c;
                        if (start == -99) {
                            start = this.getMonthVal(ch, element, i++);
                        } else {
                            end = this.getMonthVal(ch, element, i++);
                        }
                        wantValue = false;
                        while (++i < element.length && (c = element[i] | 0x20) >= 97 && c <= 122) {
                        }
                        continue;
                    }
                    if (field == 4) {
                        int c;
                        if (start == -99) {
                            start = this.getDayOfWeekVal(ch, element, i++);
                        } else {
                            end = this.getDayOfWeekVal(ch, element, i++);
                        }
                        wantValue = false;
                        while (++i < element.length && (c = element[i] | 0x20) >= 97 && c <= 122) {
                        }
                        continue;
                    }
                }
            } else {
                if (ch == '-' && start != -99 && end == -99) {
                    wantValue = true;
                    continue;
                }
                if (ch == '/' && start != -99) {
                    wantValue = true;
                    haveInterval = true;
                    continue;
                }
            }
            throw this.makeParseException("Invalid character encountered while parsing element", element, i);
        }
        if (element.length > i) {
            throw this.makeParseException("Extraneous characters found while parsing element", element, i);
        }
        if (end == -99) {
            end = start;
        }
        if (interval < 0) {
            interval = 1;
        }
        this.addToLookup(start, end, field, interval);
    }

    private ValueSet getValue(int value, char[] element, int i) {
        ValueSet result = new ValueSet();
        result.value = value;
        if (i >= element.length) {
            result.pos = i;
            return result;
        }
        char ch = element[i];
        while (ch >= '0' && ch <= '9') {
            result.value = result.value * 10 + (ch - 48);
            if (++i >= element.length) break;
            ch = element[i];
        }
        result.pos = i;
        return result;
    }

    private void addToLookup(int start, int end, int field, int interval) throws ParseException {
        if (start == end) {
            if (start < 0) {
                start = this.lookupMin[field] = MIN_VALUE[field];
                end = this.lookupMax[field] = MAX_VALUE[field];
                if (interval <= 1) {
                    this.lookup[field] = Long.MAX_VALUE;
                    return;
                }
            } else {
                if (start < MIN_VALUE[field]) {
                    throw new ParseException("Value " + start + " in field " + field + " is lower than the minimum allowable value for this field (min=" + MIN_VALUE[field] + ")", 0);
                }
                if (start > MAX_VALUE[field]) {
                    throw new ParseException("Value " + start + " in field " + field + " is higher than the maximum allowable value for this field (max=" + MAX_VALUE[field] + ")", 0);
                }
            }
        } else {
            if (start > end) {
                end ^= start;
                start ^= end;
                end ^= start;
            }
            if (start < 0) {
                start = MIN_VALUE[field];
            } else if (start < MIN_VALUE[field]) {
                throw new ParseException("Value " + start + " in field " + field + " is lower than the minimum allowable value for this field (min=" + MIN_VALUE[field] + ")", 0);
            }
            if (end < 0) {
                end = MAX_VALUE[field];
            } else if (end > MAX_VALUE[field]) {
                throw new ParseException("Value " + end + " in field " + field + " is higher than the maximum allowable value for this field (max=" + MAX_VALUE[field] + ")", 0);
            }
        }
        if (interval < 1) {
            interval = 1;
        }
        int i = start - MIN_VALUE[field];
        for (i = start - MIN_VALUE[field]; i <= end - MIN_VALUE[field]; i += interval) {
            int n = field;
            this.lookup[n] = this.lookup[n] | 1L << i;
        }
        if (this.lookupMin[field] > start) {
            this.lookupMin[field] = start;
        }
        if (this.lookupMax[field] < (i += MIN_VALUE[field] - interval)) {
            this.lookupMax[field] = i;
        }
    }

    private boolean isLeapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    private int dayOfWeek(int day, int month, int year) {
        int n;
        if (month < 3) {
            int n2 = year;
            n = n2;
            year = n2 - 1;
        } else {
            n = year - 2;
        }
        return (23 * month / 9 + (day += n) + 4 + year / 4 - year / 100 + year / 400) % 7;
    }

    private int numberOfDaysInMonth(int month, int year) {
        while (month < 1) {
            month += 12;
            --year;
        }
        while (month > 12) {
            month -= 12;
            ++year;
        }
        if (month == 2) {
            return this.isLeapYear(year) ? 29 : 28;
        }
        return DAYS_IN_MONTH[month - 1];
    }

    private int getDayOfWeekVal(char ch1, char[] element, int i) throws ParseException {
        if (i + 1 >= element.length) {
            throw this.makeParseException("Unexpected end of element encountered while parsing a day name", element, i);
        }
        int ch2 = element[i] | 0x20;
        int ch3 = element[i + 1] | 0x20;
        switch (ch1 | 0x20) {
            case 115: {
                if (ch2 == 117 && ch3 == 110) {
                    return 0;
                }
                if (ch2 != 97 || ch3 != 116) break;
                return 6;
            }
            case 109: {
                if (ch2 != 111 || ch3 != 110) break;
                return 1;
            }
            case 116: {
                if (ch2 == 117 && ch3 == 101) {
                    return 2;
                }
                if (ch2 != 104 || ch3 != 117) break;
                return 4;
            }
            case 119: {
                if (ch2 != 101 || ch3 != 100) break;
                return 3;
            }
            case 102: {
                if (ch2 != 114 || ch3 != 105) break;
                return 5;
            }
        }
        throw this.makeParseException("Unexpected character while parsing a day name", element, i - 1);
    }

    private int getMonthVal(char ch1, char[] element, int i) throws ParseException {
        if (i + 1 >= element.length) {
            throw this.makeParseException("Unexpected end of element encountered while parsing a month name", element, i);
        }
        int ch2 = element[i] | 0x20;
        int ch3 = element[i + 1] | 0x20;
        switch (ch1 | 0x20) {
            case 106: {
                if (ch2 == 97 && ch3 == 110) {
                    return 1;
                }
                if (ch2 != 117) break;
                if (ch3 == 110) {
                    return 6;
                }
                if (ch3 != 108) break;
                return 7;
            }
            case 102: {
                if (ch2 != 101 || ch3 != 98) break;
                return 2;
            }
            case 109: {
                if (ch2 != 97) break;
                if (ch3 == 114) {
                    return 3;
                }
                if (ch3 != 121) break;
                return 5;
            }
            case 97: {
                if (ch2 == 112 && ch3 == 114) {
                    return 4;
                }
                if (ch2 != 117 || ch3 != 103) break;
                return 8;
            }
            case 115: {
                if (ch2 != 101 || ch3 != 112) break;
                return 9;
            }
            case 111: {
                if (ch2 != 99 || ch3 != 116) break;
                return 10;
            }
            case 110: {
                if (ch2 != 111 || ch3 != 118) break;
                return 11;
            }
            case 100: {
                if (ch2 != 101 || ch3 != 99) break;
                return 12;
            }
        }
        throw this.makeParseException("Unexpected character while parsing a month name", element, i - 1);
    }

    public String getExpressionSummary() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.getExpressionSetSummary(0)).append(' ');
        buf.append(this.getExpressionSetSummary(1)).append(' ');
        buf.append(this.getExpressionSetSummary(2)).append(' ');
        buf.append(this.getExpressionSetSummary(3)).append(' ');
        buf.append(this.getExpressionSetSummary(4));
        return buf.toString();
    }

    private String getExpressionSetSummary(int field) {
        if (this.lookup[field] == Long.MAX_VALUE) {
            return "*";
        }
        StringBuffer buf = new StringBuffer();
        boolean first = true;
        for (int i = MIN_VALUE[field]; i <= MAX_VALUE[field]; ++i) {
            if ((this.lookup[field] & 1L << i - MIN_VALUE[field]) == 0L) continue;
            if (!first) {
                buf.append(",");
            } else {
                first = false;
            }
            buf.append(String.valueOf(i));
        }
        return buf.toString();
    }

    private ParseException makeParseException(String msg, char[] data, int offset) {
        char[] buf = new char[msg.length() + data.length + 3];
        int msgLen = msg.length();
        System.arraycopy(msg.toCharArray(), 0, buf, 0, msgLen);
        buf[msgLen] = 32;
        buf[msgLen + 1] = 91;
        System.arraycopy(data, 0, buf, msgLen + 2, data.length);
        buf[buf.length - 1] = 93;
        return new ParseException(new String(buf), offset);
    }
}

