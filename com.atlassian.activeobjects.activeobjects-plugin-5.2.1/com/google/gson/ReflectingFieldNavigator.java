/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.Cache;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.LruCache;
import com.google.gson.ObjectNavigator;
import com.google.gson.ObjectTypePair;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.$Gson$Types;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ReflectingFieldNavigator {
    private static final Cache<Type, List<FieldAttributes>> fieldsCache = new LruCache<Type, List<FieldAttributes>>(500);
    private final ExclusionStrategy exclusionStrategy;

    ReflectingFieldNavigator(ExclusionStrategy exclusionStrategy) {
        this.exclusionStrategy = $Gson$Preconditions.checkNotNull(exclusionStrategy);
    }

    void visitFieldsReflectively(ObjectTypePair objTypePair, ObjectNavigator.Visitor visitor) {
        Type moreSpecificType = objTypePair.getMoreSpecificType();
        Object obj = objTypePair.getObject();
        for (FieldAttributes fieldAttributes : this.getAllFields(moreSpecificType, objTypePair.getType())) {
            Type resolvedTypeOfField;
            boolean visitedWithCustomHandler;
            if (this.exclusionStrategy.shouldSkipField(fieldAttributes) || this.exclusionStrategy.shouldSkipClass(fieldAttributes.getDeclaredClass()) || (visitedWithCustomHandler = visitor.visitFieldUsingCustomHandler(fieldAttributes, resolvedTypeOfField = fieldAttributes.getResolvedType(), obj))) continue;
            if ($Gson$Types.isArray(resolvedTypeOfField)) {
                visitor.visitArrayField(fieldAttributes, resolvedTypeOfField, obj);
                continue;
            }
            visitor.visitObjectField(fieldAttributes, resolvedTypeOfField, obj);
        }
    }

    private List<FieldAttributes> getAllFields(Type type, Type declaredType) {
        List<FieldAttributes> fields = fieldsCache.getElement(type);
        if (fields == null) {
            fields = new ArrayList<FieldAttributes>();
            for (Class<?> curr : this.getInheritanceHierarchy(type)) {
                AccessibleObject[] classFields;
                AccessibleObject[] currentClazzFields = curr.getDeclaredFields();
                AccessibleObject.setAccessible(currentClazzFields, true);
                for (AccessibleObject f : classFields = currentClazzFields) {
                    fields.add(new FieldAttributes(curr, (Field)f, declaredType));
                }
            }
            fieldsCache.addElement(type, fields);
        }
        return fields;
    }

    private List<Class<?>> getInheritanceHierarchy(Type type) {
        Class<?> topLevelClass;
        ArrayList classes = new ArrayList();
        for (Class<?> curr = topLevelClass = $Gson$Types.getRawType(type); curr != null && !curr.equals(Object.class); curr = curr.getSuperclass()) {
            if (curr.isSynthetic()) continue;
            classes.add(curr);
        }
        return classes;
    }
}

