/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  com.fasterxml.jackson.databind.SerializerProvider
 *  org.joda.time.Interval
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.DateTimeFormatter
 */
package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import com.fasterxml.jackson.datatype.joda.ser.JodaDateSerializerBase;
import java.io.IOException;
import org.joda.time.Interval;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormatter;

public class IntervalSerializer
extends JodaDateSerializerBase<Interval> {
    private static final long serialVersionUID = 1L;

    public IntervalSerializer() {
        this(FormatConfig.DEFAULT_DATETIME_PRINTER, 0);
    }

    public IntervalSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }

    public IntervalSerializer(JacksonJodaDateFormat format, int shapeOverride) {
        super(Interval.class, format, SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, 2, shapeOverride);
    }

    public IntervalSerializer withFormat(JacksonJodaDateFormat formatter, int shapeOverride) {
        return new IntervalSerializer(formatter, shapeOverride);
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, Interval value) {
        return value.getStartMillis() == value.getEndMillis();
    }

    public void serialize(Interval interval, JsonGenerator gen, SerializerProvider provider) throws IOException {
        String repr;
        if (this._serializationShape(provider) == 1) {
            DateTimeFormatter f = this._format.createFormatter(provider);
            repr = f.print((ReadableInstant)interval.getStart()) + "/" + f.print((ReadableInstant)interval.getEnd());
        } else {
            repr = interval.getStartMillis() + "-" + interval.getEndMillis();
        }
        gen.writeString(repr);
    }
}

