/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  com.fasterxml.jackson.databind.SerializerProvider
 *  org.joda.time.DateTimeZone
 *  org.joda.time.LocalDateTime
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
import java.util.TimeZone;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.ReadablePartial;

public class LocalDateTimeSerializer
extends JodaDateSerializerBase<LocalDateTime> {
    private static final long serialVersionUID = 1L;

    public LocalDateTimeSerializer() {
        this(FormatConfig.DEFAULT_LOCAL_DATETIME_PRINTER, 0);
    }

    public LocalDateTimeSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }

    public LocalDateTimeSerializer(JacksonJodaDateFormat format, int shapeOverride) {
        super(LocalDateTime.class, format, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, 3, shapeOverride);
    }

    public LocalDateTimeSerializer withFormat(JacksonJodaDateFormat formatter, int shapeOverride) {
        return new LocalDateTimeSerializer(formatter, shapeOverride);
    }

    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        switch (this._serializationShape(provider)) {
            case 1: {
                gen.writeString(this._format.createFormatter(provider).print((ReadablePartial)value));
                break;
            }
            case 2: {
                DateTimeZone tz = this._format.isTimezoneExplicit() ? this._format.getTimeZone() : DateTimeZone.forTimeZone((TimeZone)provider.getTimeZone());
                gen.writeNumber(value.toDateTime(tz).getMillis());
                break;
            }
            case 3: {
                gen.writeStartArray();
                gen.writeNumber(value.year().get());
                gen.writeNumber(value.monthOfYear().get());
                gen.writeNumber(value.dayOfMonth().get());
                gen.writeNumber(value.hourOfDay().get());
                gen.writeNumber(value.minuteOfHour().get());
                gen.writeNumber(value.secondOfMinute().get());
                gen.writeNumber(value.millisOfSecond().get());
                gen.writeEndArray();
            }
        }
    }
}

