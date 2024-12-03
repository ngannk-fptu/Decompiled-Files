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
 *  org.joda.time.LocalDateTime
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
import org.joda.time.LocalDateTime;

public class LocalDateTimeDeserializer
extends JodaDateDeserializerBase<LocalDateTime> {
    private static final long serialVersionUID = 1L;

    public LocalDateTimeDeserializer() {
        this(FormatConfig.DEFAULT_LOCAL_DATETIME_PARSER);
    }

    public LocalDateTimeDeserializer(JacksonJodaDateFormat format) {
        super(LocalDateTime.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new LocalDateTimeDeserializer(format);
    }

    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
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
        return (LocalDateTime)ctxt.handleUnexpectedToken(this.handledType(), p.currentToken(), p, "expected String, Number or JSON Array", new Object[0]);
    }

    protected LocalDateTime _fromString(JsonParser p, DeserializationContext ctxt, String value) throws IOException {
        if ((value = value.trim()).isEmpty()) {
            return (LocalDateTime)this._fromEmptyString(p, ctxt, value);
        }
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS) && this._isValidTimestampString(value)) {
            return this._fromTimestamp(ctxt, NumberInput.parseLong((String)value));
        }
        return this._format.createParser(ctxt).parseLocalDateTime(value);
    }

    protected LocalDateTime _fromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.nextToken();
        LocalDateTime dt = null;
        if (t.isNumeric()) {
            int year = p.getIntValue();
            t = p.nextToken();
            if (t.isNumeric()) {
                int month = p.getIntValue();
                t = p.nextToken();
                if (t.isNumeric()) {
                    int day = p.getIntValue();
                    t = p.nextToken();
                    if (t.isNumeric()) {
                        int hour = p.getIntValue();
                        t = p.nextToken();
                        if (t.isNumeric()) {
                            int minute = p.getIntValue();
                            t = p.nextToken();
                            if (t.isNumeric()) {
                                int second = p.getIntValue();
                                t = p.nextToken();
                                int millisecond = 0;
                                if (t.isNumeric()) {
                                    millisecond = p.getIntValue();
                                    t = p.nextToken();
                                }
                                dt = new LocalDateTime(year, month, day, hour, minute, second, millisecond);
                            }
                        }
                    }
                }
            }
        }
        if (t == JsonToken.END_ARRAY) {
            return dt;
        }
        throw ctxt.wrongTokenException(p, this.handledType(), JsonToken.END_ARRAY, "after LocalDateTime ints");
    }

    protected LocalDateTime _fromTimestamp(DeserializationContext ctxt, long ts) {
        DateTimeZone tz = this._format.isTimezoneExplicit() ? this._format.getTimeZone() : DateTimeZone.forTimeZone((TimeZone)ctxt.getTimeZone());
        return new LocalDateTime(ts, tz);
    }
}

