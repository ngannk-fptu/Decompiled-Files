/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.JsonParserSequence;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.AsArrayTypeDeserializer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;

public class AsPropertyTypeDeserializer
extends AsArrayTypeDeserializer {
    private static final long serialVersionUID = 1L;
    protected final JsonTypeInfo.As _inclusion;
    protected final String _msgForMissingId;

    public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl) {
        this(bt, idRes, typePropertyName, typeIdVisible, defaultImpl, JsonTypeInfo.As.PROPERTY);
    }

    public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl, JsonTypeInfo.As inclusion) {
        super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
        this._msgForMissingId = this._property == null ? String.format("missing type id property '%s'", this._typePropertyName) : String.format("missing type id property '%s' (for POJO property '%s')", this._typePropertyName, this._property.getName());
        this._inclusion = inclusion;
    }

    public AsPropertyTypeDeserializer(AsPropertyTypeDeserializer src, BeanProperty property) {
        super(src, property);
        this._msgForMissingId = this._property == null ? String.format("missing type id property '%s'", this._typePropertyName) : String.format("missing type id property '%s' (for POJO property '%s')", this._typePropertyName, this._property.getName());
        this._inclusion = src._inclusion;
    }

    @Override
    public TypeDeserializer forProperty(BeanProperty prop) {
        return prop == this._property ? this : new AsPropertyTypeDeserializer(this, prop);
    }

    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return this._inclusion;
    }

    @Override
    public Object deserializeTypedFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
        Object typeId;
        if (p.canReadTypeId() && (typeId = p.getTypeId()) != null) {
            return this._deserializeWithNativeTypeId(p, ctxt, typeId);
        }
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        } else if (t != JsonToken.FIELD_NAME) {
            return this._deserializeTypedUsingDefaultImpl(p, ctxt, null, this._msgForMissingId);
        }
        TokenBuffer tb = null;
        boolean ignoreCase = ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        while (t == JsonToken.FIELD_NAME) {
            String name = p.currentName();
            p.nextToken();
            if (name.equals(this._typePropertyName) || ignoreCase && name.equalsIgnoreCase(this._typePropertyName)) {
                return this._deserializeTypedForId(p, ctxt, tb, p.getText());
            }
            if (tb == null) {
                tb = new TokenBuffer(p, ctxt);
            }
            tb.writeFieldName(name);
            tb.copyCurrentStructure(p);
            t = p.nextToken();
        }
        return this._deserializeTypedUsingDefaultImpl(p, ctxt, tb, this._msgForMissingId);
    }

    protected Object _deserializeTypedForId(JsonParser p, DeserializationContext ctxt, TokenBuffer tb, String typeId) throws IOException {
        JsonDeserializer<Object> deser = this._findDeserializer(ctxt, typeId);
        if (this._typeIdVisible) {
            if (tb == null) {
                tb = new TokenBuffer(p, ctxt);
            }
            tb.writeFieldName(p.currentName());
            tb.writeString(typeId);
        }
        if (tb != null) {
            p.clearCurrentToken();
            p = JsonParserSequence.createFlattened(false, tb.asParser(p), p);
        }
        if (p.currentToken() != JsonToken.END_OBJECT) {
            p.nextToken();
        }
        return deser.deserialize(p, ctxt);
    }

    @Deprecated
    protected Object _deserializeTypedUsingDefaultImpl(JsonParser p, DeserializationContext ctxt, TokenBuffer tb) throws IOException {
        return this._deserializeTypedUsingDefaultImpl(p, ctxt, tb, null);
    }

    protected Object _deserializeTypedUsingDefaultImpl(JsonParser p, DeserializationContext ctxt, TokenBuffer tb, String priorFailureMsg) throws IOException {
        JsonDeserializer<Object> deser;
        if (!this.hasDefaultImpl()) {
            String str;
            Object result = TypeDeserializer.deserializeIfNatural(p, ctxt, this._baseType);
            if (result != null) {
                return result;
            }
            if (p.isExpectedStartArrayToken()) {
                return super.deserializeTypedFromAny(p, ctxt);
            }
            if (p.hasToken(JsonToken.VALUE_STRING) && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) && (str = p.getText().trim()).isEmpty()) {
                return null;
            }
        }
        if ((deser = this._findDefaultImplDeserializer(ctxt)) == null) {
            JavaType t = this._handleMissingTypeId(ctxt, priorFailureMsg);
            if (t == null) {
                return null;
            }
            deser = ctxt.findContextualValueDeserializer(t, this._property);
        }
        if (tb != null) {
            tb.writeEndObject();
            p = tb.asParser(p);
            p.nextToken();
        }
        return deser.deserialize(p, ctxt);
    }

    @Override
    public Object deserializeTypedFromAny(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.START_ARRAY)) {
            return super.deserializeTypedFromArray(p, ctxt);
        }
        return this.deserializeTypedFromObject(p, ctxt);
    }
}

