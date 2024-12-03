/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.annotate.JsonTypeInfo$As
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
public class AsExternalTypeSerializer
extends TypeSerializerBase {
    protected final String _typePropertyName;

    public AsExternalTypeSerializer(TypeIdResolver idRes, BeanProperty property, String propName) {
        super(idRes, property);
        this._typePropertyName = propName;
    }

    @Override
    public String getPropertyName() {
        return this._typePropertyName;
    }

    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.EXTERNAL_PROPERTY;
    }

    @Override
    public void writeTypePrefixForObject(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        this._writePrefix(value, jgen);
    }

    @Override
    public void writeTypePrefixForObject(Object value, JsonGenerator jgen, Class<?> type) throws IOException, JsonProcessingException {
        this._writePrefix(value, jgen, type);
    }

    @Override
    public void writeTypePrefixForArray(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        this._writePrefix(value, jgen);
    }

    @Override
    public void writeTypePrefixForArray(Object value, JsonGenerator jgen, Class<?> type) throws IOException, JsonProcessingException {
        this._writePrefix(value, jgen, type);
    }

    @Override
    public void writeTypePrefixForScalar(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        this._writePrefix(value, jgen);
    }

    @Override
    public void writeTypePrefixForScalar(Object value, JsonGenerator jgen, Class<?> type) throws IOException, JsonProcessingException {
        this._writePrefix(value, jgen, type);
    }

    @Override
    public void writeTypeSuffixForObject(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        this._writeSuffix(value, jgen);
    }

    @Override
    public void writeTypeSuffixForArray(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        this._writeSuffix(value, jgen);
    }

    @Override
    public void writeTypeSuffixForScalar(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        this._writeSuffix(value, jgen);
    }

    protected final void _writePrefix(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
    }

    protected final void _writePrefix(Object value, JsonGenerator jgen, Class<?> type) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
    }

    protected final void _writeSuffix(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeEndObject();
        jgen.writeStringField(this._typePropertyName, this._idResolver.idFromValue(value));
    }
}

