/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.Cache;
import com.google.gson.LruCache;
import com.google.gson.Pair;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.$Gson$Types;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class FieldAttributes {
    private static final String MAX_CACHE_PROPERTY_NAME = "com.google.gson.annotation_cache_size_hint";
    private static final Cache<Pair<Class<?>, String>, Collection<Annotation>> ANNOTATION_CACHE = new LruCache(FieldAttributes.getMaxCacheSize());
    private final Class<?> declaringClazz;
    private final Field field;
    private final Class<?> declaredType;
    private final boolean isSynthetic;
    private final int modifiers;
    private final String name;
    private final Type resolvedType;
    private Type genericType;
    private Collection<Annotation> annotations;

    FieldAttributes(Class<?> declaringClazz, Field f, Type declaringType) {
        this.declaringClazz = $Gson$Preconditions.checkNotNull(declaringClazz);
        this.name = f.getName();
        this.declaredType = f.getType();
        this.isSynthetic = f.isSynthetic();
        this.modifiers = f.getModifiers();
        this.field = f;
        this.resolvedType = FieldAttributes.getTypeInfoForField(f, declaringType);
    }

    private static int getMaxCacheSize() {
        int defaultMaxCacheSize = 2000;
        try {
            String propertyValue = System.getProperty(MAX_CACHE_PROPERTY_NAME, String.valueOf(2000));
            return Integer.parseInt(propertyValue);
        }
        catch (NumberFormatException e) {
            return 2000;
        }
    }

    public Class<?> getDeclaringClass() {
        return this.declaringClazz;
    }

    public String getName() {
        return this.name;
    }

    public Type getDeclaredType() {
        if (this.genericType == null) {
            this.genericType = this.field.getGenericType();
        }
        return this.genericType;
    }

    public Class<?> getDeclaredClass() {
        return this.declaredType;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotation) {
        return FieldAttributes.getAnnotationFromArray(this.getAnnotations(), annotation);
    }

    public Collection<Annotation> getAnnotations() {
        if (this.annotations == null) {
            Pair key = new Pair(this.declaringClazz, this.name);
            Collection<Annotation> cachedValue = ANNOTATION_CACHE.getElement(key);
            if (cachedValue == null) {
                cachedValue = Collections.unmodifiableCollection(Arrays.asList(this.field.getAnnotations()));
                ANNOTATION_CACHE.addElement(key, cachedValue);
            }
            this.annotations = cachedValue;
        }
        return this.annotations;
    }

    public boolean hasModifier(int modifier) {
        return (this.modifiers & modifier) != 0;
    }

    void set(Object instance, Object value) throws IllegalAccessException {
        this.field.set(instance, value);
    }

    Object get(Object instance) throws IllegalAccessException {
        return this.field.get(instance);
    }

    boolean isSynthetic() {
        return this.isSynthetic;
    }

    @Deprecated
    Field getFieldObject() {
        return this.field;
    }

    Type getResolvedType() {
        return this.resolvedType;
    }

    private static <T extends Annotation> T getAnnotationFromArray(Collection<Annotation> annotations, Class<T> annotation) {
        for (Annotation a : annotations) {
            if (a.annotationType() != annotation) continue;
            return (T)a;
        }
        return null;
    }

    static Type getTypeInfoForField(Field f, Type typeDefiningF) {
        Class<?> rawType = $Gson$Types.getRawType(typeDefiningF);
        if (!f.getDeclaringClass().isAssignableFrom(rawType)) {
            return f.getGenericType();
        }
        return $Gson$Types.resolve(typeDefiningF, rawType, f.getGenericType());
    }
}

