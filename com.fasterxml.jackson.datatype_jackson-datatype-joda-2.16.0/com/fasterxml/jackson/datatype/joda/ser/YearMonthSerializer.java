/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  com.fasterxml.jackson.databind.SerializerProvider
 *  org.joda.time.ReadablePartial
 *  org.joda.time.YearMonth
 */
package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import com.fasterxml.jackson.datatype.joda.ser.JodaDateSerializerBase;
import java.io.IOException;
import org.joda.time.ReadablePartial;
import org.joda.time.YearMonth;

public class YearMonthSerializer
extends JodaDateSerializerBase<YearMonth> {
    private static final long serialVersionUID = 1L;

    public YearMonthSerializer() {
        this(FormatConfig.DEFAULT_YEAR_MONTH_FORMAT, 0);
    }

    public YearMonthSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }

    public YearMonthSerializer(JacksonJodaDateFormat format, int shapeOverride) {
        super(YearMonth.class, format, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, 1, shapeOverride);
    }

    public YearMonthSerializer withFormat(JacksonJodaDateFormat formatter, int shapeOverride) {
        return new YearMonthSerializer(formatter, shapeOverride);
    }

    public void serialize(YearMonth value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(this._format.createFormatter(provider).print((ReadablePartial)value));
    }
}

