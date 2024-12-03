/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  com.fasterxml.jackson.databind.SerializerProvider
 *  org.joda.time.LocalTime
 *  org.joda.time.ReadablePartial
 */
package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import com.fasterxml.jackson.datatype.joda.ser.JodaDateSerializerBase;
import java.io.IOException;
import org.joda.time.LocalTime;
import org.joda.time.ReadablePartial;

public class LocalTimeSerializer
extends JodaDateSerializerBase<LocalTime> {
    private static final long serialVersionUID = 1L;

    public LocalTimeSerializer() {
        this(FormatConfig.DEFAULT_LOCAL_TIMEONLY_PRINTER, 0);
    }

    public LocalTimeSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }

    public LocalTimeSerializer(JacksonJodaDateFormat format, int shapeOverride) {
        super(LocalTime.class, format, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, 3, shapeOverride);
    }

    public LocalTimeSerializer withFormat(JacksonJodaDateFormat formatter, int shapeOverride) {
        return new LocalTimeSerializer(formatter, shapeOverride);
    }

    public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (this._serializationShape(provider) == 1) {
            gen.writeString(this._format.createFormatter(provider).print((ReadablePartial)value));
            return;
        }
        gen.writeStartArray();
        gen.writeNumber(value.hourOfDay().get());
        gen.writeNumber(value.minuteOfHour().get());
        gen.writeNumber(value.secondOfMinute().get());
        gen.writeNumber(value.millisOfSecond().get());
        gen.writeEndArray();
    }
}

