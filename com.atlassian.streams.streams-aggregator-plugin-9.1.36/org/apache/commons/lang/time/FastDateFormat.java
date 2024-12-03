/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.lang.Validate;

public class FastDateFormat
extends Format {
    private static final long serialVersionUID = 1L;
    public static final int FULL = 0;
    public static final int LONG = 1;
    public static final int MEDIUM = 2;
    public static final int SHORT = 3;
    private static String cDefaultPattern;
    private static final Map cInstanceCache;
    private static final Map cDateInstanceCache;
    private static final Map cTimeInstanceCache;
    private static final Map cDateTimeInstanceCache;
    private static final Map cTimeZoneDisplayCache;
    private final String mPattern;
    private final TimeZone mTimeZone;
    private final boolean mTimeZoneForced;
    private final Locale mLocale;
    private final boolean mLocaleForced;
    private transient Rule[] mRules;
    private transient int mMaxLengthEstimate;

    public static FastDateFormat getInstance() {
        return FastDateFormat.getInstance(FastDateFormat.getDefaultPattern(), null, null);
    }

    public static FastDateFormat getInstance(String pattern) {
        return FastDateFormat.getInstance(pattern, null, null);
    }

    public static FastDateFormat getInstance(String pattern, TimeZone timeZone) {
        return FastDateFormat.getInstance(pattern, timeZone, null);
    }

    public static FastDateFormat getInstance(String pattern, Locale locale) {
        return FastDateFormat.getInstance(pattern, null, locale);
    }

    public static synchronized FastDateFormat getInstance(String pattern, TimeZone timeZone, Locale locale) {
        FastDateFormat emptyFormat = new FastDateFormat(pattern, timeZone, locale);
        FastDateFormat format = (FastDateFormat)cInstanceCache.get(emptyFormat);
        if (format == null) {
            format = emptyFormat;
            format.init();
            cInstanceCache.put(format, format);
        }
        return format;
    }

    public static FastDateFormat getDateInstance(int style) {
        return FastDateFormat.getDateInstance(style, null, null);
    }

    public static FastDateFormat getDateInstance(int style, Locale locale) {
        return FastDateFormat.getDateInstance(style, null, locale);
    }

    public static FastDateFormat getDateInstance(int style, TimeZone timeZone) {
        return FastDateFormat.getDateInstance(style, timeZone, null);
    }

    public static synchronized FastDateFormat getDateInstance(int style, TimeZone timeZone, Locale locale) {
        FastDateFormat format;
        Object key = new Integer(style);
        if (timeZone != null) {
            key = new Pair(key, timeZone);
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if ((format = (FastDateFormat)cDateInstanceCache.get(key = new Pair(key, locale))) == null) {
            try {
                SimpleDateFormat formatter = (SimpleDateFormat)DateFormat.getDateInstance(style, locale);
                String pattern = formatter.toPattern();
                format = FastDateFormat.getInstance(pattern, timeZone, locale);
                cDateInstanceCache.put(key, format);
            }
            catch (ClassCastException ex) {
                throw new IllegalArgumentException("No date pattern for locale: " + locale);
            }
        }
        return format;
    }

    public static FastDateFormat getTimeInstance(int style) {
        return FastDateFormat.getTimeInstance(style, null, null);
    }

    public static FastDateFormat getTimeInstance(int style, Locale locale) {
        return FastDateFormat.getTimeInstance(style, null, locale);
    }

    public static FastDateFormat getTimeInstance(int style, TimeZone timeZone) {
        return FastDateFormat.getTimeInstance(style, timeZone, null);
    }

    public static synchronized FastDateFormat getTimeInstance(int style, TimeZone timeZone, Locale locale) {
        FastDateFormat format;
        Object key = new Integer(style);
        if (timeZone != null) {
            key = new Pair(key, timeZone);
        }
        if (locale != null) {
            key = new Pair(key, locale);
        }
        if ((format = (FastDateFormat)cTimeInstanceCache.get(key)) == null) {
            if (locale == null) {
                locale = Locale.getDefault();
            }
            try {
                SimpleDateFormat formatter = (SimpleDateFormat)DateFormat.getTimeInstance(style, locale);
                String pattern = formatter.toPattern();
                format = FastDateFormat.getInstance(pattern, timeZone, locale);
                cTimeInstanceCache.put(key, format);
            }
            catch (ClassCastException ex) {
                throw new IllegalArgumentException("No date pattern for locale: " + locale);
            }
        }
        return format;
    }

    public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle) {
        return FastDateFormat.getDateTimeInstance(dateStyle, timeStyle, null, null);
    }

    public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, Locale locale) {
        return FastDateFormat.getDateTimeInstance(dateStyle, timeStyle, null, locale);
    }

    public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone) {
        return FastDateFormat.getDateTimeInstance(dateStyle, timeStyle, timeZone, null);
    }

    public static synchronized FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone, Locale locale) {
        FastDateFormat format;
        Pair key = new Pair(new Integer(dateStyle), new Integer(timeStyle));
        if (timeZone != null) {
            key = new Pair(key, timeZone);
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if ((format = (FastDateFormat)cDateTimeInstanceCache.get(key = new Pair(key, locale))) == null) {
            try {
                SimpleDateFormat formatter = (SimpleDateFormat)DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
                String pattern = formatter.toPattern();
                format = FastDateFormat.getInstance(pattern, timeZone, locale);
                cDateTimeInstanceCache.put(key, format);
            }
            catch (ClassCastException ex) {
                throw new IllegalArgumentException("No date time pattern for locale: " + locale);
            }
        }
        return format;
    }

    static synchronized String getTimeZoneDisplay(TimeZone tz, boolean daylight, int style, Locale locale) {
        TimeZoneDisplayKey key = new TimeZoneDisplayKey(tz, daylight, style, locale);
        String value = (String)cTimeZoneDisplayCache.get(key);
        if (value == null) {
            value = tz.getDisplayName(daylight, style, locale);
            cTimeZoneDisplayCache.put(key, value);
        }
        return value;
    }

    private static synchronized String getDefaultPattern() {
        if (cDefaultPattern == null) {
            cDefaultPattern = new SimpleDateFormat().toPattern();
        }
        return cDefaultPattern;
    }

    protected FastDateFormat(String pattern, TimeZone timeZone, Locale locale) {
        if (pattern == null) {
            throw new IllegalArgumentException("The pattern must not be null");
        }
        this.mPattern = pattern;
        boolean bl = this.mTimeZoneForced = timeZone != null;
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        this.mTimeZone = timeZone;
        boolean bl2 = this.mLocaleForced = locale != null;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        this.mLocale = locale;
    }

    protected void init() {
        List rulesList = this.parsePattern();
        this.mRules = rulesList.toArray(new Rule[rulesList.size()]);
        int len = 0;
        int i = this.mRules.length;
        while (--i >= 0) {
            len += this.mRules[i].estimateLength();
        }
        this.mMaxLengthEstimate = len;
    }

    /*
     * WARNING - void declaration
     */
    protected List parsePattern() {
        DateFormatSymbols symbols = new DateFormatSymbols(this.mLocale);
        ArrayList<void> rules = new ArrayList<void>();
        String[] ERAs = symbols.getEras();
        String[] months = symbols.getMonths();
        String[] shortMonths = symbols.getShortMonths();
        String[] weekdays = symbols.getWeekdays();
        String[] shortWeekdays = symbols.getShortWeekdays();
        String[] AmPmStrings = symbols.getAmPmStrings();
        int length = this.mPattern.length();
        int[] indexRef = new int[1];
        for (int i = 0; i < length; ++i) {
            void var14_14;
            indexRef[0] = i;
            String token = this.parseToken(this.mPattern, indexRef);
            i = indexRef[0];
            int tokenLen = token.length();
            if (tokenLen == 0) break;
            char c = token.charAt(0);
            switch (c) {
                case 'G': {
                    Rule rule = new TextField(0, ERAs);
                    break;
                }
                case 'y': {
                    Rule rule;
                    if (tokenLen >= 4) {
                        rule = this.selectNumberRule(1, tokenLen);
                        break;
                    }
                    rule = TwoDigitYearField.INSTANCE;
                    break;
                }
                case 'M': {
                    Rule rule;
                    if (tokenLen >= 4) {
                        rule = new TextField(2, months);
                        break;
                    }
                    if (tokenLen == 3) {
                        rule = new TextField(2, shortMonths);
                        break;
                    }
                    if (tokenLen == 2) {
                        rule = TwoDigitMonthField.INSTANCE;
                        break;
                    }
                    rule = UnpaddedMonthField.INSTANCE;
                    break;
                }
                case 'd': {
                    Rule rule = this.selectNumberRule(5, tokenLen);
                    break;
                }
                case 'h': {
                    Rule rule = new TwelveHourField(this.selectNumberRule(10, tokenLen));
                    break;
                }
                case 'H': {
                    Rule rule = this.selectNumberRule(11, tokenLen);
                    break;
                }
                case 'm': {
                    Rule rule = this.selectNumberRule(12, tokenLen);
                    break;
                }
                case 's': {
                    Rule rule = this.selectNumberRule(13, tokenLen);
                    break;
                }
                case 'S': {
                    Rule rule = this.selectNumberRule(14, tokenLen);
                    break;
                }
                case 'E': {
                    Rule rule = new TextField(7, tokenLen < 4 ? shortWeekdays : weekdays);
                    break;
                }
                case 'D': {
                    Rule rule = this.selectNumberRule(6, tokenLen);
                    break;
                }
                case 'F': {
                    Rule rule = this.selectNumberRule(8, tokenLen);
                    break;
                }
                case 'w': {
                    Rule rule = this.selectNumberRule(3, tokenLen);
                    break;
                }
                case 'W': {
                    Rule rule = this.selectNumberRule(4, tokenLen);
                    break;
                }
                case 'a': {
                    Rule rule = new TextField(9, AmPmStrings);
                    break;
                }
                case 'k': {
                    Rule rule = new TwentyFourHourField(this.selectNumberRule(11, tokenLen));
                    break;
                }
                case 'K': {
                    Rule rule = this.selectNumberRule(10, tokenLen);
                    break;
                }
                case 'z': {
                    Rule rule;
                    if (tokenLen >= 4) {
                        rule = new TimeZoneNameRule(this.mTimeZone, this.mTimeZoneForced, this.mLocale, 1);
                        break;
                    }
                    rule = new TimeZoneNameRule(this.mTimeZone, this.mTimeZoneForced, this.mLocale, 0);
                    break;
                }
                case 'Z': {
                    Rule rule;
                    if (tokenLen == 1) {
                        rule = TimeZoneNumberRule.INSTANCE_NO_COLON;
                        break;
                    }
                    rule = TimeZoneNumberRule.INSTANCE_COLON;
                    break;
                }
                case '\'': {
                    Rule rule;
                    String sub = token.substring(1);
                    if (sub.length() == 1) {
                        rule = new CharacterLiteral(sub.charAt(0));
                        break;
                    }
                    rule = new StringLiteral(sub);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Illegal pattern component: " + token);
                }
            }
            rules.add(var14_14);
        }
        return rules;
    }

    protected String parseToken(String pattern, int[] indexRef) {
        int i;
        StringBuffer buf = new StringBuffer();
        int length = pattern.length();
        char c = pattern.charAt(i);
        if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') {
            char peek;
            buf.append(c);
            while (i + 1 < length && (peek = pattern.charAt(i + 1)) == c) {
                buf.append(c);
                ++i;
            }
        } else {
            buf.append('\'');
            boolean inLiteral = false;
            for (i = indexRef[0]; i < length; ++i) {
                c = pattern.charAt(i);
                if (c == '\'') {
                    if (i + 1 < length && pattern.charAt(i + 1) == '\'') {
                        ++i;
                        buf.append(c);
                        continue;
                    }
                    inLiteral = !inLiteral;
                    continue;
                }
                if (inLiteral || (c < 'A' || c > 'Z') && (c < 'a' || c > 'z')) {
                    buf.append(c);
                    continue;
                }
                break;
            }
        }
        indexRef[0] = --i;
        return buf.toString();
    }

    protected NumberRule selectNumberRule(int field, int padding) {
        switch (padding) {
            case 1: {
                return new UnpaddedNumberField(field);
            }
            case 2: {
                return new TwoDigitNumberField(field);
            }
        }
        return new PaddedNumberField(field, padding);
    }

    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj instanceof Date) {
            return this.format((Date)obj, toAppendTo);
        }
        if (obj instanceof Calendar) {
            return this.format((Calendar)obj, toAppendTo);
        }
        if (obj instanceof Long) {
            return this.format((Long)obj, toAppendTo);
        }
        throw new IllegalArgumentException("Unknown class: " + (obj == null ? "<null>" : obj.getClass().getName()));
    }

    public String format(long millis) {
        return this.format(new Date(millis));
    }

    public String format(Date date) {
        GregorianCalendar c = new GregorianCalendar(this.mTimeZone);
        c.setTime(date);
        return this.applyRules(c, new StringBuffer(this.mMaxLengthEstimate)).toString();
    }

    public String format(Calendar calendar) {
        return this.format(calendar, new StringBuffer(this.mMaxLengthEstimate)).toString();
    }

    public StringBuffer format(long millis, StringBuffer buf) {
        return this.format(new Date(millis), buf);
    }

    public StringBuffer format(Date date, StringBuffer buf) {
        GregorianCalendar c = new GregorianCalendar(this.mTimeZone);
        c.setTime(date);
        return this.applyRules(c, buf);
    }

    public StringBuffer format(Calendar calendar, StringBuffer buf) {
        if (this.mTimeZoneForced) {
            calendar = (Calendar)calendar.clone();
            calendar.setTimeZone(this.mTimeZone);
        }
        return this.applyRules(calendar, buf);
    }

    protected StringBuffer applyRules(Calendar calendar, StringBuffer buf) {
        Rule[] rules = this.mRules;
        int len = this.mRules.length;
        for (int i = 0; i < len; ++i) {
            rules[i].appendTo(buf, calendar);
        }
        return buf;
    }

    public Object parseObject(String source, ParsePosition pos) {
        pos.setIndex(0);
        pos.setErrorIndex(0);
        return null;
    }

    public String getPattern() {
        return this.mPattern;
    }

    public TimeZone getTimeZone() {
        return this.mTimeZone;
    }

    public boolean getTimeZoneOverridesCalendar() {
        return this.mTimeZoneForced;
    }

    public Locale getLocale() {
        return this.mLocale;
    }

    public int getMaxLengthEstimate() {
        return this.mMaxLengthEstimate;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FastDateFormat)) {
            return false;
        }
        FastDateFormat other = (FastDateFormat)obj;
        return !(this.mPattern != other.mPattern && !this.mPattern.equals(other.mPattern) || this.mTimeZone != other.mTimeZone && !this.mTimeZone.equals(other.mTimeZone) || this.mLocale != other.mLocale && !this.mLocale.equals(other.mLocale) || this.mTimeZoneForced != other.mTimeZoneForced || this.mLocaleForced != other.mLocaleForced);
    }

    public int hashCode() {
        int total = 0;
        total += this.mPattern.hashCode();
        total += this.mTimeZone.hashCode();
        total += this.mTimeZoneForced ? 1 : 0;
        total += this.mLocale.hashCode();
        return total += this.mLocaleForced ? 1 : 0;
    }

    public String toString() {
        return "FastDateFormat[" + this.mPattern + "]";
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.init();
    }

    static {
        cInstanceCache = new HashMap(7);
        cDateInstanceCache = new HashMap(7);
        cTimeInstanceCache = new HashMap(7);
        cDateTimeInstanceCache = new HashMap(7);
        cTimeZoneDisplayCache = new HashMap(7);
    }

    private static class Pair {
        private final Object mObj1;
        private final Object mObj2;

        public Pair(Object obj1, Object obj2) {
            this.mObj1 = obj1;
            this.mObj2 = obj2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Pair)) {
                return false;
            }
            Pair key = (Pair)obj;
            return (this.mObj1 == null ? key.mObj1 == null : this.mObj1.equals(key.mObj1)) && (this.mObj2 == null ? key.mObj2 == null : this.mObj2.equals(key.mObj2));
        }

        public int hashCode() {
            return (this.mObj1 == null ? 0 : this.mObj1.hashCode()) + (this.mObj2 == null ? 0 : this.mObj2.hashCode());
        }

        public String toString() {
            return "[" + this.mObj1 + ':' + this.mObj2 + ']';
        }
    }

    private static class TimeZoneDisplayKey {
        private final TimeZone mTimeZone;
        private final int mStyle;
        private final Locale mLocale;

        TimeZoneDisplayKey(TimeZone timeZone, boolean daylight, int style, Locale locale) {
            this.mTimeZone = timeZone;
            if (daylight) {
                style |= Integer.MIN_VALUE;
            }
            this.mStyle = style;
            this.mLocale = locale;
        }

        public int hashCode() {
            return this.mStyle * 31 + this.mLocale.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof TimeZoneDisplayKey) {
                TimeZoneDisplayKey other = (TimeZoneDisplayKey)obj;
                return this.mTimeZone.equals(other.mTimeZone) && this.mStyle == other.mStyle && this.mLocale.equals(other.mLocale);
            }
            return false;
        }
    }

    private static class TimeZoneNumberRule
    implements Rule {
        static final TimeZoneNumberRule INSTANCE_COLON = new TimeZoneNumberRule(true);
        static final TimeZoneNumberRule INSTANCE_NO_COLON = new TimeZoneNumberRule(false);
        final boolean mColon;

        TimeZoneNumberRule(boolean colon) {
            this.mColon = colon;
        }

        public int estimateLength() {
            return 5;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            int offset = calendar.get(15) + calendar.get(16);
            if (offset < 0) {
                buffer.append('-');
                offset = -offset;
            } else {
                buffer.append('+');
            }
            int hours = offset / 3600000;
            buffer.append((char)(hours / 10 + 48));
            buffer.append((char)(hours % 10 + 48));
            if (this.mColon) {
                buffer.append(':');
            }
            int minutes = offset / 60000 - 60 * hours;
            buffer.append((char)(minutes / 10 + 48));
            buffer.append((char)(minutes % 10 + 48));
        }
    }

    private static class TimeZoneNameRule
    implements Rule {
        private final TimeZone mTimeZone;
        private final boolean mTimeZoneForced;
        private final Locale mLocale;
        private final int mStyle;
        private final String mStandard;
        private final String mDaylight;

        TimeZoneNameRule(TimeZone timeZone, boolean timeZoneForced, Locale locale, int style) {
            this.mTimeZone = timeZone;
            this.mTimeZoneForced = timeZoneForced;
            this.mLocale = locale;
            this.mStyle = style;
            if (timeZoneForced) {
                this.mStandard = FastDateFormat.getTimeZoneDisplay(timeZone, false, style, locale);
                this.mDaylight = FastDateFormat.getTimeZoneDisplay(timeZone, true, style, locale);
            } else {
                this.mStandard = null;
                this.mDaylight = null;
            }
        }

        public int estimateLength() {
            if (this.mTimeZoneForced) {
                return Math.max(this.mStandard.length(), this.mDaylight.length());
            }
            if (this.mStyle == 0) {
                return 4;
            }
            return 40;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            if (this.mTimeZoneForced) {
                if (this.mTimeZone.useDaylightTime() && calendar.get(16) != 0) {
                    buffer.append(this.mDaylight);
                } else {
                    buffer.append(this.mStandard);
                }
            } else {
                TimeZone timeZone = calendar.getTimeZone();
                if (timeZone.useDaylightTime() && calendar.get(16) != 0) {
                    buffer.append(FastDateFormat.getTimeZoneDisplay(timeZone, true, this.mStyle, this.mLocale));
                } else {
                    buffer.append(FastDateFormat.getTimeZoneDisplay(timeZone, false, this.mStyle, this.mLocale));
                }
            }
        }
    }

    private static class TwentyFourHourField
    implements NumberRule {
        private final NumberRule mRule;

        TwentyFourHourField(NumberRule rule) {
            this.mRule = rule;
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            int value = calendar.get(11);
            if (value == 0) {
                value = calendar.getMaximum(11) + 1;
            }
            this.mRule.appendTo(buffer, value);
        }

        public void appendTo(StringBuffer buffer, int value) {
            this.mRule.appendTo(buffer, value);
        }
    }

    private static class TwelveHourField
    implements NumberRule {
        private final NumberRule mRule;

        TwelveHourField(NumberRule rule) {
            this.mRule = rule;
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            int value = calendar.get(10);
            if (value == 0) {
                value = calendar.getLeastMaximum(10) + 1;
            }
            this.mRule.appendTo(buffer, value);
        }

        public void appendTo(StringBuffer buffer, int value) {
            this.mRule.appendTo(buffer, value);
        }
    }

    private static class TwoDigitMonthField
    implements NumberRule {
        static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField();

        TwoDigitMonthField() {
        }

        public int estimateLength() {
            return 2;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            this.appendTo(buffer, calendar.get(2) + 1);
        }

        public final void appendTo(StringBuffer buffer, int value) {
            buffer.append((char)(value / 10 + 48));
            buffer.append((char)(value % 10 + 48));
        }
    }

    private static class TwoDigitYearField
    implements NumberRule {
        static final TwoDigitYearField INSTANCE = new TwoDigitYearField();

        TwoDigitYearField() {
        }

        public int estimateLength() {
            return 2;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            this.appendTo(buffer, calendar.get(1) % 100);
        }

        public final void appendTo(StringBuffer buffer, int value) {
            buffer.append((char)(value / 10 + 48));
            buffer.append((char)(value % 10 + 48));
        }
    }

    private static class TwoDigitNumberField
    implements NumberRule {
        private final int mField;

        TwoDigitNumberField(int field) {
            this.mField = field;
        }

        public int estimateLength() {
            return 2;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            this.appendTo(buffer, calendar.get(this.mField));
        }

        public final void appendTo(StringBuffer buffer, int value) {
            if (value < 100) {
                buffer.append((char)(value / 10 + 48));
                buffer.append((char)(value % 10 + 48));
            } else {
                buffer.append(Integer.toString(value));
            }
        }
    }

    private static class PaddedNumberField
    implements NumberRule {
        private final int mField;
        private final int mSize;

        PaddedNumberField(int field, int size) {
            if (size < 3) {
                throw new IllegalArgumentException();
            }
            this.mField = field;
            this.mSize = size;
        }

        public int estimateLength() {
            return 4;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            this.appendTo(buffer, calendar.get(this.mField));
        }

        public final void appendTo(StringBuffer buffer, int value) {
            if (value < 100) {
                int i = this.mSize;
                while (--i >= 2) {
                    buffer.append('0');
                }
                buffer.append((char)(value / 10 + 48));
                buffer.append((char)(value % 10 + 48));
            } else {
                int digits;
                if (value < 1000) {
                    digits = 3;
                } else {
                    Validate.isTrue(value > -1, "Negative values should not be possible", value);
                    digits = Integer.toString(value).length();
                }
                int i = this.mSize;
                while (--i >= digits) {
                    buffer.append('0');
                }
                buffer.append(Integer.toString(value));
            }
        }
    }

    private static class UnpaddedMonthField
    implements NumberRule {
        static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField();

        UnpaddedMonthField() {
        }

        public int estimateLength() {
            return 2;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            this.appendTo(buffer, calendar.get(2) + 1);
        }

        public final void appendTo(StringBuffer buffer, int value) {
            if (value < 10) {
                buffer.append((char)(value + 48));
            } else {
                buffer.append((char)(value / 10 + 48));
                buffer.append((char)(value % 10 + 48));
            }
        }
    }

    private static class UnpaddedNumberField
    implements NumberRule {
        static final UnpaddedNumberField INSTANCE_YEAR = new UnpaddedNumberField(1);
        private final int mField;

        UnpaddedNumberField(int field) {
            this.mField = field;
        }

        public int estimateLength() {
            return 4;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            this.appendTo(buffer, calendar.get(this.mField));
        }

        public final void appendTo(StringBuffer buffer, int value) {
            if (value < 10) {
                buffer.append((char)(value + 48));
            } else if (value < 100) {
                buffer.append((char)(value / 10 + 48));
                buffer.append((char)(value % 10 + 48));
            } else {
                buffer.append(Integer.toString(value));
            }
        }
    }

    private static class TextField
    implements Rule {
        private final int mField;
        private final String[] mValues;

        TextField(int field, String[] values) {
            this.mField = field;
            this.mValues = values;
        }

        public int estimateLength() {
            int max = 0;
            int i = this.mValues.length;
            while (--i >= 0) {
                int len = this.mValues[i].length();
                if (len <= max) continue;
                max = len;
            }
            return max;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            buffer.append(this.mValues[calendar.get(this.mField)]);
        }
    }

    private static class StringLiteral
    implements Rule {
        private final String mValue;

        StringLiteral(String value) {
            this.mValue = value;
        }

        public int estimateLength() {
            return this.mValue.length();
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            buffer.append(this.mValue);
        }
    }

    private static class CharacterLiteral
    implements Rule {
        private final char mValue;

        CharacterLiteral(char value) {
            this.mValue = value;
        }

        public int estimateLength() {
            return 1;
        }

        public void appendTo(StringBuffer buffer, Calendar calendar) {
            buffer.append(this.mValue);
        }
    }

    private static interface NumberRule
    extends Rule {
        public void appendTo(StringBuffer var1, int var2);
    }

    private static interface Rule {
        public int estimateLength();

        public void appendTo(StringBuffer var1, Calendar var2);
    }
}

