/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  com.fasterxml.jackson.databind.SerializerProvider
 */
package com.fasterxml.jackson.datatype.joda.cfg;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.util.Locale;
import java.util.TimeZone;

abstract class JacksonJodaFormatBase {
    protected static final Locale DEFAULT_LOCALE = Locale.getDefault();
    protected final Boolean _useTimestamp;
    protected final Locale _locale;
    protected final boolean _explicitLocale;

    protected JacksonJodaFormatBase(Boolean useTimestamp, Locale locale, boolean explicitLocale) {
        this._useTimestamp = useTimestamp;
        this._locale = locale;
        this._explicitLocale = explicitLocale;
    }

    protected JacksonJodaFormatBase() {
        this._useTimestamp = null;
        this._locale = DEFAULT_LOCALE;
        this._explicitLocale = false;
    }

    protected JacksonJodaFormatBase(JacksonJodaFormatBase base) {
        this._useTimestamp = base._useTimestamp;
        this._locale = base._locale;
        this._explicitLocale = base._explicitLocale;
    }

    protected JacksonJodaFormatBase(JacksonJodaFormatBase base, Boolean useTimestamp) {
        this._useTimestamp = useTimestamp;
        this._locale = base._locale;
        this._explicitLocale = base._explicitLocale;
    }

    protected JacksonJodaFormatBase(JacksonJodaFormatBase base, TimeZone jdkTimezone) {
        this._useTimestamp = base._useTimestamp;
        this._locale = base._locale;
        this._explicitLocale = base._explicitLocale;
    }

    protected JacksonJodaFormatBase(JacksonJodaFormatBase base, Locale locale) {
        this._useTimestamp = base._useTimestamp;
        if (locale == null) {
            this._locale = DEFAULT_LOCALE;
            this._explicitLocale = false;
        } else {
            this._locale = locale;
            this._explicitLocale = true;
        }
    }

    public boolean useTimestamp(SerializerProvider provider, SerializationFeature feat) {
        if (this._useTimestamp != null) {
            return this._useTimestamp;
        }
        return provider.isEnabled(feat);
    }
}

