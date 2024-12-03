/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.query.impl.getters.AbstractMultiValueGetter;
import com.hazelcast.query.impl.getters.FieldGetter;
import com.hazelcast.query.impl.getters.Getter;
import com.hazelcast.query.impl.getters.MethodGetter;
import com.hazelcast.query.impl.getters.MultiResult;
import com.hazelcast.query.impl.getters.NullGetter;
import com.hazelcast.query.impl.getters.NullMultiValueGetter;
import com.hazelcast.query.impl.getters.ThisGetter;
import com.hazelcast.util.CollectionUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

public final class GetterFactory {
    private static final String ANY_POSTFIX = "[any]";

    private GetterFactory() {
    }

    public static Getter newFieldGetter(Object object, Getter parentGetter, Field field, String modifierSuffix) throws Exception {
        Class<?> fieldType = field.getType();
        Class<?> returnType = null;
        if (GetterFactory.isExtractingFromCollection(fieldType, modifierSuffix)) {
            AbstractMultiValueGetter.validateModifier(modifierSuffix);
            Object currentObject = GetterFactory.getCurrentObject(object, parentGetter);
            if (currentObject == null) {
                return NullGetter.NULL_GETTER;
            }
            if (currentObject instanceof MultiResult) {
                MultiResult multiResult = (MultiResult)currentObject;
                returnType = GetterFactory.extractTypeFromMultiResult(field, multiResult);
            } else {
                Collection collection = (Collection)field.get(currentObject);
                returnType = GetterFactory.getCollectionType(collection);
            }
            if (returnType == null) {
                if (modifierSuffix.equals(ANY_POSTFIX)) {
                    return NullMultiValueGetter.NULL_MULTIVALUE_GETTER;
                }
                return NullGetter.NULL_GETTER;
            }
        } else if (GetterFactory.isExtractingFromArray(fieldType, modifierSuffix)) {
            AbstractMultiValueGetter.validateModifier(modifierSuffix);
            Object currentObject = GetterFactory.getCurrentObject(object, parentGetter);
            if (currentObject == null) {
                return NullGetter.NULL_GETTER;
            }
        }
        return new FieldGetter(parentGetter, field, modifierSuffix, returnType);
    }

    private static Class<?> extractTypeFromMultiResult(Field field, MultiResult multiResult) throws Exception {
        Class<?> returnType = null;
        for (Object o : multiResult.getResults()) {
            Collection collection;
            if (o != null && (returnType = GetterFactory.getCollectionType(collection = (Collection)field.get(o))) != null) break;
        }
        return returnType;
    }

    public static Getter newMethodGetter(Object object, Getter parentGetter, Method method, String modifierSuffix) throws Exception {
        Class<?> methodReturnType = method.getReturnType();
        Class<?> returnType = null;
        if (GetterFactory.isExtractingFromCollection(methodReturnType, modifierSuffix)) {
            AbstractMultiValueGetter.validateModifier(modifierSuffix);
            Object currentObject = GetterFactory.getCurrentObject(object, parentGetter);
            if (currentObject == null) {
                return NullGetter.NULL_GETTER;
            }
            if (currentObject instanceof MultiResult) {
                MultiResult multiResult = (MultiResult)currentObject;
                returnType = GetterFactory.extractTypeFromMultiResult(method, multiResult);
            } else {
                Collection collection = (Collection)method.invoke(currentObject, new Object[0]);
                returnType = GetterFactory.getCollectionType(collection);
            }
            if (returnType == null) {
                if (modifierSuffix.equals(ANY_POSTFIX)) {
                    return NullMultiValueGetter.NULL_MULTIVALUE_GETTER;
                }
                return NullGetter.NULL_GETTER;
            }
        } else if (GetterFactory.isExtractingFromArray(methodReturnType, modifierSuffix)) {
            AbstractMultiValueGetter.validateModifier(modifierSuffix);
            Object currentObject = GetterFactory.getCurrentObject(object, parentGetter);
            if (currentObject == null) {
                return NullGetter.NULL_GETTER;
            }
        }
        return new MethodGetter(parentGetter, method, modifierSuffix, returnType);
    }

    private static Class<?> extractTypeFromMultiResult(Method method, MultiResult multiResult) throws Exception {
        Class<?> returnType = null;
        for (Object o : multiResult.getResults()) {
            Collection collection;
            if (o != null && (returnType = GetterFactory.getCollectionType(collection = (Collection)method.invoke(o, new Object[0]))) != null) break;
        }
        return returnType;
    }

    public static Getter newThisGetter(Getter parent, Object object) {
        return new ThisGetter(parent, object);
    }

    private static Class<?> getCollectionType(Collection collection) throws Exception {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        Object targetObject = CollectionUtil.getItemAtPositionOrNull(collection, 0);
        if (targetObject == null) {
            for (Object object : collection) {
                if (object == null) continue;
                return object.getClass();
            }
            return null;
        }
        return targetObject.getClass();
    }

    private static boolean isExtractingFromCollection(Class<?> type, String modifierSuffix) {
        return modifierSuffix != null && Collection.class.isAssignableFrom(type);
    }

    private static boolean isExtractingFromArray(Class<?> type, String modifierSuffix) {
        return modifierSuffix != null && type.isArray();
    }

    private static Object getCurrentObject(Object obj, Getter parent) throws Exception {
        return parent == null ? obj : parent.getValue(obj);
    }
}

