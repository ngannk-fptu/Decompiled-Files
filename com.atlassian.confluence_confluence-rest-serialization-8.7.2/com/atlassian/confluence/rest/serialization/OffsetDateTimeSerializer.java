/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.codehaus.jackson.map.ser.std.SerializerBase
 */
package com.atlassian.confluence.rest.serialization;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

public final class OffsetDateTimeSerializer
extends SerializerBase<OffsetDateTime> {
    private final Style style;

    private OffsetDateTimeSerializer(Style style) {
        super(OffsetDateTime.class);
        this.style = style;
    }

    public static OffsetDateTimeSerializer serializeAsIso() {
        return new OffsetDateTimeSerializer(Style.ISO);
    }

    public static OffsetDateTimeSerializer serializeAsTimestamp() {
        return new OffsetDateTimeSerializer(Style.TIMESTAMP);
    }

    public void serialize(OffsetDateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (this.style == Style.TIMESTAMP) {
            jgen.writeNumber(value.toInstant().toEpochMilli());
        } else {
            jgen.writeString(value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }
    }

    private static enum Style {
        TIMESTAMP,
        ISO;

    }
}

