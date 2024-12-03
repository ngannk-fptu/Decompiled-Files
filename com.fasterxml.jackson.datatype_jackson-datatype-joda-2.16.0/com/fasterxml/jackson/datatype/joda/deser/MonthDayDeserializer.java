/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.JsonToken
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonDeserializer
 *  org.joda.time.MonthDay
 *  org.joda.time.format.DateTimeFormatter
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
import org.joda.time.MonthDay;
import org.joda.time.format.DateTimeFormatter;

public class MonthDayDeserializer
extends JodaDateDeserializerBase<MonthDay> {
    private static final long serialVersionUID = 1L;

    public MonthDayDeserializer() {
        this(FormatConfig.DEFAULT_MONTH_DAY_FORMAT);
    }

    public MonthDayDeserializer(JacksonJodaDateFormat format) {
        super(MonthDay.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new MonthDayDeserializer(format);
    }

    public MonthDay deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return this._fromString(p, ctxt, p.getText());
        }
        if (p.isExpectedStartObjectToken()) {
            return this._fromString(p, ctxt, ctxt.extractScalarFromObject(p, (JsonDeserializer)this, this.handledType()));
        }
        return (MonthDay)ctxt.handleUnexpectedToken(this.handledType(), p.currentToken(), p, "expected JSON String", new Object[0]);
    }

    protected MonthDay _fromString(JsonParser p, DeserializationContext ctxt, String value) throws IOException {
        if ((value = value.trim()).isEmpty()) {
            return (MonthDay)this._fromEmptyString(p, ctxt, value);
        }
        return MonthDay.parse((String)value, (DateTimeFormatter)this._format.createParser(ctxt));
    }
}

