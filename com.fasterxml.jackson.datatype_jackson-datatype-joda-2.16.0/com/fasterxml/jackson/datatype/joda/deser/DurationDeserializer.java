/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.StreamReadCapability
 *  com.fasterxml.jackson.core.io.NumberInput
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonDeserializer
 *  org.joda.time.Duration
 */
package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaPeriodFormat;
import com.fasterxml.jackson.datatype.joda.deser.JodaDeserializerBase;
import java.io.IOException;
import org.joda.time.Duration;

public class DurationDeserializer
extends JodaDeserializerBase<Duration> {
    private static final long serialVersionUID = 1L;
    protected final JacksonJodaPeriodFormat _format;

    public DurationDeserializer() {
        this(FormatConfig.DEFAULT_PERIOD_FORMAT);
    }

    public DurationDeserializer(JacksonJodaPeriodFormat format) {
        super(Duration.class);
        this._format = format;
    }

    public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
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
        return (Duration)this._handleNotNumberOrString(p, ctxt);
    }

    protected Duration _fromString(JsonParser p, DeserializationContext ctxt, String value) throws IOException {
        if ((value = value.trim()).isEmpty()) {
            return (Duration)this._fromEmptyString(p, ctxt, value);
        }
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS) && this._isValidTimestampString(value)) {
            return this._fromTimestamp(ctxt, NumberInput.parseLong((String)value));
        }
        return this._format.parsePeriod(ctxt, value).toStandardDuration();
    }

    protected Duration _fromTimestamp(DeserializationContext ctxt, long ts) {
        return new Duration(ts);
    }
}

