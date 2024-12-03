/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.StreamReadCapability
 *  com.fasterxml.jackson.core.io.NumberInput
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonDeserializer
 *  org.joda.time.Instant
 *  org.joda.time.format.DateTimeFormatter
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
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;

public class InstantDeserializer
extends JodaDateDeserializerBase<Instant> {
    private static final long serialVersionUID = 1L;

    public InstantDeserializer() {
        this(FormatConfig.DEFAULT_DATETIME_PARSER);
    }

    public InstantDeserializer(JacksonJodaDateFormat format) {
        super(Instant.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new InstantDeserializer(format);
    }

    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
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
        return (Instant)this._handleNotNumberOrString(p, ctxt);
    }

    protected Instant _fromString(JsonParser p, DeserializationContext ctxt, String value) throws IOException {
        if ((value = value.trim()).isEmpty()) {
            return (Instant)this._fromEmptyString(p, ctxt, value);
        }
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS) && this._isValidTimestampString(value)) {
            return this._fromTimestamp(ctxt, NumberInput.parseLong((String)value));
        }
        return Instant.parse((String)value, (DateTimeFormatter)this._format.createParser(ctxt));
    }

    protected Instant _fromTimestamp(DeserializationContext ctxt, long ts) {
        return new Instant(ts);
    }
}

