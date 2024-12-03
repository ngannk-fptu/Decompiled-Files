/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonToken
 *  org.codehaus.jackson.annotate.JsonTypeInfo$As
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.jsontype.impl;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.map.jsontype.impl.TypeDeserializerBase;
import org.codehaus.jackson.map.jsontype.impl.TypeIdResolverBase;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AsArrayTypeDeserializer
extends TypeDeserializerBase {
    @Deprecated
    public AsArrayTypeDeserializer(JavaType bt, TypeIdResolver idRes, BeanProperty property) {
        this(bt, idRes, property, null);
    }

    public AsArrayTypeDeserializer(JavaType bt, TypeIdResolver idRes, BeanProperty property, Class<?> defaultImpl) {
        super(bt, idRes, property, defaultImpl);
    }

    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.WRAPPER_ARRAY;
    }

    @Override
    public Object deserializeTypedFromArray(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return this._deserialize(jp, ctxt);
    }

    @Override
    public Object deserializeTypedFromObject(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return this._deserialize(jp, ctxt);
    }

    @Override
    public Object deserializeTypedFromScalar(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return this._deserialize(jp, ctxt);
    }

    @Override
    public Object deserializeTypedFromAny(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return this._deserialize(jp, ctxt);
    }

    private final Object _deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        boolean hadStartArray = jp.isExpectedStartArrayToken();
        JsonDeserializer<Object> deser = this._findDeserializer(ctxt, this._locateTypeId(jp, ctxt));
        Object value = deser.deserialize(jp, ctxt);
        if (hadStartArray && jp.nextToken() != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "expected closing END_ARRAY after type information and deserialized value");
        }
        return value;
    }

    protected final String _locateTypeId(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (!jp.isExpectedStartArrayToken()) {
            if (this._idResolver instanceof TypeIdResolverBase && this._defaultImpl != null) {
                return ((TypeIdResolverBase)this._idResolver).idFromBaseType();
            }
            throw ctxt.wrongTokenException(jp, JsonToken.START_ARRAY, "need JSON Array to contain As.WRAPPER_ARRAY type information for class " + this.baseTypeName());
        }
        if (jp.nextToken() != JsonToken.VALUE_STRING) {
            if (this._idResolver instanceof TypeIdResolverBase && this._defaultImpl != null) {
                return ((TypeIdResolverBase)this._idResolver).idFromBaseType();
            }
            throw ctxt.wrongTokenException(jp, JsonToken.VALUE_STRING, "need JSON String that contains type id (for subtype of " + this.baseTypeName() + ")");
        }
        String result = jp.getText();
        jp.nextToken();
        return result;
    }
}

