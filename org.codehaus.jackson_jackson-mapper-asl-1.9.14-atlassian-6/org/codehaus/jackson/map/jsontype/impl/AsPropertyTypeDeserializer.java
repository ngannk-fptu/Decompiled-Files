/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonToken
 *  org.codehaus.jackson.annotate.JsonTypeInfo$As
 *  org.codehaus.jackson.type.JavaType
 *  org.codehaus.jackson.util.JsonParserSequence
 *  org.codehaus.jackson.util.TokenBuffer
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
import org.codehaus.jackson.map.jsontype.impl.AsArrayTypeDeserializer;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.util.JsonParserSequence;
import org.codehaus.jackson.util.TokenBuffer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AsPropertyTypeDeserializer
extends AsArrayTypeDeserializer {
    protected final String _typePropertyName;

    @Deprecated
    public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, BeanProperty property, String typePropName) {
        this(bt, idRes, property, null, typePropName);
    }

    public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, BeanProperty property, Class<?> defaultImpl, String typePropName) {
        super(bt, idRes, property, defaultImpl);
        this._typePropertyName = typePropName;
    }

    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.PROPERTY;
    }

    @Override
    public String getPropertyName() {
        return this._typePropertyName;
    }

    @Override
    public Object deserializeTypedFromObject(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        } else {
            if (t == JsonToken.START_ARRAY) {
                return this._deserializeTypedUsingDefaultImpl(jp, ctxt, null);
            }
            if (t != JsonToken.FIELD_NAME) {
                return this._deserializeTypedUsingDefaultImpl(jp, ctxt, null);
            }
        }
        TokenBuffer tb = null;
        while (t == JsonToken.FIELD_NAME) {
            String name = jp.getCurrentName();
            jp.nextToken();
            if (this._typePropertyName.equals(name)) {
                String typeId = jp.getText();
                JsonDeserializer<Object> deser = this._findDeserializer(ctxt, typeId);
                if (tb != null) {
                    jp = JsonParserSequence.createFlattened((JsonParser)tb.asParser(jp), (JsonParser)jp);
                }
                jp.nextToken();
                return deser.deserialize(jp, ctxt);
            }
            if (tb == null) {
                tb = new TokenBuffer(null);
            }
            tb.writeFieldName(name);
            tb.copyCurrentStructure(jp);
            t = jp.nextToken();
        }
        return this._deserializeTypedUsingDefaultImpl(jp, ctxt, tb);
    }

    protected Object _deserializeTypedUsingDefaultImpl(JsonParser jp, DeserializationContext ctxt, TokenBuffer tb) throws IOException, JsonProcessingException {
        if (this._defaultImpl != null) {
            JsonDeserializer<Object> deser = this._findDefaultImplDeserializer(ctxt);
            if (tb != null) {
                tb.writeEndObject();
                jp = tb.asParser(jp);
                jp.nextToken();
            }
            return deser.deserialize(jp, ctxt);
        }
        Object result = this._deserializeIfNatural(jp, ctxt);
        if (result != null) {
            return result;
        }
        if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
            return super.deserializeTypedFromAny(jp, ctxt);
        }
        throw ctxt.wrongTokenException(jp, JsonToken.FIELD_NAME, "missing property '" + this._typePropertyName + "' that is to contain type id  (for class " + this.baseTypeName() + ")");
    }

    @Override
    public Object deserializeTypedFromAny(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
            return super.deserializeTypedFromArray(jp, ctxt);
        }
        return this.deserializeTypedFromObject(jp, ctxt);
    }

    protected Object _deserializeIfNatural(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        switch (jp.getCurrentToken()) {
            case VALUE_STRING: {
                if (!this._baseType.getRawClass().isAssignableFrom(String.class)) break;
                return jp.getText();
            }
            case VALUE_NUMBER_INT: {
                if (!this._baseType.getRawClass().isAssignableFrom(Integer.class)) break;
                return jp.getIntValue();
            }
            case VALUE_NUMBER_FLOAT: {
                if (!this._baseType.getRawClass().isAssignableFrom(Double.class)) break;
                return jp.getDoubleValue();
            }
            case VALUE_TRUE: {
                if (!this._baseType.getRawClass().isAssignableFrom(Boolean.class)) break;
                return Boolean.TRUE;
            }
            case VALUE_FALSE: {
                if (!this._baseType.getRawClass().isAssignableFrom(Boolean.class)) break;
                return Boolean.FALSE;
            }
        }
        return null;
    }
}

