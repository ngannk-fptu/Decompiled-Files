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
import org.codehaus.jackson.map.jsontype.impl.AsArrayTypeSerializer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AsPropertyTypeSerializer
extends AsArrayTypeSerializer {
    protected final String _typePropertyName;

    public AsPropertyTypeSerializer(TypeIdResolver idRes, BeanProperty property, String propName) {
        super(idRes, property);
        this._typePropertyName = propName;
    }

    @Override
    public String getPropertyName() {
        return this._typePropertyName;
    }

    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.PROPERTY;
    }

    @Override
    public void writeTypePrefixForObject(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField(this._typePropertyName, this._idResolver.idFromValue(value));
    }

    @Override
    public void writeTypePrefixForObject(Object value, JsonGenerator jgen, Class<?> type) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField(this._typePropertyName, this._idResolver.idFromValueAndType(value, type));
    }

    @Override
    public void writeTypeSuffixForObject(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeEndObject();
    }
}

