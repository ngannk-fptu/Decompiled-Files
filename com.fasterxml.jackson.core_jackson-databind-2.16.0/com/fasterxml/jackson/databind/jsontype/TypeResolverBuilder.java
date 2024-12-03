/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$As
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$Id
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$Value
 */
package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.util.Collection;

public interface TypeResolverBuilder<T extends TypeResolverBuilder<T>> {
    public Class<?> getDefaultImpl();

    public TypeSerializer buildTypeSerializer(SerializationConfig var1, JavaType var2, Collection<NamedType> var3);

    public TypeDeserializer buildTypeDeserializer(DeserializationConfig var1, JavaType var2, Collection<NamedType> var3);

    public T init(JsonTypeInfo.Id var1, TypeIdResolver var2);

    default public T init(JsonTypeInfo.Value settings, TypeIdResolver res) {
        return this.init(settings.getIdType(), res);
    }

    public T inclusion(JsonTypeInfo.As var1);

    public T typeProperty(String var1);

    public T defaultImpl(Class<?> var1);

    public T typeIdVisibility(boolean var1);

    default public T withDefaultImpl(Class<?> defaultImpl) {
        return this.defaultImpl(defaultImpl);
    }

    default public T withSettings(JsonTypeInfo.Value typeInfo) {
        throw new IllegalStateException("TypeResolveBuilder implementation " + this.getClass().getName() + " must implement `withSettings()`");
    }
}

