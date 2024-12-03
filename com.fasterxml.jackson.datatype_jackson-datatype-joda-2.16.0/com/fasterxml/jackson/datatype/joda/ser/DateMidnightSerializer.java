/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  com.fasterxml.jackson.databind.SerializerProvider
 *  org.joda.time.DateMidnight
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
import org.joda.time.DateMidnight;
import org.joda.time.ReadableInstant;

@Deprecated
public class DateMidnightSerializer
extends JodaDateSerializerBase<DateMidnight> {
    private static final long serialVersionUID = 1L;

    public DateMidnightSerializer() {
        this(FormatConfig.DEFAULT_LOCAL_DATEONLY_FORMAT, 0);
    }

    public DateMidnightSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }

    public DateMidnightSerializer(JacksonJodaDateFormat format, int shapeOverride) {
        super(DateMidnight.class, format, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, 3, shapeOverride);
    }

    public DateMidnightSerializer withFormat(JacksonJodaDateFormat formatter, int shapeOverride) {
        return new DateMidnightSerializer(formatter, shapeOverride);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, DateMidnight value) {
        return value.getMillis() == 0L;
    }

    public void serialize(DateMidnight value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        switch (this._serializationShape(provider)) {
            case 1: {
                gen.writeString(this._format.createFormatterWithLocale(provider).print((ReadableInstant)value));
                break;
            }
            case 2: {
                gen.writeNumber(value.getMillis());
                break;
            }
            case 3: {
                gen.writeStartArray();
                gen.writeNumber(value.year().get());
                gen.writeNumber(value.monthOfYear().get());
                gen.writeNumber(value.dayOfMonth().get());
                gen.writeEndArray();
            }
        }
    }
}

