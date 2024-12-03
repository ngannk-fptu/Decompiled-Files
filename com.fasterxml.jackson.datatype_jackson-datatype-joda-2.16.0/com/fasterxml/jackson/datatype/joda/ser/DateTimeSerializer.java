/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  com.fasterxml.jackson.databind.SerializerProvider
 *  org.joda.time.DateTime
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
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

public class DateTimeSerializer
extends JodaDateSerializerBase<DateTime> {
    private static final long serialVersionUID = 1L;

    public DateTimeSerializer() {
        this(FormatConfig.DEFAULT_DATETIME_PRINTER, 0);
    }

    public DateTimeSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }

    public DateTimeSerializer(JacksonJodaDateFormat format, int shapeOverride) {
        super(DateTime.class, format, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, 2, shapeOverride);
    }

    public DateTimeSerializer withFormat(JacksonJodaDateFormat formatter, int shapeOverride) {
        return new DateTimeSerializer(formatter, shapeOverride);
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, DateTime value) {
        return value.getMillis() == 0L;
    }

    public void serialize(DateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        boolean numeric;
        boolean bl = numeric = this._serializationShape(provider) != 1;
        if (!this.writeWithZoneId(provider)) {
            if (numeric) {
                gen.writeNumber(value.getMillis());
            } else {
                gen.writeString(this._format.createFormatter(provider).print((ReadableInstant)value));
            }
        } else {
            if (numeric) {
                gen.writeNumber(value.getMillis());
                return;
            }
            StringBuilder sb = new StringBuilder(40).append(this._format.createFormatter(provider).withOffsetParsed().print((ReadableInstant)value));
            sb = sb.append('[').append(value.getZone()).append(']');
            gen.writeString(sb.toString());
        }
    }
}

