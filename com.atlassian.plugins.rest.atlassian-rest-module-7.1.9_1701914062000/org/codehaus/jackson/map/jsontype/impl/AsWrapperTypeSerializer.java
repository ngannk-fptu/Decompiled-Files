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
public class AsWrapperTypeSerializer
extends TypeSerializerBase {
    public AsWrapperTypeSerializer(TypeIdResolver idRes, BeanProperty property) {
        super(idRes, property);
    }

    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.WRAPPER_OBJECT;
    }

    @Override
    public void writeTypePrefixForObject(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeObjectFieldStart(this._idResolver.idFromValue(value));
    }

    @Override
    public void writeTypePrefixForObject(Object value, JsonGenerator jgen, Class<?> type) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeObjectFieldStart(this._idResolver.idFromValueAndType(value, type));
    }

    @Override
    public void writeTypePrefixForArray(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeArrayFieldStart(this._idResolver.idFromValue(value));
    }

    @Override
    public void writeTypePrefixForArray(Object value, JsonGenerator jgen, Class<?> type) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeArrayFieldStart(this._idResolver.idFromValueAndType(value, type));
    }

    @Override
    public void writeTypePrefixForScalar(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeFieldName(this._idResolver.idFromValue(value));
    }

    @Override
    public void writeTypePrefixForScalar(Object value, JsonGenerator jgen, Class<?> type) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeFieldName(this._idResolver.idFromValueAndType(value, type));
    }

    @Override
    public void writeTypeSuffixForObject(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeEndObject();
        jgen.writeEndObject();
    }

    @Override
    public void writeTypeSuffixForArray(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeEndArray();
        jgen.writeEndObject();
    }

    @Override
    public void writeTypeSuffixForScalar(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeEndObject();
    }
}

