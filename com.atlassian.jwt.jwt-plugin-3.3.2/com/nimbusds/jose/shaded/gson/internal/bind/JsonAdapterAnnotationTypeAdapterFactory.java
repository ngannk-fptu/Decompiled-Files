/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.shaded.gson.internal.bind;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonDeserializer;
import com.nimbusds.jose.shaded.gson.JsonSerializer;
import com.nimbusds.jose.shaded.gson.TypeAdapter;
import com.nimbusds.jose.shaded.gson.TypeAdapterFactory;
import com.nimbusds.jose.shaded.gson.annotations.JsonAdapter;
import com.nimbusds.jose.shaded.gson.internal.ConstructorConstructor;
import com.nimbusds.jose.shaded.gson.internal.bind.TreeTypeAdapter;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;

public final class JsonAdapterAnnotationTypeAdapterFactory
implements TypeAdapterFactory {
    private final ConstructorConstructor constructorConstructor;

    public JsonAdapterAnnotationTypeAdapterFactory(ConstructorConstructor constructorConstructor) {
        this.constructorConstructor = constructorConstructor;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> targetType) {
        Class<T> rawType = targetType.getRawType();
        JsonAdapter annotation = rawType.getAnnotation(JsonAdapter.class);
        if (annotation == null) {
            return null;
        }
        return this.getTypeAdapter(this.constructorConstructor, gson, targetType, annotation);
    }

    TypeAdapter<?> getTypeAdapter(ConstructorConstructor constructorConstructor, Gson gson, TypeToken<?> type, JsonAdapter annotation) {
        TypeAdapter<?> typeAdapter;
        Object instance = constructorConstructor.get(TypeToken.get(annotation.value())).construct();
        boolean nullSafe = annotation.nullSafe();
        if (instance instanceof TypeAdapter) {
            typeAdapter = (TypeAdapter<?>)instance;
        } else if (instance instanceof TypeAdapterFactory) {
            typeAdapter = ((TypeAdapterFactory)instance).create(gson, type);
        } else if (instance instanceof JsonSerializer || instance instanceof JsonDeserializer) {
            JsonSerializer serializer = instance instanceof JsonSerializer ? (JsonSerializer)instance : null;
            JsonDeserializer deserializer = instance instanceof JsonDeserializer ? (JsonDeserializer)instance : null;
            TreeTypeAdapter tempAdapter = new TreeTypeAdapter(serializer, deserializer, gson, type, null, nullSafe);
            typeAdapter = tempAdapter;
            nullSafe = false;
        } else {
            throw new IllegalArgumentException("Invalid attempt to bind an instance of " + instance.getClass().getName() + " as a @JsonAdapter for " + type.toString() + ". @JsonAdapter value must be a TypeAdapter, TypeAdapterFactory, JsonSerializer or JsonDeserializer.");
        }
        if (typeAdapter != null && nullSafe) {
            typeAdapter = typeAdapter.nullSafe();
        }
        return typeAdapter;
    }
}

