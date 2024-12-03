/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.core.JsonToken
 *  com.fasterxml.jackson.core.type.WritableTypeId
 *  com.fasterxml.jackson.databind.JavaType
 *  com.fasterxml.jackson.databind.JsonMappingException
 *  com.fasterxml.jackson.databind.SerializerProvider
 *  com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper
 *  com.fasterxml.jackson.databind.jsontype.TypeSerializer
 *  org.joda.time.DateTimeZone
 */
package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.datatype.joda.ser.JodaSerializerBase;
import java.io.IOException;
import org.joda.time.DateTimeZone;

public class DateTimeZoneSerializer
extends JodaSerializerBase<DateTimeZone> {
    private static final long serialVersionUID = 1L;

    public DateTimeZoneSerializer() {
        super(DateTimeZone.class);
    }

    public void serialize(DateTimeZone value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getID());
    }

    @Override
    public void serializeWithType(DateTimeZone value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId((Object)value, DateTimeZone.class, JsonToken.VALUE_STRING));
        this.serialize(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        visitor.expectStringFormat(typeHint);
    }
}

