/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.CreateIfNull;
import com.opensymphony.xwork2.util.Element;
import com.opensymphony.xwork2.util.Key;
import com.opensymphony.xwork2.util.KeyProperty;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import java.beans.IntrospectionException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultObjectTypeDeterminer
implements ObjectTypeDeterminer {
    protected static final Logger LOG = LogManager.getLogger(DefaultObjectTypeDeterminer.class);
    public static final String KEY_PREFIX = "Key_";
    public static final String ELEMENT_PREFIX = "Element_";
    public static final String KEY_PROPERTY_PREFIX = "KeyProperty_";
    public static final String CREATE_IF_NULL_PREFIX = "CreateIfNull_";
    public static final String DEPRECATED_ELEMENT_PREFIX = "Collection_";
    private ReflectionProvider reflectionProvider;
    private XWorkConverter xworkConverter;

    @Inject
    public DefaultObjectTypeDeterminer(@Inject XWorkConverter converter, @Inject ReflectionProvider provider) {
        this.reflectionProvider = provider;
        this.xworkConverter = converter;
    }

    @Override
    public Class getKeyClass(Class parentClass, String property) {
        Key annotation = this.getAnnotation(parentClass, property, Key.class);
        if (annotation != null) {
            return annotation.value();
        }
        Class clazz = (Class)this.xworkConverter.getConverter(parentClass, KEY_PREFIX + property);
        if (clazz != null) {
            return clazz;
        }
        return this.getClass(parentClass, property, false);
    }

    @Override
    public Class getElementClass(Class parentClass, String property, Object key) {
        Element annotation = this.getAnnotation(parentClass, property, Element.class);
        if (annotation != null) {
            return annotation.value();
        }
        Class clazz = (Class)this.xworkConverter.getConverter(parentClass, ELEMENT_PREFIX + property);
        if (clazz == null && (clazz = (Class)this.xworkConverter.getConverter(parentClass, DEPRECATED_ELEMENT_PREFIX + property)) != null) {
            LOG.info("The Collection_xxx pattern for collection type conversion is deprecated. Please use Element_xxx!");
        }
        if (clazz != null) {
            return clazz;
        }
        clazz = this.getClass(parentClass, property, true);
        return clazz;
    }

    @Override
    public String getKeyProperty(Class parentClass, String property) {
        KeyProperty annotation = this.getAnnotation(parentClass, property, KeyProperty.class);
        if (annotation != null) {
            return annotation.value();
        }
        return (String)this.xworkConverter.getConverter(parentClass, KEY_PROPERTY_PREFIX + property);
    }

    @Override
    public boolean shouldCreateIfNew(Class parentClass, String property, Object target, String keyProperty, boolean isIndexAccessed) {
        CreateIfNull annotation = this.getAnnotation(parentClass, property, CreateIfNull.class);
        if (annotation != null) {
            return annotation.value();
        }
        String configValue = (String)this.xworkConverter.getConverter(parentClass, CREATE_IF_NULL_PREFIX + property);
        if (configValue != null) {
            return BooleanUtils.toBoolean((String)configValue);
        }
        return target instanceof Map || isIndexAccessed;
    }

    protected <T extends Annotation> T getAnnotation(Class parentClass, String property, Class<T> annotationClass) {
        T annotation = null;
        Field field = this.reflectionProvider.getField(parentClass, property);
        if (field != null) {
            annotation = field.getAnnotation(annotationClass);
        }
        if (annotation == null) {
            annotation = this.getAnnotationFromSetter(parentClass, property, annotationClass);
        }
        if (annotation == null) {
            annotation = this.getAnnotationFromGetter(parentClass, property, annotationClass);
        }
        return annotation;
    }

    private <T extends Annotation> T getAnnotationFromGetter(Class parentClass, String property, Class<T> annotationClass) {
        try {
            Method getter = this.reflectionProvider.getGetMethod(parentClass, property);
            if (getter != null) {
                return getter.getAnnotation(annotationClass);
            }
        }
        catch (ReflectionException | IntrospectionException exception) {
            // empty catch block
        }
        return null;
    }

    private <T extends Annotation> T getAnnotationFromSetter(Class parentClass, String property, Class<T> annotationClass) {
        try {
            Method setter = this.reflectionProvider.getSetMethod(parentClass, property);
            if (setter != null) {
                return setter.getAnnotation(annotationClass);
            }
        }
        catch (ReflectionException | IntrospectionException exception) {
            // empty catch block
        }
        return null;
    }

    private Class getClass(Class parentClass, String property, boolean element) {
        try {
            Field field = this.reflectionProvider.getField(parentClass, property);
            Type genericType = null;
            if (field != null) {
                genericType = field.getGenericType();
            }
            if (genericType == null || !(genericType instanceof ParameterizedType)) {
                try {
                    Method setter = this.reflectionProvider.getSetMethod(parentClass, property);
                    genericType = setter != null ? setter.getGenericParameterTypes()[0] : null;
                }
                catch (ReflectionException | IntrospectionException setter) {
                    // empty catch block
                }
            }
            if (genericType == null || !(genericType instanceof ParameterizedType)) {
                try {
                    Method getter = this.reflectionProvider.getGetMethod(parentClass, property);
                    genericType = getter.getGenericReturnType();
                }
                catch (ReflectionException | IntrospectionException getter) {
                    // empty catch block
                }
            }
            if (genericType instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType)genericType;
                int index = element && type.getRawType().toString().contains(Map.class.getName()) ? 1 : 0;
                Type resultType = type.getActualTypeArguments()[index];
                if (resultType instanceof ParameterizedType) {
                    return (Class)((ParameterizedType)resultType).getRawType();
                }
                return (Class)resultType;
            }
        }
        catch (Exception e) {
            LOG.debug("Error while retrieving generic property class for property: {}", (Object)property, (Object)e);
        }
        return null;
    }
}

