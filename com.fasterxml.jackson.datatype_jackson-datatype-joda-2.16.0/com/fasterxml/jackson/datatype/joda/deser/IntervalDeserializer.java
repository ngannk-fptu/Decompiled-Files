/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.JsonToken
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonDeserializer
 *  org.joda.time.DateTimeZone
 *  org.joda.time.Interval
 */
package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import com.fasterxml.jackson.datatype.joda.deser.JodaDateDeserializerBase;
import java.io.IOException;
import java.util.TimeZone;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

public class IntervalDeserializer
extends JodaDateDeserializerBase<Interval> {
    private static final long serialVersionUID = 1L;

    public IntervalDeserializer() {
        this(FormatConfig.DEFAULT_DATETIME_PARSER);
    }

    public IntervalDeserializer(JacksonJodaDateFormat format) {
        super(Interval.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new IntervalDeserializer(format);
    }

    public Interval deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return this._fromString(p, ctxt, p.getText());
        }
        if (p.isExpectedStartObjectToken()) {
            return this._fromString(p, ctxt, ctxt.extractScalarFromObject(p, (JsonDeserializer)this, this.handledType()));
        }
        return (Interval)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p.currentToken(), p, "expected JSON String", new Object[0]);
    }

    protected Interval _fromString(JsonParser p, DeserializationContext ctxt, String value) throws IOException {
        DateTimeZone tz;
        Interval result;
        boolean hasSlash;
        if ((value = value.trim()).isEmpty()) {
            return (Interval)this._fromEmptyString(p, ctxt, value);
        }
        int index = value.indexOf(47, 1);
        boolean bl = hasSlash = index > 0;
        if (!hasSlash) {
            index = value.indexOf(45, 1);
        }
        if (index < 0) {
            throw ctxt.weirdStringException(value, this.handledType(), "no slash or hyphen found to separate start, end");
        }
        String str = value.substring(0, index);
        try {
            if (hasSlash) {
                result = Interval.parseWithOffset((String)value);
            } else {
                long start = Long.valueOf(str);
                str = value.substring(index + 1);
                long end = Long.valueOf(str);
                result = new Interval(start, end);
            }
        }
        catch (NumberFormatException e) {
            return (Interval)ctxt.handleWeirdStringValue(this.handledType(), str, "Failed to parse number from '%s' (full source String '%s')", new Object[]{str, value});
        }
        DateTimeZone contextTimezone = this._format.shouldAdjustToContextTimeZone(ctxt) ? DateTimeZone.forTimeZone((TimeZone)ctxt.getTimeZone()) : null;
        DateTimeZone dateTimeZone = tz = this._format.isTimezoneExplicit() ? this._format.getTimeZone() : contextTimezone;
        if (tz != null && !tz.equals((Object)result.getStart().getZone())) {
            result = new Interval(result.getStartMillis(), result.getEndMillis(), tz);
        }
        return result;
    }
}

