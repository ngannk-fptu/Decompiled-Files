/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.datetime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.springframework.format.Formatter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class DateFormatter
implements Formatter<Date> {
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final Map<DateTimeFormat.ISO, String> ISO_PATTERNS;
    @Nullable
    private Object source;
    @Nullable
    private String pattern;
    @Nullable
    private String[] fallbackPatterns;
    private int style = 2;
    @Nullable
    private String stylePattern;
    @Nullable
    private DateTimeFormat.ISO iso;
    @Nullable
    private TimeZone timeZone;
    private boolean lenient = false;

    public DateFormatter() {
    }

    public DateFormatter(String pattern) {
        this.pattern = pattern;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setFallbackPatterns(String ... fallbackPatterns) {
        this.fallbackPatterns = fallbackPatterns;
    }

    public void setIso(DateTimeFormat.ISO iso) {
        this.iso = iso;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public void setStylePattern(String stylePattern) {
        this.stylePattern = stylePattern;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    @Override
    public String print(Date date, Locale locale) {
        return this.getDateFormat(locale).format(date);
    }

    @Override
    public Date parse(String text, Locale locale) throws ParseException {
        try {
            return this.getDateFormat(locale).parse(text);
        }
        catch (ParseException ex) {
            if (!ObjectUtils.isEmpty(this.fallbackPatterns)) {
                for (String pattern : this.fallbackPatterns) {
                    try {
                        DateFormat dateFormat = this.configureDateFormat(new SimpleDateFormat(pattern, locale));
                        if (this.iso != null && this.iso != DateTimeFormat.ISO.NONE) {
                            dateFormat.setTimeZone(UTC);
                        }
                        return dateFormat.parse(text);
                    }
                    catch (ParseException parseException) {
                    }
                }
            }
            if (this.source != null) {
                ParseException parseException = new ParseException(String.format("Unable to parse date time value \"%s\" using configuration from %s", text, this.source), ex.getErrorOffset());
                parseException.initCause(ex);
                throw parseException;
            }
            throw ex;
        }
    }

    protected DateFormat getDateFormat(Locale locale) {
        return this.configureDateFormat(this.createDateFormat(locale));
    }

    private DateFormat configureDateFormat(DateFormat dateFormat) {
        if (this.timeZone != null) {
            dateFormat.setTimeZone(this.timeZone);
        }
        dateFormat.setLenient(this.lenient);
        return dateFormat;
    }

    private DateFormat createDateFormat(Locale locale) {
        if (StringUtils.hasLength(this.pattern)) {
            return new SimpleDateFormat(this.pattern, locale);
        }
        if (this.iso != null && this.iso != DateTimeFormat.ISO.NONE) {
            String pattern = ISO_PATTERNS.get((Object)this.iso);
            if (pattern == null) {
                throw new IllegalStateException("Unsupported ISO format " + (Object)((Object)this.iso));
            }
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            format.setTimeZone(UTC);
            return format;
        }
        if (StringUtils.hasLength(this.stylePattern)) {
            int dateStyle = this.getStylePatternForChar(0);
            int timeStyle = this.getStylePatternForChar(1);
            if (dateStyle != -1 && timeStyle != -1) {
                return DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
            }
            if (dateStyle != -1) {
                return DateFormat.getDateInstance(dateStyle, locale);
            }
            if (timeStyle != -1) {
                return DateFormat.getTimeInstance(timeStyle, locale);
            }
            throw new IllegalStateException("Unsupported style pattern '" + this.stylePattern + "'");
        }
        return DateFormat.getDateInstance(this.style, locale);
    }

    private int getStylePatternForChar(int index) {
        if (this.stylePattern != null && this.stylePattern.length() > index) {
            switch (this.stylePattern.charAt(index)) {
                case 'S': {
                    return 3;
                }
                case 'M': {
                    return 2;
                }
                case 'L': {
                    return 1;
                }
                case 'F': {
                    return 0;
                }
                case '-': {
                    return -1;
                }
            }
        }
        throw new IllegalStateException("Unsupported style pattern '" + this.stylePattern + "'");
    }

    static {
        EnumMap<DateTimeFormat.ISO, String> formats = new EnumMap<DateTimeFormat.ISO, String>(DateTimeFormat.ISO.class);
        formats.put(DateTimeFormat.ISO.DATE, "yyyy-MM-dd");
        formats.put(DateTimeFormat.ISO.TIME, "HH:mm:ss.SSSXXX");
        formats.put(DateTimeFormat.ISO.DATE_TIME, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        ISO_PATTERNS = Collections.unmodifiableMap(formats);
    }
}

