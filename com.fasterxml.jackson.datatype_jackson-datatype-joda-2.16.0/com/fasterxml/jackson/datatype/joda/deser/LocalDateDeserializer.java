/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.JsonToken
 *  com.fasterxml.jackson.core.StreamReadCapability
 *  com.fasterxml.jackson.core.io.NumberInput
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonDeserializer
 *  org.joda.time.DateTimeZone
 *  org.joda.time.LocalDate
 */
package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import com.fasterxml.jackson.datatype.joda.deser.JodaDateDeserializerBase;
import java.io.IOException;
import java.util.TimeZone;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

public class LocalDateDeserializer
extends JodaDateDeserializerBase<LocalDate> {
    private static final long serialVersionUID = 1L;

    public LocalDateDeserializer() {
        this(FormatConfig.DEFAULT_LOCAL_DATEONLY_FORMAT);
    }

    public LocalDateDeserializer(JacksonJodaDateFormat format) {
        super(LocalDate.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new LocalDateDeserializer(format);
    }

    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.currentTokenId()) {
            case 7: {
                return this._fromTimestamp(ctxt, p.getLongValue());
            }
            case 6: {
                return this._fromString(p, ctxt, p.getText());
            }
            case 1: {
                return this._fromString(p, ctxt, ctxt.extractScalarFromObject(p, (JsonDeserializer)this, this.handledType()));
            }
        }
        if (p.isExpectedStartArrayToken()) {
            return this._fromArray(p, ctxt);
        }
        return (LocalDate)ctxt.handleUnexpectedToken(this.handledType(), p.currentToken(), p, "expected String, Number or JSON Array", new Object[0]);
    }

    protected LocalDate _fromString(JsonParser p, DeserializationContext ctxt, String value) throws IOException {
        if ((value = value.trim()).isEmpty()) {
            return (LocalDate)this._fromEmptyString(p, ctxt, value);
        }
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS) && this._isValidTimestampString(value)) {
            return this._fromTimestamp(ctxt, NumberInput.parseLong((String)value));
        }
        return this._format.createParser(ctxt).parseLocalDate(value);
    }

    protected LocalDate _fromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        int day;
        int month;
        int year = p.nextIntValue(-1);
        if (year == -1) {
            year = this._parseIntPrimitive(p, ctxt);
        }
        if ((month = p.nextIntValue(-1)) == -1) {
            month = this._parseIntPrimitive(p, ctxt);
        }
        if ((day = p.nextIntValue(-1)) == -1) {
            day = this._parseIntPrimitive(p, ctxt);
        }
        if (p.nextToken() != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(p, this.handledType(), JsonToken.END_ARRAY, "after LocalDate ints");
        }
        return new LocalDate(year, month, day);
    }

    protected LocalDate _fromTimestamp(DeserializationContext ctxt, long ts) {
        DateTimeZone tz = this._format.isTimezoneExplicit() ? this._format.getTimeZone() : DateTimeZone.forTimeZone((TimeZone)ctxt.getTimeZone());
        return new LocalDate(ts, tz);
    }
}

