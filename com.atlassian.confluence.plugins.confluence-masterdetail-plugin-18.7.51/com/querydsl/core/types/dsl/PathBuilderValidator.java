/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Primitives
 */
package com.querydsl.core.types.dsl;

import com.google.common.primitives.Primitives;
import com.querydsl.core.util.BeanUtils;
import com.querydsl.core.util.ReflectionUtils;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public interface PathBuilderValidator
extends Serializable {
    public static final PathBuilderValidator DEFAULT = new PathBuilderValidator(){

        @Override
        public Class<?> validate(Class<?> parent, String property, Class<?> propertyType) {
            return propertyType;
        }
    };
    public static final PathBuilderValidator FIELDS = new PathBuilderValidator(){

        @Override
        public Class<?> validate(Class<?> parent, String property, Class<?> propertyType) {
            while (!parent.equals(Object.class)) {
                try {
                    Field field = parent.getDeclaredField(property);
                    if (Map.class.isAssignableFrom(field.getType())) {
                        return ReflectionUtils.getTypeParameterAsClass(field.getGenericType(), 1);
                    }
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        return ReflectionUtils.getTypeParameterAsClass(field.getGenericType(), 0);
                    }
                    return Primitives.wrap(field.getType());
                }
                catch (NoSuchFieldException e) {
                    parent = parent.getSuperclass();
                }
            }
            return null;
        }
    };
    public static final PathBuilderValidator PROPERTIES = new PathBuilderValidator(){

        @Override
        public Class<?> validate(Class<?> parent, String property, Class<?> propertyType) {
            Method getter = BeanUtils.getAccessor("get", property, parent);
            if (getter == null && Primitives.wrap(propertyType).equals(Boolean.class)) {
                getter = BeanUtils.getAccessor("is", property, parent);
            }
            if (getter != null) {
                if (Map.class.isAssignableFrom(getter.getReturnType())) {
                    return ReflectionUtils.getTypeParameterAsClass(getter.getGenericReturnType(), 1);
                }
                if (Collection.class.isAssignableFrom(getter.getReturnType())) {
                    return ReflectionUtils.getTypeParameterAsClass(getter.getGenericReturnType(), 0);
                }
                return Primitives.wrap(getter.getReturnType());
            }
            return null;
        }
    };

    public Class<?> validate(Class<?> var1, String var2, Class<?> var3);
}

