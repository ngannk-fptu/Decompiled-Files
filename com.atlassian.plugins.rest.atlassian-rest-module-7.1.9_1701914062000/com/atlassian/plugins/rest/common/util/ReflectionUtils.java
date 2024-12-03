/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.common.util;

import com.atlassian.plugins.rest.common.util.FieldAccessibilityException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtils {
    private static final Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

    private ReflectionUtils() {
    }

    public static Object getFieldValue(Field field, Object object) {
        boolean accessible = field.isAccessible();
        try {
            if (!accessible) {
                field.setAccessible(true);
            }
            Object object2 = field.get(object);
            return object2;
        }
        catch (IllegalAccessException e) {
            throw new FieldAccessibilityException(field, object, e);
        }
        finally {
            if (!accessible) {
                field.setAccessible(false);
            }
        }
    }

    public static void setFieldValue(Field field, Object object, Object value) {
        boolean accessible = field.isAccessible();
        try {
            if (!accessible) {
                field.setAccessible(true);
            }
            field.set(object, value);
        }
        catch (IllegalAccessException e) {
            throw new FieldAccessibilityException(field, object, e);
        }
        finally {
            if (!accessible) {
                field.setAccessible(false);
            }
        }
    }

    public static List<Field> getDeclaredFields(Class clazz) {
        if (clazz == null) {
            return new ArrayList<Field>();
        }
        List<Field> superFields = ReflectionUtils.getDeclaredFields(clazz.getSuperclass());
        superFields.addAll(0, Arrays.asList(clazz.getDeclaredFields()));
        return superFields;
    }

    public static <T extends Annotation> T getAnnotation(@Nonnull Class<T> annotationType, @Nullable AnnotatedElement element) {
        Objects.requireNonNull(annotationType, "An annotation is required");
        if (element == null) {
            return null;
        }
        for (Annotation a : element.getAnnotations()) {
            if (!StringUtils.equals((CharSequence)a.annotationType().getCanonicalName(), (CharSequence)annotationType.getCanonicalName())) continue;
            if (!a.annotationType().equals(annotationType)) {
                log.warn("Detected usage of the {} annotation loaded from elsewhere. {} != {}", new Object[]{annotationType.getCanonicalName(), annotationType.getClassLoader(), a.annotationType().getClassLoader()});
                return null;
            }
            return (T)a;
        }
        return null;
    }
}

