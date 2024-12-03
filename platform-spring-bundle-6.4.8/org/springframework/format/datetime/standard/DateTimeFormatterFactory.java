/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.datetime.standard;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.TimeZone;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.standard.DateTimeFormatterUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class DateTimeFormatterFactory {
    @Nullable
    private String pattern;
    @Nullable
    private DateTimeFormat.ISO iso;
    @Nullable
    private FormatStyle dateStyle;
    @Nullable
    private FormatStyle timeStyle;
    @Nullable
    private TimeZone timeZone;

    public DateTimeFormatterFactory() {
    }

    public DateTimeFormatterFactory(String pattern) {
        this.pattern = pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setIso(DateTimeFormat.ISO iso) {
        this.iso = iso;
    }

    public void setDateStyle(FormatStyle dateStyle) {
        this.dateStyle = dateStyle;
    }

    public void setTimeStyle(FormatStyle timeStyle) {
        this.timeStyle = timeStyle;
    }

    public void setDateTimeStyle(FormatStyle dateTimeStyle) {
        this.dateStyle = dateTimeStyle;
        this.timeStyle = dateTimeStyle;
    }

    public void setStylePattern(String style) {
        Assert.isTrue(style.length() == 2, "Style pattern must consist of two characters");
        this.dateStyle = this.convertStyleCharacter(style.charAt(0));
        this.timeStyle = this.convertStyleCharacter(style.charAt(1));
    }

    @Nullable
    private FormatStyle convertStyleCharacter(char c) {
        switch (c) {
            case 'S': {
                return FormatStyle.SHORT;
            }
            case 'M': {
                return FormatStyle.MEDIUM;
            }
            case 'L': {
                return FormatStyle.LONG;
            }
            case 'F': {
                return FormatStyle.FULL;
            }
            case '-': {
                return null;
            }
        }
        throw new IllegalArgumentException("Invalid style character '" + c + "'");
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public DateTimeFormatter createDateTimeFormatter() {
        return this.createDateTimeFormatter(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }

    public DateTimeFormatter createDateTimeFormatter(DateTimeFormatter fallbackFormatter) {
        DateTimeFormatter dateTimeFormatter;
        block12: {
            block13: {
                block11: {
                    dateTimeFormatter = null;
                    if (!StringUtils.hasLength(this.pattern)) break block11;
                    dateTimeFormatter = DateTimeFormatterUtils.createStrictDateTimeFormatter(this.pattern);
                    break block12;
                }
                if (this.iso == null || this.iso == DateTimeFormat.ISO.NONE) break block13;
                switch (this.iso) {
                    case DATE: {
                        dateTimeFormatter = DateTimeFormatter.ISO_DATE;
                        break block12;
                    }
                    case TIME: {
                        dateTimeFormatter = DateTimeFormatter.ISO_TIME;
                        break block12;
                    }
                    case DATE_TIME: {
                        dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
                        break block12;
                    }
                    default: {
                        throw new IllegalStateException("Unsupported ISO format: " + (Object)((Object)this.iso));
                    }
                }
            }
            if (this.dateStyle != null && this.timeStyle != null) {
                dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(this.dateStyle, this.timeStyle);
            } else if (this.dateStyle != null) {
                dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(this.dateStyle);
            } else if (this.timeStyle != null) {
                dateTimeFormatter = DateTimeFormatter.ofLocalizedTime(this.timeStyle);
            }
        }
        if (dateTimeFormatter != null && this.timeZone != null) {
            dateTimeFormatter = dateTimeFormatter.withZone(this.timeZone.toZoneId());
        }
        return dateTimeFormatter != null ? dateTimeFormatter : fallbackFormatter;
    }
}

