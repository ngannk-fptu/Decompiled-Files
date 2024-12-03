/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.jsontype.impl;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.map.jsontype.impl.TypeSerializerBase;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AsArrayTypeSerializer
extends TypeSerializerBase {
    public AsArrayTypeSerializer(TypeIdResolver idRes, BeanProperty property) {
        super(idRes, property);
    }

    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.WRAPPER_ARRAY;
    }

    @Override
    public void writeTypePrefixForObject(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeStartArray();
        jgen.writeString(this._idResolver.idFromValue(value));
        jgen.writeStartObject();
    }

    @Override
    public void writeTypePrefixForObject(Object value, JsonGenerator jgen, Class<?> type) throws IOException, JsonProcessingException {
        jgen.writeStartArray();
        jgen.writeString(this._idResolver.idFromValueAndType(value, type));
        jgen.writeStartObject();
    }

    @Override
    public void writeTypePrefixForArray(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeStartArray();
        jgen.writeString(this._idResolver.idFromValue(value));
        jgen.writeStartArray();
    }

    @Override
    public void writeTypePrefixForArray(Object value, JsonGenerator jgen, Class<?> type) throws IOException, JsonProcessingException {
        jgen.writeStartArray();
        jgen.writeString(this._idResolver.idFromValueAndType(value, type));
        jgen.writeStartArray();
    }

    @Override
    public void writeTypePrefixForScalar(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeStartArray();
        jgen.writeString(this._idResolver.idFromValue(value));
    }

    @Override
    public void writeTypePrefixForScalar(Object value, JsonGenerator jgen, Class<?> type) throws IOException, JsonProcessingException {
        jgen.writeStartArray();
        jgen.writeString(this._idResolver.idFromValueAndType(value, type));
    }

    @Override
    public void writeTypeSuffixForObject(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeEndObject();
        jgen.writeEndArray();
    }

    @Override
    public void writeTypeSuffixForArray(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeEndArray();
        jgen.writeEndArray();
    }

    @Override
    public void writeTypeSuffixForScalar(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeEndArray();
    }
}

