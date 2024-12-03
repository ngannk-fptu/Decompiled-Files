/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.security.util;

import java.lang.reflect.Field;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public final class FieldUtils {
    private FieldUtils() {
    }

    public static Field getField(Class<?> clazz, String fieldName) throws IllegalStateException {
        Assert.notNull(clazz, (String)"Class required");
        Assert.hasText((String)fieldName, (String)"Field name required");
        try {
            return clazz.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null) {
                return FieldUtils.getField(clazz.getSuperclass(), fieldName);
            }
            throw new IllegalStateException("Could not locate field '" + fieldName + "' on class " + clazz);
        }
    }

    public static Object getFieldValue(Object bean, String fieldName) throws IllegalAccessException {
        Assert.notNull((Object)bean, (String)"Bean cannot be null");
        Assert.hasText((String)fieldName, (String)"Field name required");
        String[] nestedFields = StringUtils.tokenizeToStringArray((String)fieldName, (String)".");
        Class<?> componentClass = bean.getClass();
        Object value = bean;
        for (String nestedField : nestedFields) {
            Field field = FieldUtils.getField(componentClass, nestedField);
            field.setAccessible(true);
            value = field.get(value);
            if (value == null) continue;
            componentClass = value.getClass();
        }
        return value;
    }

    public static Object getProtectedFieldValue(String protectedField, Object object) {
        Field field = FieldUtils.getField(object.getClass(), protectedField);
        try {
            field.setAccessible(true);
            return field.get(object);
        }
        catch (Exception ex) {
            ReflectionUtils.handleReflectionException((Exception)ex);
            return null;
        }
    }

    public static void setProtectedFieldValue(String protectedField, Object object, Object newValue) {
        Field field = FieldUtils.getField(object.getClass(), protectedField);
        try {
            field.setAccessible(true);
            field.set(object, newValue);
        }
        catch (Exception ex) {
            ReflectionUtils.handleReflectionException((Exception)ex);
        }
    }
}

