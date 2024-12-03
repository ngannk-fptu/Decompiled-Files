/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.StreamReadCapability
 *  com.fasterxml.jackson.core.io.NumberInput
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonDeserializer
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.ReadableInstant
 */
package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import com.fasterxml.jackson.datatype.joda.deser.JodaDateDeserializerBase;
import java.io.IOException;
import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;

public class DateTimeDeserializer
extends JodaDateDeserializerBase<ReadableInstant> {
    private static final long serialVersionUID = 1L;

    public DateTimeDeserializer(Class<?> cls, JacksonJodaDateFormat format) {
        super(cls, format);
    }

    public DateTimeDeserializer() {
        super(DateTime.class, FormatConfig.DEFAULT_DATETIME_PARSER);
    }

    public static <T extends ReadableInstant> JsonDeserializer<T> forType(Class<T> cls) {
        return new DateTimeDeserializer(cls, FormatConfig.DEFAULT_DATETIME_PARSER);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new DateTimeDeserializer(this._valueClass, format);
    }

    public ReadableInstant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
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
        return (ReadableInstant)this._handleNotNumberOrString(p, ctxt);
    }

    protected ReadableInstant _fromString(JsonParser p, DeserializationContext ctxt, String value) throws IOException {
        if ((value = value.trim()).isEmpty()) {
            return (ReadableInstant)this._fromEmptyString(p, ctxt, value);
        }
        int ix = value.indexOf(91);
        if (ix > 0) {
            DateTimeZone tz;
            int ix2 = value.lastIndexOf(93);
            String tzId = ix2 < ix ? value.substring(ix + 1) : value.substring(ix + 1, ix2);
            try {
                tz = DateTimeZone.forID((String)tzId);
            }
            catch (IllegalArgumentException e) {
                ctxt.reportInputMismatch(this.handledType(), "Unknown DateTimeZone id '%s'", new Object[]{tzId});
                tz = null;
            }
            value = value.substring(0, ix);
            DateTime result = this._format.createParser(ctxt).withZone(tz).parseDateTime(value);
            if (this._format.shouldAdjustToContextTimeZone(ctxt)) {
                result = result.withZone(this._format.getTimeZone());
            }
            return result;
        }
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS) && this._isValidTimestampString(value)) {
            return this._fromTimestamp(ctxt, NumberInput.parseLong((String)value));
        }
        return this._format.createParser(ctxt).parseDateTime(value);
    }

    protected DateTime _fromTimestamp(DeserializationContext ctxt, long ts) {
        DateTimeZone tz = this._format.isTimezoneExplicit() ? this._format.getTimeZone() : DateTimeZone.forTimeZone((TimeZone)ctxt.getTimeZone());
        return new DateTime(ts, tz);
    }
}

