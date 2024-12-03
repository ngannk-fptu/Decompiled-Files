/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.AbstractConverter;

public abstract class DateTimeConverter
extends AbstractConverter {
    private String[] patterns;
    private String displayPatterns;
    private Locale locale;
    private TimeZone timeZone;
    private boolean useLocaleFormat;

    public DateTimeConverter() {
    }

    public DateTimeConverter(Object defaultValue) {
        super(defaultValue);
    }

    public void setUseLocaleFormat(boolean useLocaleFormat) {
        this.useLocaleFormat = useLocaleFormat;
    }

    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        this.setUseLocaleFormat(true);
    }

    public void setPattern(String pattern) {
        this.setPatterns(new String[]{pattern});
    }

    public String[] getPatterns() {
        return this.patterns;
    }

    public void setPatterns(String[] patterns) {
        this.patterns = patterns;
        if (patterns != null && patterns.length > 1) {
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < patterns.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(patterns[i]);
            }
            this.displayPatterns = buffer.toString();
        }
        this.setUseLocaleFormat(true);
    }

    @Override
    protected String convertToString(Object value) throws Throwable {
        Date date = null;
        if (value instanceof Date) {
            date = (Date)value;
        } else if (value instanceof Calendar) {
            date = ((Calendar)value).getTime();
        } else if (value instanceof Long) {
            date = new Date((Long)value);
        }
        String result = null;
        if (this.useLocaleFormat && date != null) {
            DateFormat format = null;
            format = this.patterns != null && this.patterns.length > 0 ? this.getFormat(this.patterns[0]) : this.getFormat(this.locale, this.timeZone);
            this.logFormat("Formatting", format);
            result = format.format(date);
            if (this.log().isDebugEnabled()) {
                this.log().debug((Object)("    Converted  to String using format '" + result + "'"));
            }
        } else {
            result = value.toString();
            if (this.log().isDebugEnabled()) {
                this.log().debug((Object)("    Converted  to String using toString() '" + result + "'"));
            }
        }
        return result;
    }

    @Override
    protected <T> T convertToType(Class<T> targetType, Object value) throws Exception {
        Class<?> sourceType = value.getClass();
        if (value instanceof Timestamp) {
            Timestamp timestamp = (Timestamp)value;
            long timeInMillis = timestamp.getTime() / 1000L * 1000L;
            return this.toDate(targetType, timeInMillis += (long)(timestamp.getNanos() / 1000000));
        }
        if (value instanceof Date) {
            Date date = (Date)value;
            return this.toDate(targetType, date.getTime());
        }
        if (value instanceof Calendar) {
            Calendar calendar = (Calendar)value;
            return this.toDate(targetType, calendar.getTime().getTime());
        }
        if (value instanceof Long) {
            Long longObj = (Long)value;
            return this.toDate(targetType, longObj);
        }
        String stringValue = value.toString().trim();
        if (stringValue.length() == 0) {
            return this.handleMissing(targetType);
        }
        if (this.useLocaleFormat) {
            Calendar calendar = null;
            if (this.patterns != null && this.patterns.length > 0) {
                calendar = this.parse(sourceType, targetType, stringValue);
            } else {
                DateFormat format = this.getFormat(this.locale, this.timeZone);
                calendar = this.parse(sourceType, targetType, stringValue, format);
            }
            if (Calendar.class.isAssignableFrom(targetType)) {
                return targetType.cast(calendar);
            }
            return this.toDate(targetType, calendar.getTime().getTime());
        }
        return this.toDate(targetType, stringValue);
    }

    private <T> T toDate(Class<T> type, long value) {
        if (type.equals(Date.class)) {
            return type.cast(new Date(value));
        }
        if (type.equals(java.sql.Date.class)) {
            return type.cast(new java.sql.Date(value));
        }
        if (type.equals(Time.class)) {
            return type.cast(new Time(value));
        }
        if (type.equals(Timestamp.class)) {
            return type.cast(new Timestamp(value));
        }
        if (type.equals(Calendar.class)) {
            Calendar calendar = null;
            calendar = this.locale == null && this.timeZone == null ? Calendar.getInstance() : (this.locale == null ? Calendar.getInstance(this.timeZone) : (this.timeZone == null ? Calendar.getInstance(this.locale) : Calendar.getInstance(this.timeZone, this.locale)));
            calendar.setTime(new Date(value));
            calendar.setLenient(false);
            return type.cast(calendar);
        }
        String msg = this.toString(this.getClass()) + " cannot handle conversion to '" + this.toString(type) + "'";
        if (this.log().isWarnEnabled()) {
            this.log().warn((Object)("    " + msg));
        }
        throw new ConversionException(msg);
    }

    private <T> T toDate(Class<T> type, String value) {
        if (type.equals(java.sql.Date.class)) {
            try {
                return type.cast(java.sql.Date.valueOf(value));
            }
            catch (IllegalArgumentException e) {
                throw new ConversionException("String must be in JDBC format [yyyy-MM-dd] to create a java.sql.Date");
            }
        }
        if (type.equals(Time.class)) {
            try {
                return type.cast(Time.valueOf(value));
            }
            catch (IllegalArgumentException e) {
                throw new ConversionException("String must be in JDBC format [HH:mm:ss] to create a java.sql.Time");
            }
        }
        if (type.equals(Timestamp.class)) {
            try {
                return type.cast(Timestamp.valueOf(value));
            }
            catch (IllegalArgumentException e) {
                throw new ConversionException("String must be in JDBC format [yyyy-MM-dd HH:mm:ss.fffffffff] to create a java.sql.Timestamp");
            }
        }
        String msg = this.toString(this.getClass()) + " does not support default String to '" + this.toString(type) + "' conversion.";
        if (this.log().isWarnEnabled()) {
            this.log().warn((Object)("    " + msg));
            this.log().warn((Object)"    (N.B. Re-configure Converter or use alternative implementation)");
        }
        throw new ConversionException(msg);
    }

    protected DateFormat getFormat(Locale locale, TimeZone timeZone) {
        DateFormat format = null;
        format = locale == null ? DateFormat.getDateInstance(3) : DateFormat.getDateInstance(3, locale);
        if (timeZone != null) {
            format.setTimeZone(timeZone);
        }
        return format;
    }

    private DateFormat getFormat(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        if (this.timeZone != null) {
            format.setTimeZone(this.timeZone);
        }
        return format;
    }

    private Calendar parse(Class<?> sourceType, Class<?> targetType, String value) throws Exception {
        Exception firstEx = null;
        for (String pattern : this.patterns) {
            try {
                DateFormat format = this.getFormat(pattern);
                Calendar calendar = this.parse(sourceType, targetType, value, format);
                return calendar;
            }
            catch (Exception ex) {
                if (firstEx != null) continue;
                firstEx = ex;
            }
        }
        if (this.patterns.length > 1) {
            throw new ConversionException("Error converting '" + this.toString(sourceType) + "' to '" + this.toString(targetType) + "' using  patterns '" + this.displayPatterns + "'");
        }
        throw firstEx;
    }

    private Calendar parse(Class<?> sourceType, Class<?> targetType, String value, DateFormat format) {
        this.logFormat("Parsing", format);
        format.setLenient(false);
        ParsePosition pos = new ParsePosition(0);
        Date parsedDate = format.parse(value, pos);
        if (pos.getErrorIndex() >= 0 || pos.getIndex() != value.length() || parsedDate == null) {
            String msg = "Error converting '" + this.toString(sourceType) + "' to '" + this.toString(targetType) + "'";
            if (format instanceof SimpleDateFormat) {
                msg = msg + " using pattern '" + ((SimpleDateFormat)format).toPattern() + "'";
            }
            if (this.log().isDebugEnabled()) {
                this.log().debug((Object)("    " + msg));
            }
            throw new ConversionException(msg);
        }
        Calendar calendar = format.getCalendar();
        return calendar;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(this.toString(this.getClass()));
        buffer.append("[UseDefault=");
        buffer.append(this.isUseDefault());
        buffer.append(", UseLocaleFormat=");
        buffer.append(this.useLocaleFormat);
        if (this.displayPatterns != null) {
            buffer.append(", Patterns={");
            buffer.append(this.displayPatterns);
            buffer.append('}');
        }
        if (this.locale != null) {
            buffer.append(", Locale=");
            buffer.append(this.locale);
        }
        if (this.timeZone != null) {
            buffer.append(", TimeZone=");
            buffer.append(this.timeZone);
        }
        buffer.append(']');
        return buffer.toString();
    }

    private void logFormat(String action, DateFormat format) {
        if (this.log().isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder(45);
            buffer.append("    ");
            buffer.append(action);
            buffer.append(" with Format");
            if (format instanceof SimpleDateFormat) {
                buffer.append("[");
                buffer.append(((SimpleDateFormat)format).toPattern());
                buffer.append("]");
            }
            buffer.append(" for ");
            if (this.locale == null) {
                buffer.append("default locale");
            } else {
                buffer.append("locale[");
                buffer.append(this.locale);
                buffer.append("]");
            }
            if (this.timeZone != null) {
                buffer.append(", TimeZone[");
                buffer.append(this.timeZone);
                buffer.append("]");
            }
            this.log().debug((Object)buffer.toString());
        }
    }
}

