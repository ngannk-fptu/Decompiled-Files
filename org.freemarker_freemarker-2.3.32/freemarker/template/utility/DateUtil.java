/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template.utility;

import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.UnrecognizedTimeZoneException;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {
    public static final int ACCURACY_HOURS = 4;
    public static final int ACCURACY_MINUTES = 5;
    public static final int ACCURACY_SECONDS = 6;
    public static final int ACCURACY_MILLISECONDS = 7;
    public static final int ACCURACY_MILLISECONDS_FORCED = 8;
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final String REGEX_XS_TIME_ZONE = "Z|(?:[-+][0-9]{2}:[0-9]{2})";
    private static final String REGEX_ISO8601_BASIC_TIME_ZONE = "Z|(?:[-+][0-9]{2}(?:[0-9]{2})?)";
    private static final String REGEX_ISO8601_EXTENDED_TIME_ZONE = "Z|(?:[-+][0-9]{2}(?::[0-9]{2})?)";
    private static final String REGEX_XS_OPTIONAL_TIME_ZONE = "(Z|(?:[-+][0-9]{2}:[0-9]{2}))?";
    private static final String REGEX_ISO8601_BASIC_OPTIONAL_TIME_ZONE = "(Z|(?:[-+][0-9]{2}(?:[0-9]{2})?))?";
    private static final String REGEX_ISO8601_EXTENDED_OPTIONAL_TIME_ZONE = "(Z|(?:[-+][0-9]{2}(?::[0-9]{2})?))?";
    private static final String REGEX_XS_DATE_BASE = "(-?[0-9]+)-([0-9]{2})-([0-9]{2})";
    private static final String REGEX_ISO8601_BASIC_DATE_BASE = "(-?[0-9]{4,}?)([0-9]{2})([0-9]{2})";
    private static final String REGEX_ISO8601_EXTENDED_DATE_BASE = "(-?[0-9]{4,})-([0-9]{2})-([0-9]{2})";
    private static final String REGEX_XS_TIME_BASE = "([0-9]{2}):([0-9]{2}):([0-9]{2})(?:\\.([0-9]+))?";
    private static final String REGEX_ISO8601_BASIC_TIME_BASE = "([0-9]{2})(?:([0-9]{2})(?:([0-9]{2})(?:[\\.,]([0-9]+))?)?)?";
    private static final String REGEX_ISO8601_EXTENDED_TIME_BASE = "([0-9]{2})(?::([0-9]{2})(?::([0-9]{2})(?:[\\.,]([0-9]+))?)?)?";
    private static final Pattern PATTERN_XS_DATE = Pattern.compile("(-?[0-9]+)-([0-9]{2})-([0-9]{2})(Z|(?:[-+][0-9]{2}:[0-9]{2}))?");
    private static final Pattern PATTERN_ISO8601_BASIC_DATE = Pattern.compile("(-?[0-9]{4,}?)([0-9]{2})([0-9]{2})");
    private static final Pattern PATTERN_ISO8601_EXTENDED_DATE = Pattern.compile("(-?[0-9]{4,})-([0-9]{2})-([0-9]{2})");
    private static final Pattern PATTERN_XS_TIME = Pattern.compile("([0-9]{2}):([0-9]{2}):([0-9]{2})(?:\\.([0-9]+))?(Z|(?:[-+][0-9]{2}:[0-9]{2}))?");
    private static final Pattern PATTERN_ISO8601_BASIC_TIME = Pattern.compile("([0-9]{2})(?:([0-9]{2})(?:([0-9]{2})(?:[\\.,]([0-9]+))?)?)?(Z|(?:[-+][0-9]{2}(?:[0-9]{2})?))?");
    private static final Pattern PATTERN_ISO8601_EXTENDED_TIME = Pattern.compile("([0-9]{2})(?::([0-9]{2})(?::([0-9]{2})(?:[\\.,]([0-9]+))?)?)?(Z|(?:[-+][0-9]{2}(?::[0-9]{2})?))?");
    private static final Pattern PATTERN_XS_DATE_TIME = Pattern.compile("(-?[0-9]+)-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9]{2})(?:\\.([0-9]+))?(Z|(?:[-+][0-9]{2}:[0-9]{2}))?");
    private static final Pattern PATTERN_ISO8601_BASIC_DATE_TIME = Pattern.compile("(-?[0-9]{4,}?)([0-9]{2})([0-9]{2})T([0-9]{2})(?:([0-9]{2})(?:([0-9]{2})(?:[\\.,]([0-9]+))?)?)?(Z|(?:[-+][0-9]{2}(?:[0-9]{2})?))?");
    private static final Pattern PATTERN_ISO8601_EXTENDED_DATE_TIME = Pattern.compile("(-?[0-9]{4,})-([0-9]{2})-([0-9]{2})T([0-9]{2})(?::([0-9]{2})(?::([0-9]{2})(?:[\\.,]([0-9]+))?)?)?(Z|(?:[-+][0-9]{2}(?::[0-9]{2})?))?");
    private static final Pattern PATTERN_XS_TIME_ZONE = Pattern.compile("Z|(?:[-+][0-9]{2}:[0-9]{2})");
    private static final String MSG_YEAR_0_NOT_ALLOWED = "Year 0 is not allowed in XML schema dates. BC 1 is -1, AD 1 is 1.";

    private DateUtil() {
    }

    public static TimeZone getTimeZone(String name) throws UnrecognizedTimeZoneException {
        if (DateUtil.isGMTish(name)) {
            if (name.equalsIgnoreCase("UTC")) {
                return UTC;
            }
            return TimeZone.getTimeZone(name);
        }
        TimeZone tz = TimeZone.getTimeZone(name);
        if (DateUtil.isGMTish(tz.getID())) {
            throw new UnrecognizedTimeZoneException(name);
        }
        return tz;
    }

    private static boolean isGMTish(String name) {
        if (name.length() < 3) {
            return false;
        }
        char c1 = name.charAt(0);
        char c2 = name.charAt(1);
        char c3 = name.charAt(2);
        if ((c1 != 'G' && c1 != 'g' || c2 != 'M' && c2 != 'm' || c3 != 'T' && c3 != 't') && (c1 != 'U' && c1 != 'u' || c2 != 'T' && c2 != 't' || c3 != 'C' && c3 != 'c') && (c1 != 'U' && c1 != 'u' || c2 != 'T' && c2 != 't' || c3 != '1')) {
            return false;
        }
        if (name.length() == 3) {
            return true;
        }
        String offset = name.substring(3);
        if (offset.startsWith("+")) {
            return offset.equals("+0") || offset.equals("+00") || offset.equals("+00:00");
        }
        return offset.equals("-0") || offset.equals("-00") || offset.equals("-00:00");
    }

    public static String dateToISO8601String(Date date, boolean datePart, boolean timePart, boolean offsetPart, int accuracy, TimeZone timeZone, DateToISO8601CalendarFactory calendarFactory) {
        return DateUtil.dateToString(date, datePart, timePart, offsetPart, accuracy, timeZone, false, calendarFactory);
    }

    public static String dateToXSString(Date date, boolean datePart, boolean timePart, boolean offsetPart, int accuracy, TimeZone timeZone, DateToISO8601CalendarFactory calendarFactory) {
        return DateUtil.dateToString(date, datePart, timePart, offsetPart, accuracy, timeZone, true, calendarFactory);
    }

    private static String dateToString(Date date, boolean datePart, boolean timePart, boolean offsetPart, int accuracy, TimeZone timeZone, boolean xsMode, DateToISO8601CalendarFactory calendarFactory) {
        int x;
        if (!xsMode && !timePart && offsetPart) {
            throw new IllegalArgumentException("ISO 8601:2004 doesn't specify any formats where the offset is shown but the time isn't.");
        }
        if (timeZone == null) {
            timeZone = UTC;
        }
        GregorianCalendar cal = calendarFactory.get(timeZone, date);
        int maxLength = !timePart ? 10 + (xsMode ? 6 : 0) : (!datePart ? 18 : 29);
        char[] res = new char[maxLength];
        int dstIdx = 0;
        if (datePart) {
            x = cal.get(1);
            if (x > 0 && cal.get(0) == 0) {
                x = -x + (xsMode ? 0 : 1);
            }
            if (x >= 0 && x < 9999) {
                res[dstIdx++] = (char)(48 + x / 1000);
                res[dstIdx++] = (char)(48 + x % 1000 / 100);
                res[dstIdx++] = (char)(48 + x % 100 / 10);
                res[dstIdx++] = (char)(48 + x % 10);
            } else {
                String yearString = String.valueOf(x);
                maxLength = maxLength - 4 + yearString.length();
                res = new char[maxLength];
                for (int i = 0; i < yearString.length(); ++i) {
                    res[dstIdx++] = yearString.charAt(i);
                }
            }
            res[dstIdx++] = 45;
            x = cal.get(2) + 1;
            dstIdx = DateUtil.append00(res, dstIdx, x);
            res[dstIdx++] = 45;
            x = cal.get(5);
            dstIdx = DateUtil.append00(res, dstIdx, x);
            if (timePart) {
                res[dstIdx++] = 84;
            }
        }
        if (timePart) {
            x = cal.get(11);
            dstIdx = DateUtil.append00(res, dstIdx, x);
            if (accuracy >= 5) {
                res[dstIdx++] = 58;
                x = cal.get(12);
                dstIdx = DateUtil.append00(res, dstIdx, x);
                if (accuracy >= 6) {
                    res[dstIdx++] = 58;
                    x = cal.get(13);
                    dstIdx = DateUtil.append00(res, dstIdx, x);
                    if (accuracy >= 7) {
                        int forcedDigits;
                        x = cal.get(14);
                        int n = forcedDigits = accuracy == 8 ? 3 : 0;
                        if (x != 0 || forcedDigits != 0) {
                            if (x > 999) {
                                throw new RuntimeException("Calendar.MILLISECOND > 999");
                            }
                            res[dstIdx++] = 46;
                            do {
                                res[dstIdx++] = (char)(48 + x / 100);
                            } while ((x = x % 100 * 10) != 0 || --forcedDigits > 0);
                        }
                    }
                }
            }
        }
        if (offsetPart) {
            if (timeZone == UTC) {
                res[dstIdx++] = 90;
            } else {
                boolean positive;
                int dt = timeZone.getOffset(date.getTime());
                if (dt < 0) {
                    positive = false;
                    dt = -dt;
                } else {
                    positive = true;
                }
                int offS = (dt /= 1000) % 60;
                int offM = (dt /= 60) % 60;
                int offH = dt /= 60;
                if (offS == 0 && offM == 0 && offH == 0) {
                    res[dstIdx++] = 90;
                } else {
                    res[dstIdx++] = positive ? 43 : 45;
                    dstIdx = DateUtil.append00(res, dstIdx, offH);
                    res[dstIdx++] = 58;
                    dstIdx = DateUtil.append00(res, dstIdx, offM);
                    if (offS != 0) {
                        res[dstIdx++] = 58;
                        dstIdx = DateUtil.append00(res, dstIdx, offS);
                    }
                }
            }
        }
        return new String(res, 0, dstIdx);
    }

    private static int append00(char[] res, int dstIdx, int x) {
        res[dstIdx++] = (char)(48 + x / 10);
        res[dstIdx++] = (char)(48 + x % 10);
        return dstIdx;
    }

    public static Date parseXSDate(String dateStr, TimeZone defaultTimeZone, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        Matcher m = PATTERN_XS_DATE.matcher(dateStr);
        if (!m.matches()) {
            throw new DateParseException("The value didn't match the expected pattern: " + PATTERN_XS_DATE);
        }
        return DateUtil.parseDate_parseMatcher(m, defaultTimeZone, true, calToDateConverter);
    }

    public static Date parseISO8601Date(String dateStr, TimeZone defaultTimeZone, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        Matcher m = PATTERN_ISO8601_EXTENDED_DATE.matcher(dateStr);
        if (!m.matches() && !(m = PATTERN_ISO8601_BASIC_DATE.matcher(dateStr)).matches()) {
            throw new DateParseException("The value didn't match the expected pattern: " + PATTERN_ISO8601_EXTENDED_DATE + " or " + PATTERN_ISO8601_BASIC_DATE);
        }
        return DateUtil.parseDate_parseMatcher(m, defaultTimeZone, false, calToDateConverter);
    }

    private static Date parseDate_parseMatcher(Matcher m, TimeZone defaultTZ, boolean xsMode, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        NullArgumentException.check("defaultTZ", defaultTZ);
        try {
            int era;
            int year = DateUtil.groupToInt(m.group(1), "year", Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (year <= 0) {
                era = 0;
                if ((year = -year + (xsMode ? 0 : 1)) == 0) {
                    throw new DateParseException(MSG_YEAR_0_NOT_ALLOWED);
                }
            } else {
                era = 1;
            }
            int month = DateUtil.groupToInt(m.group(2), "month", 1, 12) - 1;
            int day = DateUtil.groupToInt(m.group(3), "day-of-month", 1, 31);
            TimeZone tz = xsMode ? DateUtil.parseMatchingTimeZone(m.group(4), defaultTZ) : defaultTZ;
            return calToDateConverter.calculate(era, year, month, day, 0, 0, 0, 0, false, tz);
        }
        catch (IllegalArgumentException e) {
            throw new DateParseException("Date calculation faliure. Probably the date is formally correct, but refers to an unexistent date (like February 30).");
        }
    }

    public static Date parseXSTime(String timeStr, TimeZone defaultTZ, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        Matcher m = PATTERN_XS_TIME.matcher(timeStr);
        if (!m.matches()) {
            throw new DateParseException("The value didn't match the expected pattern: " + PATTERN_XS_TIME);
        }
        return DateUtil.parseTime_parseMatcher(m, defaultTZ, calToDateConverter);
    }

    public static Date parseISO8601Time(String timeStr, TimeZone defaultTZ, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        Matcher m = PATTERN_ISO8601_EXTENDED_TIME.matcher(timeStr);
        if (!m.matches() && !(m = PATTERN_ISO8601_BASIC_TIME.matcher(timeStr)).matches()) {
            throw new DateParseException("The value didn't match the expected pattern: " + PATTERN_ISO8601_EXTENDED_TIME + " or " + PATTERN_ISO8601_BASIC_TIME);
        }
        return DateUtil.parseTime_parseMatcher(m, defaultTZ, calToDateConverter);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static Date parseTime_parseMatcher(Matcher m, TimeZone defaultTZ, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        NullArgumentException.check("defaultTZ", defaultTZ);
        try {
            int day;
            boolean hourWas24;
            int hours = DateUtil.groupToInt(m.group(1), "hour-of-day", 0, 24);
            if (hours == 24) {
                hours = 0;
                hourWas24 = true;
            } else {
                hourWas24 = false;
            }
            String minutesStr = m.group(2);
            int minutes = minutesStr != null ? DateUtil.groupToInt(minutesStr, "minute", 0, 59) : 0;
            String secsStr = m.group(3);
            int secs = secsStr != null ? DateUtil.groupToInt(secsStr, "second", 0, 60) : 0;
            int millisecs = DateUtil.groupToMillisecond(m.group(4));
            TimeZone tz = DateUtil.parseMatchingTimeZone(m.group(5), defaultTZ);
            if (hourWas24) {
                if (minutes != 0 || secs != 0 || millisecs != 0) throw new DateParseException("Hour 24 is only allowed in the case of midnight.");
                day = 2;
                return calToDateConverter.calculate(1, 1970, 0, day, hours, minutes, secs, millisecs, false, tz);
            } else {
                day = 1;
            }
            return calToDateConverter.calculate(1, 1970, 0, day, hours, minutes, secs, millisecs, false, tz);
        }
        catch (IllegalArgumentException e) {
            throw new DateParseException("Unexpected time calculation faliure.");
        }
    }

    public static Date parseXSDateTime(String dateTimeStr, TimeZone defaultTZ, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        Matcher m = PATTERN_XS_DATE_TIME.matcher(dateTimeStr);
        if (!m.matches()) {
            throw new DateParseException("The value didn't match the expected pattern: " + PATTERN_XS_DATE_TIME);
        }
        return DateUtil.parseDateTime_parseMatcher(m, defaultTZ, true, calToDateConverter);
    }

    public static Date parseISO8601DateTime(String dateTimeStr, TimeZone defaultTZ, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        Matcher m = PATTERN_ISO8601_EXTENDED_DATE_TIME.matcher(dateTimeStr);
        if (!m.matches() && !(m = PATTERN_ISO8601_BASIC_DATE_TIME.matcher(dateTimeStr)).matches()) {
            throw new DateParseException("The value (" + dateTimeStr + ") didn't match the expected pattern: " + PATTERN_ISO8601_EXTENDED_DATE_TIME + " or " + PATTERN_ISO8601_BASIC_DATE_TIME);
        }
        return DateUtil.parseDateTime_parseMatcher(m, defaultTZ, false, calToDateConverter);
    }

    private static Date parseDateTime_parseMatcher(Matcher m, TimeZone defaultTZ, boolean xsMode, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
        NullArgumentException.check("defaultTZ", defaultTZ);
        try {
            boolean hourWas24;
            int era;
            int year = DateUtil.groupToInt(m.group(1), "year", Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (year <= 0) {
                era = 0;
                if ((year = -year + (xsMode ? 0 : 1)) == 0) {
                    throw new DateParseException(MSG_YEAR_0_NOT_ALLOWED);
                }
            } else {
                era = 1;
            }
            int month = DateUtil.groupToInt(m.group(2), "month", 1, 12) - 1;
            int day = DateUtil.groupToInt(m.group(3), "day-of-month", 1, 31);
            int hours = DateUtil.groupToInt(m.group(4), "hour-of-day", 0, 24);
            if (hours == 24) {
                hours = 0;
                hourWas24 = true;
            } else {
                hourWas24 = false;
            }
            String minutesStr = m.group(5);
            int minutes = minutesStr != null ? DateUtil.groupToInt(minutesStr, "minute", 0, 59) : 0;
            String secsStr = m.group(6);
            int secs = secsStr != null ? DateUtil.groupToInt(secsStr, "second", 0, 60) : 0;
            int millisecs = DateUtil.groupToMillisecond(m.group(7));
            TimeZone tz = DateUtil.parseMatchingTimeZone(m.group(8), defaultTZ);
            if (hourWas24 && (minutes != 0 || secs != 0 || millisecs != 0)) {
                throw new DateParseException("Hour 24 is only allowed in the case of midnight.");
            }
            return calToDateConverter.calculate(era, year, month, day, hours, minutes, secs, millisecs, hourWas24, tz);
        }
        catch (IllegalArgumentException e) {
            throw new DateParseException("Date-time calculation faliure. Probably the date-time is formally correct, but refers to an unexistent date-time (like February 30).");
        }
    }

    public static TimeZone parseXSTimeZone(String timeZoneStr) throws DateParseException {
        Matcher m = PATTERN_XS_TIME_ZONE.matcher(timeZoneStr);
        if (!m.matches()) {
            throw new DateParseException("The time zone offset didn't match the expected pattern: " + PATTERN_XS_TIME_ZONE);
        }
        return DateUtil.parseMatchingTimeZone(timeZoneStr, null);
    }

    private static int groupToInt(String g, String gName, int min, int max) throws DateParseException {
        int start;
        boolean negative;
        if (g == null) {
            throw new DateParseException("The " + gName + " part is missing.");
        }
        if (g.startsWith("-")) {
            negative = true;
            start = 1;
        } else {
            negative = false;
            start = 0;
        }
        while (start < g.length() - 1 && g.charAt(start) == '0') {
            ++start;
        }
        if (start != 0) {
            g = g.substring(start);
        }
        try {
            int r = Integer.parseInt(g);
            if (negative) {
                r = -r;
            }
            if (r < min) {
                throw new DateParseException("The " + gName + " part must be at least " + min + ".");
            }
            if (r > max) {
                throw new DateParseException("The " + gName + " part can't be more than " + max + ".");
            }
            return r;
        }
        catch (NumberFormatException e) {
            throw new DateParseException("The " + gName + " part is a malformed integer.");
        }
    }

    private static TimeZone parseMatchingTimeZone(String s, TimeZone defaultZone) throws DateParseException {
        if (s == null) {
            return defaultZone;
        }
        if (s.equals("Z")) {
            return UTC;
        }
        StringBuilder sb = new StringBuilder(9);
        sb.append("GMT");
        sb.append(s.charAt(0));
        String h = s.substring(1, 3);
        DateUtil.groupToInt(h, "offset-hours", 0, 23);
        sb.append(h);
        int ln = s.length();
        if (ln > 3) {
            int startIdx = s.charAt(3) == ':' ? 4 : 3;
            String m = s.substring(startIdx, startIdx + 2);
            DateUtil.groupToInt(m, "offset-minutes", 0, 59);
            sb.append(':');
            sb.append(m);
        }
        return TimeZone.getTimeZone(sb.toString());
    }

    private static int groupToMillisecond(String g) throws DateParseException {
        if (g == null) {
            return 0;
        }
        if (g.length() > 3) {
            g = g.substring(0, 3);
        }
        int i = DateUtil.groupToInt(g, "partial-seconds", 0, Integer.MAX_VALUE);
        return g.length() == 1 ? i * 100 : (g.length() == 2 ? i * 10 : i);
    }

    public static final class DateParseException
    extends ParseException {
        public DateParseException(String message) {
            super(message, 0);
        }
    }

    public static final class TrivialCalendarFieldsToDateConverter
    implements CalendarFieldsToDateConverter {
        private GregorianCalendar calendar;
        private TimeZone lastlySetTimeZone;

        @Override
        public Date calculate(int era, int year, int month, int day, int hours, int minutes, int secs, int millisecs, boolean addOneDay, TimeZone tz) {
            if (this.calendar == null) {
                this.calendar = new GregorianCalendar(tz, Locale.US);
                this.calendar.setLenient(false);
                this.calendar.setGregorianChange(new Date(Long.MIN_VALUE));
            } else if (this.lastlySetTimeZone != tz) {
                this.calendar.setTimeZone(tz);
                this.lastlySetTimeZone = tz;
            }
            this.calendar.set(0, era);
            this.calendar.set(1, year);
            this.calendar.set(2, month);
            this.calendar.set(5, day);
            this.calendar.set(11, hours);
            this.calendar.set(12, minutes);
            this.calendar.set(13, secs);
            this.calendar.set(14, millisecs);
            if (addOneDay) {
                this.calendar.add(5, 1);
            }
            return this.calendar.getTime();
        }
    }

    public static final class TrivialDateToISO8601CalendarFactory
    implements DateToISO8601CalendarFactory {
        private GregorianCalendar calendar;
        private TimeZone lastlySetTimeZone;

        @Override
        public GregorianCalendar get(TimeZone tz, Date date) {
            if (this.calendar == null) {
                this.calendar = new GregorianCalendar(tz, Locale.US);
                this.calendar.setGregorianChange(new Date(Long.MIN_VALUE));
            } else if (this.lastlySetTimeZone != tz) {
                this.calendar.setTimeZone(tz);
                this.lastlySetTimeZone = tz;
            }
            this.calendar.setTime(date);
            return this.calendar;
        }
    }

    public static interface CalendarFieldsToDateConverter {
        public Date calculate(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, TimeZone var10);
    }

    public static interface DateToISO8601CalendarFactory {
        public GregorianCalendar get(TimeZone var1, Date var2);
    }
}

