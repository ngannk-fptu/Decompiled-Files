/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.JsonSerializer
 *  org.codehaus.jackson.map.SerializerProvider
 */
package com.atlassian.audit.rest.model.converter;

import java.io.IOException;
import java.time.Period;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class PeriodSerializer
extends JsonSerializer<Period> {
    public void serialize(Period period, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("years", period.getYears());
        jsonGenerator.writeNumberField("months", period.getMonths());
        jsonGenerator.writeNumberField("days", period.getDays());
        jsonGenerator.writeEndObject();
    }
}

