/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.jsontype.impl;

import java.io.IOException;
import java.util.HashMap;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TypeDeserializerBase
extends TypeDeserializer {
    protected final TypeIdResolver _idResolver;
    protected final JavaType _baseType;
    protected final BeanProperty _property;
    protected final JavaType _defaultImpl;
    protected final HashMap<String, JsonDeserializer<Object>> _deserializers;
    protected JsonDeserializer<Object> _defaultImplDeserializer;

    @Deprecated
    protected TypeDeserializerBase(JavaType baseType, TypeIdResolver idRes, BeanProperty property) {
        this(baseType, idRes, property, null);
    }

    protected TypeDeserializerBase(JavaType baseType, TypeIdResolver idRes, BeanProperty property, Class<?> defaultImpl) {
        this._baseType = baseType;
        this._idResolver = idRes;
        this._property = property;
        this._deserializers = new HashMap();
        this._defaultImpl = defaultImpl == null ? null : baseType.forcedNarrowBy(defaultImpl);
    }

    @Override
    public abstract JsonTypeInfo.As getTypeInclusion();

    public String baseTypeName() {
        return this._baseType.getRawClass().getName();
    }

    @Override
    public String getPropertyName() {
        return null;
    }

    @Override
    public TypeIdResolver getTypeIdResolver() {
        return this._idResolver;
    }

    @Override
    public Class<?> getDefaultImpl() {
        return this._defaultImpl == null ? null : this._defaultImpl.getRawClass();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(this.getClass().getName());
        sb.append("; base-type:").append(this._baseType);
        sb.append("; id-resolver: ").append(this._idResolver);
        sb.append(']');
        return sb.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final JsonDeserializer<Object> _findDeserializer(DeserializationContext ctxt, String typeId) throws IOException, JsonProcessingException {
        JsonDeserializer<Object> deser;
        HashMap<String, JsonDeserializer<Object>> hashMap = this._deserializers;
        synchronized (hashMap) {
            deser = this._deserializers.get(typeId);
            if (deser == null) {
                JavaType type = this._idResolver.typeFromId(typeId);
                if (type == null) {
                    if (this._defaultImpl == null) {
                        throw ctxt.unknownTypeException(this._baseType, typeId);
                    }
                    deser = this._findDefaultImplDeserializer(ctxt);
                } else {
                    if (this._baseType != null && this._baseType.getClass() == type.getClass()) {
                        type = this._baseType.narrowBy(type.getRawClass());
                    }
                    deser = ctxt.getDeserializerProvider().findValueDeserializer(ctxt.getConfig(), type, this._property);
                }
                this._deserializers.put(typeId, deser);
            }
        }
        return deser;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final JsonDeserializer<Object> _findDefaultImplDeserializer(DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._defaultImpl == null) {
            return null;
        }
        JavaType javaType = this._defaultImpl;
        synchronized (javaType) {
            if (this._defaultImplDeserializer == null) {
                this._defaultImplDeserializer = ctxt.getDeserializerProvider().findValueDeserializer(ctxt.getConfig(), this._defaultImpl, this._property);
            }
            return this._defaultImplDeserializer;
        }
    }
}

