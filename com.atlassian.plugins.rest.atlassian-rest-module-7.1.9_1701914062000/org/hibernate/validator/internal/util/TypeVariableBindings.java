/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.validator.internal.util.CollectionHelper;

public class TypeVariableBindings {
    private TypeVariableBindings() {
    }

    public static Map<Class<?>, Map<TypeVariable<?>, TypeVariable<?>>> getTypeVariableBindings(Class<?> type) {
        TypeVariable<Class<?>>[] subTypeParameters;
        HashMap allBindings = new HashMap();
        HashMap currentBindings = new HashMap();
        for (TypeVariable<Class<?>> typeVariable : subTypeParameters = type.getTypeParameters()) {
            currentBindings.put(typeVariable, typeVariable);
        }
        allBindings.put(type, currentBindings);
        TypeVariableBindings.collectTypeBindings(type, allBindings, currentBindings);
        allBindings.put(Object.class, Collections.emptyMap());
        return CollectionHelper.toImmutableMap(allBindings);
    }

    private static void collectTypeBindings(Class<?> subType, Map<Class<?>, Map<TypeVariable<?>, TypeVariable<?>>> allBindings, Map<TypeVariable<?>, TypeVariable<?>> bindings) {
        TypeVariableBindings.processGenericSuperType(allBindings, bindings, subType.getGenericSuperclass());
        for (Type genericInterface : subType.getGenericInterfaces()) {
            TypeVariableBindings.processGenericSuperType(allBindings, bindings, genericInterface);
        }
    }

    private static void processGenericSuperType(Map<Class<?>, Map<TypeVariable<?>, TypeVariable<?>>> allBindings, Map<TypeVariable<?>, TypeVariable<?>> bindings, Type genericSuperType) {
        if (genericSuperType == null) {
            return;
        }
        if (genericSuperType instanceof ParameterizedType) {
            HashMap newBindings = new HashMap();
            Type[] typeArguments = ((ParameterizedType)genericSuperType).getActualTypeArguments();
            TypeVariable<Class<T>>[] typeParameters = ((Class)((ParameterizedType)genericSuperType).getRawType()).getTypeParameters();
            for (int i = 0; i < typeArguments.length; ++i) {
                Type typeArgument = typeArguments[i];
                TypeVariable typeParameter = typeParameters[i];
                boolean typeParameterFoundInSubType = false;
                for (Map.Entry<TypeVariable<?>, TypeVariable<?>> subTypeParameter : bindings.entrySet()) {
                    if (!typeArgument.equals(subTypeParameter.getValue())) continue;
                    newBindings.put(subTypeParameter.getKey(), typeParameter);
                    typeParameterFoundInSubType = true;
                }
                if (typeParameterFoundInSubType) continue;
                newBindings.put(typeParameter, typeParameter);
            }
            allBindings.put((Class)((ParameterizedType)genericSuperType).getRawType(), newBindings);
            TypeVariableBindings.collectTypeBindings((Class)((ParameterizedType)genericSuperType).getRawType(), allBindings, newBindings);
        } else if (genericSuperType instanceof Class) {
            allBindings.put((Class)genericSuperType, Collections.emptyMap());
            TypeVariableBindings.collectTypeBindings((Class)genericSuperType, allBindings, new HashMap());
        } else {
            throw new IllegalArgumentException("Unexpected type: " + genericSuperType);
        }
    }
}

