/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import software.amazon.ion.impl.PrivateUtils;
import software.amazon.ion.util.IonTextUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Timestamp
implements Comparable<Timestamp>,
Cloneable {
    private static final boolean APPLY_OFFSET_YES = true;
    private static final boolean APPLY_OFFSET_NO = false;
    private static final int NO_MONTH = 0;
    private static final int NO_DAY = 0;
    private static final int NO_HOURS = 0;
    private static final int NO_MINUTES = 0;
    private static final int NO_SECONDS = 0;
    private static final BigDecimal NO_FRACTIONAL_SECONDS = null;
    public static final Integer UNKNOWN_OFFSET = null;
    public static final Integer UTC_OFFSET = 0;
    private static final int FLAG_YEAR = 1;
    private static final int FLAG_MONTH = 2;
    private static final int FLAG_DAY = 4;
    private static final int FLAG_MINUTE = 8;
    private static final int FLAG_SECOND = 16;
    private static final int HASH_SIGNATURE = "INTERNAL TIMESTAMP".hashCode();
    private Precision _precision;
    private short _year;
    private byte _month = 1;
    private byte _day = 1;
    private byte _hour;
    private byte _minute;
    private byte _second;
    private BigDecimal _fraction;
    private Integer _offset;
    private static final int[] LEAP_DAYS_IN_MONTH = new int[]{0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final int[] NORMAL_DAYS_IN_MONTH = new int[]{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    static final String NULL_TIMESTAMP_IMAGE = "null.timestamp";
    static final int LEN_OF_NULL_IMAGE = "null.timestamp".length();
    static final int END_OF_YEAR = 4;
    static final int END_OF_MONTH = 7;
    static final int END_OF_DAY = 10;
    static final int END_OF_MINUTES = 16;
    static final int END_OF_SECONDS = 19;

    private static int last_day_in_month(int year, int month) {
        boolean is_leap = year % 4 == 0 ? (year % 100 == 0 ? year % 400 == 0 : true) : false;
        return is_leap ? LEAP_DAYS_IN_MONTH[month] : NORMAL_DAYS_IN_MONTH[month];
    }

    private void apply_offset(int offset) {
        if (offset == 0) {
            return;
        }
        if (offset < -1440 || offset > 1440) {
            throw new IllegalArgumentException("bad offset " + offset);
        }
        offset = -offset;
        int hour_offset = offset / 60;
        int min_offset = offset - hour_offset * 60;
        if (offset < 0) {
            this._minute = (byte)(this._minute + min_offset);
            this._hour = (byte)(this._hour + hour_offset);
            if (this._minute < 0) {
                this._minute = (byte)(this._minute + 60);
                this._hour = (byte)(this._hour - 1);
            }
            if (this._hour >= 0) {
                return;
            }
            this._hour = (byte)(this._hour + 24);
            this._day = (byte)(this._day - 1);
            if (this._day >= 1) {
                return;
            }
            this._month = (byte)(this._month - 1);
            if (this._month >= 1) {
                this._day = (byte)(this._day + Timestamp.last_day_in_month(this._year, this._month));
                assert (this._day == Timestamp.last_day_in_month(this._year, this._month));
                return;
            }
            this._month = (byte)(this._month + 12);
            this._year = (short)(this._year - 1);
            if (this._year < 1) {
                throw new IllegalArgumentException("year is less than 1");
            }
            this._day = (byte)(this._day + Timestamp.last_day_in_month(this._year, this._month));
            assert (this._day == Timestamp.last_day_in_month(this._year, this._month));
        } else {
            this._minute = (byte)(this._minute + min_offset);
            this._hour = (byte)(this._hour + hour_offset);
            if (this._minute > 59) {
                this._minute = (byte)(this._minute - 60);
                this._hour = (byte)(this._hour + 1);
            }
            if (this._hour < 24) {
                return;
            }
            this._hour = (byte)(this._hour - 24);
            this._day = (byte)(this._day + 1);
            if (this._day <= Timestamp.last_day_in_month(this._year, this._month)) {
                return;
            }
            this._day = 1;
            this._month = (byte)(this._month + 1);
            if (this._month <= 12) {
                return;
            }
            this._month = (byte)(this._month - 12);
            this._year = (short)(this._year + 1);
            if (this._year > 9999) {
                throw new IllegalArgumentException("year exceeds 9999");
            }
        }
    }

    private void set_fields_from_millis(long millis) {
        Date date = new Date(millis);
        this._year = Timestamp.checkAndCastYear(date.getYear() + 1900);
        this._month = Timestamp.checkAndCastMonth(date.getMonth() + 1);
        this._day = Timestamp.checkAndCastDay(date.getDate(), this._year, this._month);
        this._hour = Timestamp.checkAndCastHour(date.getHours());
        this._minute = Timestamp.checkAndCastMinute(date.getMinutes());
        this._second = Timestamp.checkAndCastSecond(date.getSeconds());
        int offset = date.getTimezoneOffset();
        this.apply_offset(-offset);
    }

    private void set_fields_from_calendar(Calendar cal, Precision precision, boolean setLocalOffset) {
        this._precision = precision;
        this._offset = UNKNOWN_OFFSET;
        boolean dayPrecision = false;
        boolean calendarHasMilliseconds = cal.isSet(14);
        switch (this._precision) {
            case SECOND: {
                this._second = Timestamp.checkAndCastSecond(cal.get(13));
                if (calendarHasMilliseconds) {
                    BigDecimal millis = BigDecimal.valueOf(cal.get(14));
                    this._fraction = millis.movePointLeft(3);
                }
            }
            case MINUTE: {
                this._hour = Timestamp.checkAndCastHour(cal.get(11));
                this._minute = Timestamp.checkAndCastMinute(cal.get(12));
                if (setLocalOffset && cal.isSet(15)) {
                    int offset = cal.get(15);
                    if (cal.isSet(16)) {
                        offset += cal.get(16);
                    }
                    this._offset = offset / 60000;
                }
            }
            case DAY: {
                dayPrecision = true;
            }
            case MONTH: {
                this._month = Timestamp.checkAndCastMonth(cal.get(2) + 1);
            }
            case YEAR: {
                this._year = Timestamp.checkAndCastYear(cal.get(1));
            }
        }
        if (dayPrecision) {
            this._day = Timestamp.checkAndCastDay(cal.get(5), this._year, this._month);
        }
        if (this._offset != UNKNOWN_OFFSET) {
            this.apply_offset(this._offset);
        }
    }

    private Timestamp(int zyear) {
        this(Precision.YEAR, zyear, 0, 0, 0, 0, 0, NO_FRACTIONAL_SECONDS, UNKNOWN_OFFSET, false);
    }

    private Timestamp(int zyear, int zmonth) {
        this(Precision.MONTH, zyear, zmonth, 0, 0, 0, 0, NO_FRACTIONAL_SECONDS, UNKNOWN_OFFSET, false);
    }

    @Deprecated
    private Timestamp(int zyear, int zmonth, int zday) {
        this(Precision.DAY, zyear, zmonth, zday, 0, 0, 0, NO_FRACTIONAL_SECONDS, UNKNOWN_OFFSET, false);
    }

    @Deprecated
    private Timestamp(int year, int month, int day, int hour, int minute, Integer offset) {
        this(Precision.MINUTE, year, month, day, hour, minute, 0, NO_FRACTIONAL_SECONDS, offset, true);
    }

    @Deprecated
    private Timestamp(int year, int month, int day, int hour, int minute, int second, Integer offset) {
        this(Precision.SECOND, year, month, day, hour, minute, second, NO_FRACTIONAL_SECONDS, offset, true);
    }

    private Timestamp(Precision p, int zyear, int zmonth, int zday, int zhour, int zminute, int zsecond, BigDecimal frac, Integer offset, boolean shouldApplyOffset) {
        boolean dayPrecision = false;
        switch (p) {
            default: {
                throw new IllegalArgumentException("invalid Precision passed to constructor");
            }
            case SECOND: {
                this._fraction = frac == null || frac.equals(BigDecimal.ZERO) ? null : frac.abs();
                this._second = Timestamp.checkAndCastSecond(zsecond);
            }
            case MINUTE: {
                this._minute = Timestamp.checkAndCastMinute(zminute);
                this._hour = Timestamp.checkAndCastHour(zhour);
                this._offset = offset;
            }
            case DAY: {
                dayPrecision = true;
            }
            case MONTH: {
                this._month = Timestamp.checkAndCastMonth(zmonth);
            }
            case YEAR: 
        }
        this._year = Timestamp.checkAndCastYear(zyear);
        if (dayPrecision) {
            this._day = Timestamp.checkAndCastDay(zday, zyear, zmonth);
        }
        this._precision = Timestamp.checkFraction(p, this._fraction);
        if (shouldApplyOffset && offset != null) {
            this.apply_offset(offset);
        }
    }

    @Deprecated
    public static Timestamp createFromUtcFields(Precision p, int zyear, int zmonth, int zday, int zhour, int zminute, int zsecond, BigDecimal frac, Integer offset) {
        return new Timestamp(p, zyear, zmonth, zday, zhour, zminute, zsecond, frac, offset, false);
    }

    @Deprecated
    private Timestamp(Calendar cal) {
        Precision precision;
        if (cal.isSet(14) || cal.isSet(13)) {
            precision = Precision.SECOND;
        } else if (cal.isSet(11) || cal.isSet(12)) {
            precision = Precision.MINUTE;
        } else if (cal.isSet(5)) {
            precision = Precision.DAY;
        } else if (cal.isSet(2)) {
            precision = Precision.MONTH;
        } else if (cal.isSet(1)) {
            precision = Precision.YEAR;
        } else {
            throw new IllegalArgumentException("Calendar has no fields set");
        }
        this.set_fields_from_calendar(cal, precision, true);
    }

    private Timestamp(Calendar cal, Precision precision, BigDecimal fraction, Integer offset) {
        this.set_fields_from_calendar(cal, precision, false);
        this._fraction = fraction;
        if (offset != null) {
            this._offset = offset;
            this.apply_offset(offset);
        }
    }

    private Timestamp(BigDecimal millis, Precision precision, Integer localOffset) {
        long ms = millis.longValue();
        this.set_fields_from_millis(ms);
        switch (precision) {
            case YEAR: {
                this._month = 1;
            }
            case MONTH: {
                this._day = 1;
            }
            case DAY: {
                this._hour = 0;
                this._minute = 0;
            }
            case MINUTE: {
                this._second = 0;
                this._fraction = null;
                break;
            }
            case SECOND: {
                BigDecimal secs = millis.movePointLeft(3);
                BigDecimal secsDown = secs.setScale(0, RoundingMode.FLOOR);
                this._fraction = secs.subtract(secsDown);
            }
        }
        this._precision = Timestamp.checkFraction(precision, this._fraction);
        this._offset = localOffset;
    }

    @Deprecated
    private Timestamp(BigDecimal millis, Integer localOffset) {
        if (millis == null) {
            throw new NullPointerException("millis is null");
        }
        long ms = millis.longValue();
        this.set_fields_from_millis(ms);
        this._precision = Precision.SECOND;
        int scale = millis.scale();
        if (scale <= -3) {
            this._fraction = null;
        } else {
            BigDecimal secs = millis.movePointLeft(3);
            BigDecimal secsDown = secs.setScale(0, RoundingMode.FLOOR);
            this._fraction = secs.subtract(secsDown);
        }
        this._offset = localOffset;
    }

    @Deprecated
    private Timestamp(long millis, Integer localOffset) {
        this.set_fields_from_millis(millis);
        BigDecimal secs = BigDecimal.valueOf(millis).movePointLeft(3);
        BigDecimal secsDown = secs.setScale(0, RoundingMode.FLOOR);
        this._fraction = secs.subtract(secsDown);
        this._precision = Timestamp.checkFraction(Precision.SECOND, this._fraction);
        this._offset = localOffset;
    }

    private static IllegalArgumentException fail(CharSequence input, String reason) {
        input = IonTextUtils.printString(input);
        return new IllegalArgumentException("invalid timestamp: " + reason + ": " + input);
    }

    private static IllegalArgumentException fail(CharSequence input) {
        input = IonTextUtils.printString(input);
        return new IllegalArgumentException("invalid timestamp: " + input);
    }

    public static Timestamp valueOf(CharSequence ionFormattedTimestamp) {
        Integer offset;
        int timezone_start;
        CharSequence in = ionFormattedTimestamp;
        int length = in.length();
        if (length == 0) {
            throw Timestamp.fail(in);
        }
        if (in.charAt(0) == 'n') {
            if (length >= LEN_OF_NULL_IMAGE && NULL_TIMESTAMP_IMAGE.contentEquals(in.subSequence(0, LEN_OF_NULL_IMAGE))) {
                if (length > LEN_OF_NULL_IMAGE && !Timestamp.isValidFollowChar(in.charAt(LEN_OF_NULL_IMAGE))) {
                    throw Timestamp.fail(in);
                }
                return null;
            }
            throw Timestamp.fail(in);
        }
        int year = 1;
        int month = 1;
        int day = 1;
        int hour = 0;
        int minute = 0;
        int seconds = 0;
        BigDecimal fraction = null;
        if (length < 5) {
            throw Timestamp.fail(in, "year is too short (must be at least yyyyT)");
        }
        int pos = 4;
        Precision precision = Precision.YEAR;
        year = Timestamp.read_digits(in, 0, 4, -1, "year");
        char c = in.charAt(4);
        if (c != 'T') {
            if (c != '-') {
                throw Timestamp.fail(in, "expected \"-\" between year and month, found " + IonTextUtils.printCodePointAsString(c));
            }
            if (length < 8) {
                throw Timestamp.fail(in, "month is too short (must be yyyy-mmT)");
            }
            pos = 7;
            precision = Precision.MONTH;
            month = Timestamp.read_digits(in, 5, 2, -1, "month");
            c = in.charAt(7);
            if (c != 'T') {
                if (c != '-') {
                    throw Timestamp.fail(in, "expected \"-\" between month and day, found " + IonTextUtils.printCodePointAsString(c));
                }
                if (length < 10) {
                    throw Timestamp.fail(in, "too short for yyyy-mm-dd");
                }
                pos = 10;
                precision = Precision.DAY;
                day = Timestamp.read_digits(in, 8, 2, -1, "day");
                if (length != 10) {
                    c = in.charAt(10);
                    if (c != 'T') {
                        throw Timestamp.fail(in, "expected \"T\" after day, found " + IonTextUtils.printCodePointAsString(c));
                    }
                    if (length != 11) {
                        if (length < 16) {
                            throw Timestamp.fail(in, "too short for yyyy-mm-ddThh:mm");
                        }
                        hour = Timestamp.read_digits(in, 11, 2, 58, "hour");
                        minute = Timestamp.read_digits(in, 14, 2, -1, "minutes");
                        pos = 16;
                        precision = Precision.MINUTE;
                        if (length > 16 && in.charAt(16) == ':') {
                            if (length < 19) {
                                throw Timestamp.fail(in, "too short for yyyy-mm-ddThh:mm:ss");
                            }
                            seconds = Timestamp.read_digits(in, 17, 2, -1, "seconds");
                            pos = 19;
                            precision = Precision.SECOND;
                            if (length > 19 && in.charAt(19) == '.') {
                                precision = Precision.SECOND;
                                for (pos = 20; length > pos && Character.isDigit(in.charAt(pos)); ++pos) {
                                }
                                if (pos <= 20) {
                                    throw Timestamp.fail(in, "must have at least one digit after decimal point");
                                }
                                fraction = new BigDecimal(in.subSequence(19, pos).toString());
                            }
                        }
                    }
                }
            }
        }
        int n = timezone_start = pos < length ? (int)in.charAt(pos) : 10;
        if (timezone_start == 90) {
            offset = 0;
            ++pos;
        } else if (timezone_start == 43 || timezone_start == 45) {
            int tzdHours;
            if (length < pos + 5) {
                throw Timestamp.fail(in, "local offset too short");
            }
            if ((tzdHours = Timestamp.read_digits(in, ++pos, 2, 58, "local offset hours")) < 0 || tzdHours > 23) {
                throw Timestamp.fail(in, "local offset hours must be between 0 and 23 inclusive");
            }
            int tzdMinutes = Timestamp.read_digits(in, pos += 3, 2, -1, "local offset minutes");
            if (tzdMinutes > 59) {
                throw Timestamp.fail(in, "local offset minutes must be between 0 and 59 inclusive");
            }
            pos += 2;
            int temp = tzdHours * 60 + tzdMinutes;
            if (timezone_start == 45) {
                temp = -temp;
            }
            offset = temp == 0 && timezone_start == 45 ? null : Integer.valueOf(temp);
        } else {
            switch (precision) {
                case DAY: 
                case MONTH: 
                case YEAR: {
                    break;
                }
                default: {
                    throw Timestamp.fail(in, "missing local offset");
                }
            }
            offset = null;
        }
        if (length > pos + 1 && !Timestamp.isValidFollowChar(in.charAt(pos + 1))) {
            throw Timestamp.fail(in, "invalid excess characters");
        }
        Timestamp ts = new Timestamp(precision, year, month, day, hour, minute, seconds, fraction, offset, true);
        return ts;
    }

    private static int read_digits(CharSequence in, int start, int length, int terminator, String field) {
        int ii;
        int value = 0;
        int end = start + length;
        if (in.length() < end) {
            throw Timestamp.fail(in, field + " requires " + length + " digits");
        }
        for (ii = start; ii < end; ++ii) {
            char c = in.charAt(ii);
            if (!Character.isDigit(c)) {
                throw Timestamp.fail(in, field + " has non-digit character " + IonTextUtils.printCodePointAsString(c));
            }
            value *= 10;
            value += c - 48;
        }
        if (terminator != -1) {
            if (ii >= in.length() || in.charAt(ii) != terminator) {
                throw Timestamp.fail(in, field + " should end with " + IonTextUtils.printCodePointAsString(terminator));
            }
        } else if (ii < in.length() && Character.isDigit(in.charAt(ii))) {
            throw Timestamp.fail(in, field + " requires " + length + " digits but has more");
        }
        return value;
    }

    private static boolean isValidFollowChar(char c) {
        switch (c) {
            default: {
                return false;
            }
            case '\t': 
            case '\n': 
            case '\r': 
            case '\"': 
            case '\'': 
            case '(': 
            case ')': 
            case ',': 
            case '[': 
            case '\\': 
            case ']': 
            case '{': 
            case '}': 
        }
        return true;
    }

    public Timestamp clone() {
        return new Timestamp(this._precision, this._year, this._month, this._day, this._hour, this._minute, this._second, this._fraction, this._offset, false);
    }

    private Timestamp make_localtime() {
        int offset = this._offset != null ? this._offset : 0;
        Timestamp localtime = new Timestamp(this._precision, this._year, this._month, this._day, this._hour, this._minute, this._second, this._fraction, this._offset, false);
        localtime.apply_offset(-offset);
        assert (localtime._offset == this._offset);
        return localtime;
    }

    public static Timestamp forYear(int yearZ) {
        return new Timestamp(yearZ);
    }

    public static Timestamp forMonth(int yearZ, int monthZ) {
        return new Timestamp(yearZ, monthZ);
    }

    public static Timestamp forDay(int yearZ, int monthZ, int dayZ) {
        return new Timestamp(yearZ, monthZ, dayZ);
    }

    public static Timestamp forMinute(int year, int month, int day, int hour, int minute, Integer offset) {
        return new Timestamp(year, month, day, hour, minute, offset);
    }

    public static Timestamp forSecond(int year, int month, int day, int hour, int minute, int second, Integer offset) {
        return new Timestamp(year, month, day, hour, minute, second, offset);
    }

    public static Timestamp forSecond(int year, int month, int day, int hour, int minute, BigDecimal second, Integer offset) {
        int s = second.intValue();
        BigDecimal frac = second.subtract(BigDecimal.valueOf(s));
        return new Timestamp(Precision.SECOND, year, month, day, hour, minute, s, frac, offset, true);
    }

    public static Timestamp forMillis(long millis, Integer localOffset) {
        return new Timestamp(millis, localOffset);
    }

    public static Timestamp forMillis(BigDecimal millis, Integer localOffset) {
        return new Timestamp(millis, localOffset);
    }

    public static Timestamp forCalendar(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return new Timestamp(calendar);
    }

    public static Timestamp forDateZ(Date date) {
        if (date == null) {
            return null;
        }
        long millis = date.getTime();
        return new Timestamp(millis, UTC_OFFSET);
    }

    public static Timestamp forSqlTimestampZ(java.sql.Timestamp sqlTimestamp) {
        BigDecimal frac;
        if (sqlTimestamp == null) {
            return null;
        }
        long millis = sqlTimestamp.getTime();
        Timestamp ts = new Timestamp(millis, UTC_OFFSET);
        int nanos = sqlTimestamp.getNanos();
        ts._fraction = frac = BigDecimal.valueOf(nanos).movePointLeft(9);
        return ts;
    }

    public static Timestamp now() {
        long millis = System.currentTimeMillis();
        return new Timestamp(millis, UNKNOWN_OFFSET);
    }

    public static Timestamp nowZ() {
        long millis = System.currentTimeMillis();
        return new Timestamp(millis, UTC_OFFSET);
    }

    public Date dateValue() {
        long millis = this.getMillis();
        return new Date(millis);
    }

    public Calendar calendarValue() {
        GregorianCalendar cal = new GregorianCalendar(PrivateUtils.UTC);
        long millis = this.getMillis();
        Integer offset = this._offset;
        if (offset != null && offset != 0) {
            int offsetMillis = offset * 60 * 1000;
            cal.setTimeInMillis(millis += (long)offsetMillis);
            cal.set(15, offsetMillis);
        } else {
            cal.setTimeInMillis(millis);
        }
        return cal;
    }

    public long getMillis() {
        long millis = Date.UTC(this._year - 1900, this._month - 1, this._day, this._hour, this._minute, this._second);
        if (this._fraction != null) {
            int frac = this._fraction.movePointRight(3).intValue();
            millis += (long)frac;
        }
        return millis;
    }

    public BigDecimal getDecimalMillis() {
        switch (this._precision) {
            case SECOND: 
            case MINUTE: 
            case DAY: 
            case MONTH: 
            case YEAR: {
                long millis = Date.UTC(this._year - 1900, this._month - 1, this._day, this._hour, this._minute, this._second);
                BigDecimal dec = BigDecimal.valueOf(millis);
                if (this._fraction != null) {
                    dec = dec.add(this._fraction.movePointRight(3));
                }
                return dec;
            }
        }
        throw new IllegalArgumentException();
    }

    public Precision getPrecision() {
        return this._precision;
    }

    public Integer getLocalOffset() {
        return this._offset;
    }

    public int getYear() {
        Timestamp adjusted = this;
        if (this._offset != null && this._offset != 0) {
            adjusted = this.make_localtime();
        }
        return adjusted._year;
    }

    public int getMonth() {
        Timestamp adjusted = this;
        if (this._offset != null && this._offset != 0) {
            adjusted = this.make_localtime();
        }
        return adjusted._month;
    }

    public int getDay() {
        Timestamp adjusted = this;
        if (this._offset != null && this._offset != 0) {
            adjusted = this.make_localtime();
        }
        return adjusted._day;
    }

    public int getHour() {
        Timestamp adjusted = this;
        if (this._offset != null && this._offset != 0) {
            adjusted = this.make_localtime();
        }
        return adjusted._hour;
    }

    public int getMinute() {
        Timestamp adjusted = this;
        if (this._offset != null && this._offset != 0) {
            adjusted = this.make_localtime();
        }
        return adjusted._minute;
    }

    public int getSecond() {
        return this._second;
    }

    public BigDecimal getDecimalSecond() {
        BigDecimal sec = BigDecimal.valueOf(this._second);
        if (this._fraction != null) {
            sec = sec.add(this._fraction);
        }
        return sec;
    }

    public int getZYear() {
        return this._year;
    }

    public int getZMonth() {
        return this._month;
    }

    public int getZDay() {
        return this._day;
    }

    public int getZHour() {
        return this._hour;
    }

    public int getZMinute() {
        return this._minute;
    }

    public int getZSecond() {
        return this._second;
    }

    public BigDecimal getZDecimalSecond() {
        return this.getDecimalSecond();
    }

    @Deprecated
    public BigDecimal getZFractionalSecond() {
        return this._fraction;
    }

    public Timestamp withLocalOffset(Integer offset) {
        Precision precision = this.getPrecision();
        if (precision.alwaysUnknownOffset() || PrivateUtils.safeEquals(offset, this.getLocalOffset())) {
            return this;
        }
        Timestamp ts = Timestamp.createFromUtcFields(precision, this.getZYear(), this.getZMonth(), this.getZDay(), this.getZHour(), this.getZMinute(), this.getZSecond(), this.getZFractionalSecond(), offset);
        return ts;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder(32);
        try {
            this.print(buffer);
        }
        catch (IOException e) {
            throw new RuntimeException("Exception printing to StringBuilder", e);
        }
        return buffer.toString();
    }

    public String toZString() {
        StringBuilder buffer = new StringBuilder(32);
        try {
            this.printZ(buffer);
        }
        catch (IOException e) {
            throw new RuntimeException("Exception printing to StringBuilder", e);
        }
        return buffer.toString();
    }

    public void print(Appendable out) throws IOException {
        Timestamp adjusted = this;
        if (this._offset != null && this._offset != 0) {
            adjusted = this.make_localtime();
        }
        Timestamp.print(out, adjusted);
    }

    public void printZ(Appendable out) throws IOException {
        switch (this._precision) {
            case DAY: 
            case MONTH: 
            case YEAR: {
                assert (this._offset == UNKNOWN_OFFSET);
                this.print(out);
                break;
            }
            case SECOND: 
            case MINUTE: {
                Timestamp ztime = this.clone();
                ztime._offset = UTC_OFFSET;
                ztime.print(out);
                break;
            }
        }
    }

    private static void print(Appendable out, Timestamp adjusted) throws IOException {
        if (adjusted == null) {
            out.append(NULL_TIMESTAMP_IMAGE);
            return;
        }
        Timestamp.print_digits(out, adjusted._year, 4);
        if (adjusted._precision == Precision.YEAR) {
            assert (adjusted._offset == UNKNOWN_OFFSET);
            out.append("T");
            return;
        }
        out.append("-");
        Timestamp.print_digits(out, adjusted._month, 2);
        if (adjusted._precision == Precision.MONTH) {
            assert (adjusted._offset == UNKNOWN_OFFSET);
            out.append("T");
            return;
        }
        out.append("-");
        Timestamp.print_digits(out, adjusted._day, 2);
        if (adjusted._precision == Precision.DAY) {
            assert (adjusted._offset == UNKNOWN_OFFSET);
            return;
        }
        out.append("T");
        Timestamp.print_digits(out, adjusted._hour, 2);
        out.append(":");
        Timestamp.print_digits(out, adjusted._minute, 2);
        if (adjusted._precision == Precision.SECOND) {
            out.append(":");
            Timestamp.print_digits(out, adjusted._second, 2);
            if (adjusted._fraction != null) {
                Timestamp.print_fractional_digits(out, adjusted._fraction);
            }
        }
        if (adjusted._offset != UNKNOWN_OFFSET) {
            int min = adjusted._offset;
            if (min == 0) {
                out.append('Z');
            } else {
                if (min < 0) {
                    min = -min;
                    out.append('-');
                } else {
                    out.append('+');
                }
                int hour = min / 60;
                Timestamp.print_digits(out, hour, 2);
                out.append(":");
                Timestamp.print_digits(out, min -= hour * 60, 2);
            }
        } else {
            out.append("-00:00");
        }
    }

    private static void print_digits(Appendable out, int value, int length) throws IOException {
        char[] temp = new char[length];
        while (length > 0) {
            int next = value / 10;
            temp[--length] = (char)(48 + (value - next * 10));
            value = next;
        }
        while (length > 0) {
            temp[--length] = 48;
        }
        for (char c : temp) {
            out.append(c);
        }
    }

    private static void print_fractional_digits(Appendable out, BigDecimal value) throws IOException {
        String temp = value.toPlainString();
        if (temp.charAt(0) == '0') {
            temp = temp.substring(1);
        }
        out.append(temp);
    }

    public final Timestamp addMillis(long amount) {
        if (amount == 0L) {
            return this;
        }
        BigDecimal millis = this.make_localtime().getDecimalMillis();
        millis = millis.add(BigDecimal.valueOf(amount));
        Timestamp ts = new Timestamp(millis, this._precision, this._offset);
        ts._fraction = this._fraction;
        if (this._offset != null && this._offset != 0) {
            ts.apply_offset(this._offset);
        }
        return ts;
    }

    public final Timestamp addSecond(int amount) {
        long delta = (long)amount * 1000L;
        return this.addMillis(delta);
    }

    public final Timestamp addMinute(int amount) {
        long delta = (long)amount * 60L * 1000L;
        return this.addMillis(delta);
    }

    public final Timestamp addHour(int amount) {
        long delta = (long)amount * 60L * 60L * 1000L;
        return this.addMillis(delta);
    }

    public final Timestamp addDay(int amount) {
        long delta = (long)amount * 24L * 60L * 60L * 1000L;
        return this.addMillis(delta);
    }

    public final Timestamp addMonth(int amount) {
        if (amount == 0) {
            return this;
        }
        Calendar cal = this.calendarValue();
        cal.add(2, amount);
        return new Timestamp(cal, this._precision, this._fraction, this._offset);
    }

    public final Timestamp addYear(int amount) {
        if (amount == 0) {
            return this;
        }
        Calendar cal = this.calendarValue();
        cal.add(1, amount);
        return new Timestamp(cal, this._precision, this._fraction, this._offset);
    }

    public int hashCode() {
        int prime = 8191;
        int result = HASH_SIGNATURE;
        result = 8191 * result + (this._fraction != null ? this._fraction.hashCode() : 0);
        result ^= result << 19 ^ result >> 13;
        result = 8191 * result + this._year;
        result = 8191 * result + this._month;
        result = 8191 * result + this._day;
        result = 8191 * result + this._hour;
        result = 8191 * result + this._minute;
        result = 8191 * result + this._second;
        result ^= result << 19 ^ result >> 13;
        result = 8191 * result + this._precision.toString().hashCode();
        result ^= result << 19 ^ result >> 13;
        result = 8191 * result + (this._offset == null ? 0 : this._offset.hashCode());
        result ^= result << 19 ^ result >> 13;
        return result;
    }

    @Override
    public int compareTo(Timestamp t) {
        long arg_millis;
        long this_millis = this.getMillis();
        if (this_millis != (arg_millis = t.getMillis())) {
            return this_millis < arg_millis ? -1 : 1;
        }
        BigDecimal this_fraction = this._fraction == null ? BigDecimal.ZERO : this._fraction;
        BigDecimal arg_fraction = t._fraction == null ? BigDecimal.ZERO : t._fraction;
        return this_fraction.compareTo(arg_fraction);
    }

    public boolean equals(Object t) {
        if (!(t instanceof Timestamp)) {
            return false;
        }
        return this.equals((Timestamp)t);
    }

    public boolean equals(Timestamp t) {
        if (this == t) {
            return true;
        }
        if (t == null) {
            return false;
        }
        if (this._precision != t._precision) {
            return false;
        }
        if (this._offset == null ? t._offset != null : t._offset == null) {
            return false;
        }
        if (this._year != t._year) {
            return false;
        }
        if (this._month != t._month) {
            return false;
        }
        if (this._day != t._day) {
            return false;
        }
        if (this._hour != t._hour) {
            return false;
        }
        if (this._minute != t._minute) {
            return false;
        }
        if (this._second != t._second) {
            return false;
        }
        if (this._offset != null && this._offset.intValue() != t._offset.intValue()) {
            return false;
        }
        if (this._fraction != null && t._fraction == null || this._fraction == null && t._fraction != null) {
            return false;
        }
        if (this._fraction == null && t._fraction == null) {
            return true;
        }
        return this._fraction.equals(t._fraction);
    }

    private static short checkAndCastYear(int year) {
        if (year < 1 || year > 9999) {
            throw new IllegalArgumentException(String.format("Year %s must be between 1 and 9999 inclusive", year));
        }
        return (short)year;
    }

    private static byte checkAndCastMonth(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException(String.format("Month %s must be between 1 and 12 inclusive", month));
        }
        return (byte)month;
    }

    private static byte checkAndCastDay(int day, int year, int month) {
        int lastDayInMonth = Timestamp.last_day_in_month(year, month);
        if (day < 1 || day > lastDayInMonth) {
            throw new IllegalArgumentException(String.format("Day %s for year %s and month %s must be between 1 and %s inclusive", day, year, month, lastDayInMonth));
        }
        return (byte)day;
    }

    private static byte checkAndCastHour(int hour) {
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException(String.format("Hour %s must be between 0 and 23 inclusive", hour));
        }
        return (byte)hour;
    }

    private static byte checkAndCastMinute(int minute) {
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException(String.format("Minute %s must be between between 0 and 59 inclusive", minute));
        }
        return (byte)minute;
    }

    private static byte checkAndCastSecond(int second) {
        if (second < 0 || second > 59) {
            throw new IllegalArgumentException(String.format("Second %s must be between between 0 and 59 inclusive", second));
        }
        return (byte)second;
    }

    private static Precision checkFraction(Precision precision, BigDecimal fraction) {
        if (precision == Precision.SECOND) {
            if (fraction != null && (fraction.signum() == -1 || BigDecimal.ONE.compareTo(fraction) != 1)) {
                throw new IllegalArgumentException(String.format("Fractional seconds %s must be greater than or equal to 0 and less than 1", fraction));
            }
        } else if (fraction != null) {
            throw new IllegalArgumentException("Fraction must be null for non-second precision: " + fraction);
        }
        return precision;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Precision {
        YEAR(1),
        MONTH(3),
        DAY(7),
        MINUTE(15),
        SECOND(31);

        private final int flags;

        private Precision(int flags) {
            this.flags = flags;
        }

        private boolean alwaysUnknownOffset() {
            return this.ordinal() <= DAY.ordinal();
        }

        public boolean includes(Precision isIncluded) {
            switch (isIncluded) {
                case SECOND: {
                    return (this.flags & 0x10) != 0;
                }
                case MINUTE: {
                    return (this.flags & 8) != 0;
                }
                case DAY: {
                    return (this.flags & 4) != 0;
                }
                case MONTH: {
                    return (this.flags & 2) != 0;
                }
                case YEAR: {
                    return (this.flags & 1) != 0;
                }
            }
            throw new IllegalStateException("unrecognized precision" + (Object)((Object)isIncluded));
        }
    }
}

