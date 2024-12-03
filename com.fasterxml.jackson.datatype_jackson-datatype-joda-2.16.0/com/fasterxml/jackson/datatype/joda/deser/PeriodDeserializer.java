/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.JsonToken
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonNode
 *  org.joda.time.Days
 *  org.joda.time.Hours
 *  org.joda.time.Minutes
 *  org.joda.time.Months
 *  org.joda.time.Period
 *  org.joda.time.ReadablePeriod
 *  org.joda.time.Seconds
 *  org.joda.time.Weeks
 *  org.joda.time.Years
 */
package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaPeriodFormat;
import com.fasterxml.jackson.datatype.joda.deser.JodaDeserializerBase;
import java.io.IOException;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.Years;

public class PeriodDeserializer
extends JodaDeserializerBase<ReadablePeriod> {
    private static final long serialVersionUID = 1L;
    private final JacksonJodaPeriodFormat _format = FormatConfig.DEFAULT_PERIOD_FORMAT;
    private final boolean _requireFullPeriod;

    public PeriodDeserializer() {
        this(true);
    }

    public PeriodDeserializer(boolean fullPeriod) {
        super(fullPeriod ? Period.class : ReadablePeriod.class);
        this._requireFullPeriod = fullPeriod;
    }

    public ReadablePeriod deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.currentToken();
        if (t == JsonToken.VALUE_STRING) {
            return this._fromString(p, ctxt, p.getText());
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return new Period(p.getLongValue());
        }
        if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME) {
            return (ReadablePeriod)ctxt.handleUnexpectedToken(this.handledType(), t, p, "expected JSON Number, String or Object", new Object[0]);
        }
        return this._fromObject(p, ctxt);
    }

    protected ReadablePeriod _fromString(JsonParser p, DeserializationContext ctxt, String value) throws IOException {
        if ((value = value.trim()).isEmpty()) {
            return (ReadablePeriod)this._fromEmptyString(p, ctxt, value);
        }
        return this._format.parsePeriod(ctxt, value);
    }

    protected ReadablePeriod _fromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
        Seconds rp;
        JsonNode treeNode = (JsonNode)p.readValueAsTree();
        String periodType = treeNode.path("fieldType").path("name").asText();
        String periodName = treeNode.path("periodType").path("name").asText();
        int periodValue = treeNode.path(periodType).asInt();
        if (periodName.equals("Seconds")) {
            rp = Seconds.seconds((int)periodValue);
        } else if (periodName.equals("Minutes")) {
            rp = Minutes.minutes((int)periodValue);
        } else if (periodName.equals("Hours")) {
            rp = Hours.hours((int)periodValue);
        } else if (periodName.equals("Days")) {
            rp = Days.days((int)periodValue);
        } else if (periodName.equals("Weeks")) {
            rp = Weeks.weeks((int)periodValue);
        } else if (periodName.equals("Months")) {
            rp = Months.months((int)periodValue);
        } else if (periodName.equals("Years")) {
            rp = Years.years((int)periodValue);
        } else {
            ctxt.reportInputMismatch(this.handledType(), "Don't know how to deserialize %s using periodName '%s'", new Object[]{this.handledType().getName(), periodName});
            return null;
        }
        if (this._requireFullPeriod && !(rp instanceof Period)) {
            rp = rp.toPeriod();
        }
        return rp;
    }
}

