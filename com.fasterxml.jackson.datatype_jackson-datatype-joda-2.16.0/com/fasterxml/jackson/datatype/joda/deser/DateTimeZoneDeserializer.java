/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.StreamReadCapability
 *  com.fasterxml.jackson.core.io.NumberInput
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonDeserializer
 *  org.joda.time.DateTimeZone
 */
package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.JodaDeserializerBase;
import java.io.IOException;
import org.joda.time.DateTimeZone;

public class DateTimeZoneDeserializer
extends JodaDeserializerBase<DateTimeZone> {
    private static final long serialVersionUID = 1L;

    public DateTimeZoneDeserializer() {
        super(DateTimeZone.class);
    }

    public DateTimeZone deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.currentTokenId()) {
            case 7: {
                return this._fromOffset(ctxt, p.getIntValue());
            }
            case 6: {
                return this._fromString(p, ctxt, p.getText());
            }
            case 1: {
                return this._fromString(p, ctxt, ctxt.extractScalarFromObject(p, (JsonDeserializer)this, this.handledType()));
            }
        }
        return (DateTimeZone)this._handleNotNumberOrString(p, ctxt);
    }

    protected DateTimeZone _fromString(JsonParser p, DeserializationContext ctxt, String value) throws IOException {
        if ((value = value.trim()).isEmpty()) {
            return (DateTimeZone)this._fromEmptyString(p, ctxt, value);
        }
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS) && this._isValidTimestampString(value)) {
            return this._fromOffset(ctxt, NumberInput.parseInt((String)value));
        }
        return DateTimeZone.forID((String)value);
    }

    protected DateTimeZone _fromOffset(DeserializationContext ctxt, int offset) {
        return DateTimeZone.forOffsetHours((int)offset);
    }
}

