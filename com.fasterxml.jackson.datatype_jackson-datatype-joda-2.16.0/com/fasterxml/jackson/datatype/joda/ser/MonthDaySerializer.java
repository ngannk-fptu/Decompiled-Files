/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  com.fasterxml.jackson.databind.SerializerProvider
 *  org.joda.time.MonthDay
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
import org.joda.time.MonthDay;
import org.joda.time.ReadablePartial;

public class MonthDaySerializer
extends JodaDateSerializerBase<MonthDay> {
    private static final long serialVersionUID = 1L;

    public MonthDaySerializer() {
        this(FormatConfig.DEFAULT_MONTH_DAY_FORMAT, 0);
    }

    public MonthDaySerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }

    public MonthDaySerializer(JacksonJodaDateFormat format, int shapeOverride) {
        super(MonthDay.class, format, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, 1, shapeOverride);
    }

    public MonthDaySerializer withFormat(JacksonJodaDateFormat formatter, int shapeOverride) {
        return new MonthDaySerializer(formatter, shapeOverride);
    }

    public void serialize(MonthDay value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(this._format.createFormatter(provider).print((ReadablePartial)value));
    }
}

