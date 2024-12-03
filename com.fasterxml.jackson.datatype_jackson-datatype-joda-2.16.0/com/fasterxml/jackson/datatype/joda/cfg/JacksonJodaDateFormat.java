/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonFormat$Feature
 *  com.fasterxml.jackson.annotation.JsonFormat$Value
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.DeserializationFeature
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  com.fasterxml.jackson.databind.SerializerProvider
 *  org.joda.time.DateTimeZone
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package com.fasterxml.jackson.datatype.joda.cfg;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaFormatBase;
import java.util.Locale;
import java.util.TimeZone;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class JacksonJodaDateFormat
extends JacksonJodaFormatBase {
    private static final String JODA_STYLE_CHARS = "SMLF-";
    protected final DateTimeFormatter _formatter;
    protected final DateTimeFormatter _formatterWithOffsetParsed;
    protected final TimeZone _jdkTimezone;
    protected transient DateTimeZone _jodaTimezone;
    protected final boolean _explicitTimezone;
    protected final Boolean _adjustToContextTZOverride;
    protected final Boolean _writeZoneId;

    public JacksonJodaDateFormat(DateTimeFormatter defaultFormatter) {
        this._formatter = defaultFormatter;
        this._formatterWithOffsetParsed = this._formatter.withOffsetParsed();
        DateTimeZone tz = defaultFormatter.getZone();
        this._jdkTimezone = tz == null ? null : tz.toTimeZone();
        this._explicitTimezone = false;
        this._adjustToContextTZOverride = null;
        this._writeZoneId = null;
    }

    public JacksonJodaDateFormat(JacksonJodaDateFormat base, Boolean useTimestamp) {
        super((JacksonJodaFormatBase)base, useTimestamp);
        this._formatter = base._formatter;
        this._formatterWithOffsetParsed = this._formatter.withOffsetParsed();
        this._jdkTimezone = base._jdkTimezone;
        this._explicitTimezone = base._explicitTimezone;
        this._adjustToContextTZOverride = base._adjustToContextTZOverride;
        this._writeZoneId = base._writeZoneId;
    }

    public JacksonJodaDateFormat(JacksonJodaDateFormat base, DateTimeFormatter formatter) {
        super(base);
        this._formatter = formatter;
        this._formatterWithOffsetParsed = this._formatter.withOffsetParsed();
        this._jdkTimezone = base._jdkTimezone;
        this._explicitTimezone = base._explicitTimezone;
        this._adjustToContextTZOverride = base._adjustToContextTZOverride;
        this._writeZoneId = base._writeZoneId;
    }

    public JacksonJodaDateFormat(JacksonJodaDateFormat base, TimeZone jdkTimezone) {
        super((JacksonJodaFormatBase)base, jdkTimezone);
        this._formatter = base._formatter.withZone(DateTimeZone.forTimeZone((TimeZone)jdkTimezone));
        this._formatterWithOffsetParsed = this._formatter.withOffsetParsed();
        this._jdkTimezone = jdkTimezone;
        this._explicitTimezone = true;
        this._adjustToContextTZOverride = base._adjustToContextTZOverride;
        this._writeZoneId = base._writeZoneId;
    }

    public JacksonJodaDateFormat(JacksonJodaDateFormat base, Locale locale) {
        super((JacksonJodaFormatBase)base, locale);
        this._formatter = base._formatter.withLocale(locale);
        this._formatterWithOffsetParsed = this._formatter.withOffsetParsed();
        this._jdkTimezone = base._jdkTimezone;
        this._explicitTimezone = base._explicitTimezone;
        this._adjustToContextTZOverride = base._adjustToContextTZOverride;
        this._writeZoneId = base._writeZoneId;
    }

    protected JacksonJodaDateFormat(JacksonJodaDateFormat base, Boolean adjustToContextTZOverride, Boolean writeZoneId) {
        super(base);
        this._formatter = base._formatter;
        this._formatterWithOffsetParsed = this._formatter.withOffsetParsed();
        this._jdkTimezone = base._jdkTimezone;
        this._explicitTimezone = base._explicitTimezone;
        this._adjustToContextTZOverride = adjustToContextTZOverride;
        this._writeZoneId = writeZoneId;
    }

    public JacksonJodaDateFormat with(JsonFormat.Value ann) {
        JacksonJodaDateFormat format = this;
        format = format.withLocale(ann.getLocale());
        format = format.withTimeZone(ann.getTimeZone());
        format = format.withFormat(ann.getPattern());
        Boolean adjustTZ = ann.getFeature(JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        Boolean writeZoneId = ann.getFeature(JsonFormat.Feature.WRITE_DATES_WITH_ZONE_ID);
        if (adjustTZ != this._adjustToContextTZOverride || writeZoneId != this._writeZoneId) {
            format = new JacksonJodaDateFormat(format, adjustTZ, writeZoneId);
        }
        return format;
    }

    public JacksonJodaDateFormat withUseTimestamp(Boolean useTimestamp) {
        if (this._useTimestamp != null && this._useTimestamp.equals(useTimestamp)) {
            return this;
        }
        return new JacksonJodaDateFormat(this, useTimestamp);
    }

    public JacksonJodaDateFormat withFormat(String format) {
        if (format == null || format.isEmpty()) {
            return this;
        }
        DateTimeFormatter formatter = JacksonJodaDateFormat._isStyle(format) ? DateTimeFormat.forStyle((String)format) : DateTimeFormat.forPattern((String)format);
        if (this._locale != null) {
            formatter = formatter.withLocale(this._locale);
        }
        return new JacksonJodaDateFormat(this, formatter);
    }

    public JacksonJodaDateFormat withTimeZone(TimeZone tz) {
        if (tz == null || this._jdkTimezone != null && this._jdkTimezone.equals(tz)) {
            return this;
        }
        return new JacksonJodaDateFormat(this, tz);
    }

    public JacksonJodaDateFormat withLocale(Locale locale) {
        if (locale == null || this._locale != null && this._locale.equals(locale)) {
            return this;
        }
        return new JacksonJodaDateFormat(this, locale);
    }

    public JacksonJodaDateFormat withAdjustToContextTZOverride(Boolean adjustToContextTZOverride) {
        if (adjustToContextTZOverride == this._adjustToContextTZOverride) {
            return this;
        }
        return new JacksonJodaDateFormat(this, adjustToContextTZOverride, this._writeZoneId);
    }

    public JacksonJodaDateFormat withWriteZoneId(Boolean writeZoneId) {
        if (writeZoneId == this._writeZoneId) {
            return this;
        }
        return new JacksonJodaDateFormat(this, this._adjustToContextTZOverride, writeZoneId);
    }

    public DateTimeZone getTimeZone() {
        DateTimeZone tz;
        if (this._jodaTimezone != null) {
            return this._jodaTimezone;
        }
        if (this._jdkTimezone == null) {
            return null;
        }
        this._jodaTimezone = tz = DateTimeZone.forTimeZone((TimeZone)this._jdkTimezone);
        return tz;
    }

    public Locale getLocale() {
        return this._locale;
    }

    public DateTimeFormatter rawFormatter() {
        return this._formatter;
    }

    public DateTimeFormatter createFormatter(SerializerProvider ctxt) {
        TimeZone tz;
        DateTimeFormatter formatter = this.createFormatterWithLocale(ctxt);
        if (!this._explicitTimezone && (tz = ctxt.getTimeZone()) != null && !tz.equals(this._jdkTimezone)) {
            formatter = formatter.withZone(DateTimeZone.forTimeZone((TimeZone)tz));
        }
        return formatter;
    }

    public DateTimeFormatter createFormatterWithLocale(SerializerProvider ctxt) {
        Locale loc;
        DateTimeFormatter formatter = this._formatter;
        if (!this._explicitLocale && (loc = ctxt.getLocale()) != null && !loc.equals(this._locale)) {
            formatter = formatter.withLocale(loc);
        }
        return formatter;
    }

    public DateTimeFormatter createParser(DeserializationContext ctxt) {
        Locale loc;
        DateTimeFormatter formatter = this._formatter;
        if (!this._explicitTimezone) {
            if (this.shouldAdjustToContextTimeZone(ctxt)) {
                TimeZone tz = ctxt.getTimeZone();
                if (tz != null && !tz.equals(this._jdkTimezone)) {
                    formatter = formatter.withZone(DateTimeZone.forTimeZone((TimeZone)tz));
                }
            } else {
                formatter = this._formatterWithOffsetParsed;
            }
        }
        if (!this._explicitLocale && (loc = ctxt.getLocale()) != null && !loc.equals(this._locale)) {
            formatter = formatter.withLocale(loc);
        }
        return formatter;
    }

    public boolean shouldAdjustToContextTimeZone(DeserializationContext ctxt) {
        return this._adjustToContextTZOverride != null ? this._adjustToContextTZOverride.booleanValue() : ctxt.isEnabled(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    }

    public boolean shouldWriteWithZoneId(SerializerProvider ctxt) {
        return this._writeZoneId != null ? this._writeZoneId.booleanValue() : ctxt.isEnabled(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
    }

    public boolean isTimezoneExplicit() {
        return this._explicitTimezone;
    }

    protected static boolean _isStyle(String formatStr) {
        if (formatStr.length() != 2) {
            return false;
        }
        return JODA_STYLE_CHARS.indexOf(formatStr.charAt(0)) >= 0 && JODA_STYLE_CHARS.indexOf(formatStr.charAt(0)) >= 0;
    }

    public String toString() {
        return String.format("[JacksonJodaFormat, explicitTZ? %s, JDK tz = %s, formatter = %s]", this._explicitTimezone, this._jdkTimezone.getID(), this._formatter);
    }
}

