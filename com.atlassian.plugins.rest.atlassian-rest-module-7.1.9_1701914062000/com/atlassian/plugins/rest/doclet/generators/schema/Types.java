/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.joda.time.DateTime
 */
package com.atlassian.plugins.rest.doclet.generators.schema;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.plugins.rest.doclet.generators.schema.RichClass;
import com.atlassian.plugins.rest.doclet.generators.schema.Schema;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.AnnotatedElement;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonRawValue;
import org.joda.time.DateTime;

public class Types {
    @TenantAware(value=TenancyScope.TENANTLESS)
    private static final Map<Class<?>, Schema.Type> classToJsonType;
    @TenantAware(value=TenancyScope.TENANTLESS)
    private static final Map<String, Schema.Type> primitiveTypeToJsonType;

    private Types() {
    }

    public static Schema.Type resolveType(RichClass modelClass, AnnotatedElement containingField) {
        if (containingField != null && containingField.isAnnotationPresent(JsonRawValue.class)) {
            return Schema.Type.Any;
        }
        Optional<Schema.Type> primitiveType = Types.getPrimitiveType(modelClass.getActualClass());
        if (primitiveType.isPresent()) {
            return primitiveType.get();
        }
        if (Types.isCollection(modelClass)) {
            return Schema.Type.Array;
        }
        if (modelClass.getActualClass() == Object.class) {
            return Schema.Type.Any;
        }
        return Schema.Type.Object;
    }

    public static boolean isPrimitive(Class<?> modelClass) {
        return Types.getPrimitiveType(modelClass).isPresent();
    }

    private static Optional<Schema.Type> getPrimitiveType(Class<?> modelClass) {
        for (Map.Entry<Class<?>, Schema.Type> classToTypeName : classToJsonType.entrySet()) {
            if (!classToTypeName.getKey().isAssignableFrom(modelClass)) continue;
            return Optional.of(classToTypeName.getValue());
        }
        if (modelClass.isPrimitive()) {
            Schema.Type mapped = primitiveTypeToJsonType.get(modelClass.getSimpleName());
            return Optional.of(mapped != null ? mapped : Schema.Type.String);
        }
        return Optional.empty();
    }

    public static boolean isCollection(RichClass type) {
        return type.getActualClass().isArray() || Iterable.class.isAssignableFrom(type.getActualClass()) && !type.getGenericTypes().isEmpty();
    }

    public static boolean isJDKClass(Class<?> type) {
        String name = type.getCanonicalName() != null ? type.getCanonicalName() : type.getName();
        return name.startsWith("java");
    }

    static {
        ImmutableMap.Builder builder = ImmutableMap.builder().put(String.class, (Object)Schema.Type.String).put(Integer.class, (Object)Schema.Type.Integer).put(Long.class, (Object)Schema.Type.Integer).put(Number.class, (Object)Schema.Type.Number).put(Boolean.class, (Object)Schema.Type.Boolean).put(URI.class, (Object)Schema.Type.Uri).put(Enum.class, (Object)Schema.Type.String).put(Date.class, (Object)Schema.Type.String);
        try {
            builder.put(DateTime.class, (Object)Schema.Type.String);
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            // empty catch block
        }
        classToJsonType = builder.build();
        primitiveTypeToJsonType = ImmutableMap.builder().put((Object)"boolean", (Object)Schema.Type.Boolean).put((Object)"int", (Object)Schema.Type.Integer).put((Object)"short", (Object)Schema.Type.Integer).put((Object)"long", (Object)Schema.Type.Integer).put((Object)"double", (Object)Schema.Type.Number).put((Object)"float", (Object)Schema.Type.Number).put((Object)"char", (Object)Schema.Type.String).put((Object)"byte", (Object)Schema.Type.Integer).build();
    }
}

