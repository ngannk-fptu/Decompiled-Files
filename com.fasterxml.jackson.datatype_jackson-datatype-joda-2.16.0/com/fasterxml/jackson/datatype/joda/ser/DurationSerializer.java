/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  com.fasterxml.jackson.databind.SerializerProvider
 *  org.joda.time.Duration
 */
package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import com.fasterxml.jackson.datatype.joda.ser.JodaDateSerializerBase;
import java.io.IOException;
import org.joda.time.Duration;

public class DurationSerializer
extends JodaDateSerializerBase<Duration> {
    private static final long serialVersionUID = 1L;

    public DurationSerializer() {
        this(FormatConfig.DEFAULT_DATEONLY_FORMAT, 0);
    }

    public DurationSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }

    public DurationSerializer(JacksonJodaDateFormat formatter, int shapeOverride) {
        super(Duration.class, formatter, SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, 2, shapeOverride);
    }

    public DurationSerializer withFormat(JacksonJodaDateFormat formatter, int shapeOverride) {
        return new DurationSerializer(formatter, shapeOverride);
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, Duration value) {
        return value.getMillis() == 0L;
    }

    public void serialize(Duration value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (this._serializationShape(provider) == 1) {
            gen.writeString(value.toString());
        } else {
            gen.writeNumber(value.getMillis());
        }
    }
}

