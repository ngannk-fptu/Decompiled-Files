/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  com.fasterxml.jackson.databind.SerializerProvider
 *  org.joda.time.LocalDate
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
import org.joda.time.LocalDate;
import org.joda.time.ReadablePartial;

public class LocalDateSerializer
extends JodaDateSerializerBase<LocalDate> {
    private static final long serialVersionUID = 1L;

    public LocalDateSerializer() {
        this(FormatConfig.DEFAULT_LOCAL_DATEONLY_FORMAT, 0);
    }

    public LocalDateSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }

    public LocalDateSerializer(JacksonJodaDateFormat format, int shapeOverride) {
        super(LocalDate.class, format, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, 3, shapeOverride);
    }

    public LocalDateSerializer withFormat(JacksonJodaDateFormat formatter, int shapeOverride) {
        return new LocalDateSerializer(formatter, shapeOverride);
    }

    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (this._serializationShape(provider) == 1) {
            gen.writeString(this._format.createFormatter(provider).print((ReadablePartial)value));
            return;
        }
        gen.writeStartArray();
        gen.writeNumber(value.year().get());
        gen.writeNumber(value.monthOfYear().get());
        gen.writeNumber(value.dayOfMonth().get());
        gen.writeEndArray();
    }
}

