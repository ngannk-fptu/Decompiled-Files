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
 *  org.joda.time.LocalTime
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
import org.joda.time.LocalTime;

public class LocalTimeDeserializer
extends JodaDateDeserializerBase<LocalTime> {
    private static final long serialVersionUID = 1L;

    public LocalTimeDeserializer() {
        this(FormatConfig.DEFAULT_LOCAL_TIMEONLY_PARSER);
    }

    public LocalTimeDeserializer(JacksonJodaDateFormat format) {
        super(LocalTime.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new LocalTimeDeserializer(format);
    }

    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.currentTokenId()) {
            case 7: {
                return new LocalTime(p.getLongValue());
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
        return (LocalTime)ctxt.handleUnexpectedToken(this.handledType(), p.currentToken(), p, "expected JSON Array, String or Number", new Object[0]);
    }

    protected LocalTime _fromString(JsonParser p, DeserializationContext ctxt, String value) throws IOException {
        if ((value = value.trim()).isEmpty()) {
            return (LocalTime)this._fromEmptyString(p, ctxt, value);
        }
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS) && this._isValidTimestampString(value)) {
            return new LocalTime(NumberInput.parseLong((String)value));
        }
        return this._format.createParser(ctxt).parseLocalTime(value);
    }

    protected LocalTime _fromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        p.nextToken();
        int hour = p.getIntValue();
        p.nextToken();
        int minute = p.getIntValue();
        p.nextToken();
        int second = p.getIntValue();
        p.nextToken();
        int millis = 0;
        if (p.currentToken() != JsonToken.END_ARRAY) {
            millis = p.getIntValue();
            p.nextToken();
        }
        if (p.currentToken() != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(p, this.handledType(), JsonToken.END_ARRAY, "after LocalTime ints");
        }
        return new LocalTime(hour, minute, second, millis);
    }
}

