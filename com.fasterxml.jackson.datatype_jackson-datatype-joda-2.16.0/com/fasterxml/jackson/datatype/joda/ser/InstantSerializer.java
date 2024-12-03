/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  com.fasterxml.jackson.databind.SerializerProvider
 *  org.joda.time.Instant
 *  org.joda.time.ReadableInstant
 */
package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import com.fasterxml.jackson.datatype.joda.ser.JodaDateSerializerBase;
import java.io.IOException;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

public class InstantSerializer
extends JodaDateSerializerBase<Instant> {
    private static final long serialVersionUID = 1L;

    public InstantSerializer() {
        this(FormatConfig.DEFAULT_DATETIME_PRINTER, 0);
    }

    public InstantSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }

    public InstantSerializer(JacksonJodaDateFormat format, int shapeOverride) {
        super(Instant.class, format, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, 2, shapeOverride);
    }

    public InstantSerializer withFormat(JacksonJodaDateFormat formatter, int shapeOverride) {
        return new InstantSerializer(formatter, shapeOverride);
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, Instant value) {
        return value.getMillis() == 0L;
    }

    public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (this._serializationShape(provider) == 1) {
            gen.writeString(this._format.createFormatter(provider).print((ReadableInstant)value));
        } else {
            gen.writeNumber(value.getMillis());
        }
    }
}

