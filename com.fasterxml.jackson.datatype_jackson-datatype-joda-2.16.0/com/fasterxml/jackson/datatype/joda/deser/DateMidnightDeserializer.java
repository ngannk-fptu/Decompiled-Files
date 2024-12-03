/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.JsonToken
 *  com.fasterxml.jackson.core.StreamReadCapability
 *  com.fasterxml.jackson.core.io.NumberInput
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  org.joda.time.DateMidnight
 *  org.joda.time.DateTimeZone
 *  org.joda.time.LocalDate
 */
package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import com.fasterxml.jackson.datatype.joda.deser.JodaDateDeserializerBase;
import java.io.IOException;
import java.util.TimeZone;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

@Deprecated
public class DateMidnightDeserializer
extends JodaDateDeserializerBase<DateMidnight> {
    private static final long serialVersionUID = 1L;

    public DateMidnightDeserializer() {
        this(FormatConfig.DEFAULT_DATEONLY_FORMAT);
    }

    public DateMidnightDeserializer(JacksonJodaDateFormat format) {
        super(DateMidnight.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new DateMidnightDeserializer(format);
    }

    public DateMidnight deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.isExpectedStartArrayToken()) {
            p.nextToken();
            int year = p.getIntValue();
            p.nextToken();
            int month = p.getIntValue();
            p.nextToken();
            int day = p.getIntValue();
            if (p.nextToken() != JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(p, this.handledType(), JsonToken.END_ARRAY, "after DateMidnight ints");
            }
            DateTimeZone tz = this._format.isTimezoneExplicit() ? this._format.getTimeZone() : DateTimeZone.forTimeZone((TimeZone)ctxt.getTimeZone());
            return new DateMidnight(year, month, day, tz);
        }
        switch (p.currentTokenId()) {
            case 7: {
                return new DateMidnight(p.getLongValue());
            }
            case 6: {
                return this._fromString(p, ctxt, p.getText());
            }
        }
        throw ctxt.wrongTokenException(p, this.handledType(), JsonToken.START_ARRAY, "expected JSON Array, Number or String");
    }

    protected DateMidnight _fromString(JsonParser p, DeserializationContext ctxt, String value) throws IOException {
        if ((value = value.trim()).isEmpty()) {
            return (DateMidnight)this._fromEmptyString(p, ctxt, value);
        }
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS) && this._isValidTimestampString(value)) {
            return this._fromTimestamp(ctxt, NumberInput.parseLong((String)value));
        }
        LocalDate local = this._format.createParser(ctxt).parseLocalDate(value);
        if (local == null) {
            return null;
        }
        return local.toDateMidnight();
    }

    protected DateMidnight _fromTimestamp(DeserializationContext ctxt, long ts) {
        return new DateMidnight(ts);
    }
}

