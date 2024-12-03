/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.SerializerProvider
 *  org.joda.time.Period
 *  org.joda.time.format.PeriodFormatter
 */
package com.fasterxml.jackson.datatype.joda.cfg;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaFormatBase;
import java.io.IOException;
import java.util.Locale;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;

public class JacksonJodaPeriodFormat
extends JacksonJodaFormatBase {
    protected final PeriodFormatter _formatter;

    public JacksonJodaPeriodFormat(PeriodFormatter defaultFormatter) {
        this._formatter = defaultFormatter;
    }

    public JacksonJodaPeriodFormat(JacksonJodaPeriodFormat base, Locale locale) {
        super((JacksonJodaFormatBase)base, locale);
        PeriodFormatter f = base._formatter;
        if (locale != null) {
            f = f.withLocale(locale);
        }
        this._formatter = f;
    }

    public JacksonJodaPeriodFormat(JacksonJodaPeriodFormat base, Boolean useTimestamp) {
        super((JacksonJodaFormatBase)base, useTimestamp);
        this._formatter = base._formatter;
    }

    public PeriodFormatter nativeFormatter() {
        return this._formatter;
    }

    public JacksonJodaPeriodFormat withUseTimestamp(Boolean useTimestamp) {
        if (this._useTimestamp != null && this._useTimestamp.equals(useTimestamp)) {
            return this;
        }
        return new JacksonJodaPeriodFormat(this, useTimestamp);
    }

    public JacksonJodaPeriodFormat withFormat(String format) {
        return this;
    }

    public JacksonJodaPeriodFormat withLocale(Locale locale) {
        if (locale == null || this._locale != null && this._locale.equals(locale)) {
            return this;
        }
        return new JacksonJodaPeriodFormat(this, locale);
    }

    public PeriodFormatter createFormatter(SerializerProvider provider) {
        Locale loc;
        PeriodFormatter formatter = this._formatter;
        if (!this._explicitLocale && (loc = provider.getLocale()) != null && !loc.equals(this._locale)) {
            formatter = formatter.withLocale(loc);
        }
        return formatter;
    }

    public Period parsePeriod(DeserializationContext ctxt, String str) throws IOException {
        return this._formatter.parsePeriod(str);
    }
}

