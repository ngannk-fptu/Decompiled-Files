/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableSet
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlTransient
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package com.atlassian.plugins.rest.doclet.generators.schema;

import com.atlassian.plugins.rest.doclet.generators.schema.Types;
import com.atlassian.rest.annotation.RestProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.codehaus.jackson.annotate.JsonIgnore;

public final class Annotations {
    private static final Set<Class<? extends Annotation>> propertyAnnotations = ImmutableSet.of(org.codehaus.jackson.annotate.JsonProperty.class, JsonProperty.class, XmlAttribute.class, XmlElement.class, XmlJavaTypeAdapter.class);
    private static final Set<Class<? extends Annotation>> autodetectAnnotations = ImmutableSet.of(org.codehaus.jackson.annotate.JsonAutoDetect.class, JsonAutoDetect.class, XmlRootElement.class);
    private static final Set<Class<? extends Annotation>> ignoreAnnotations = ImmutableSet.of(JsonIgnore.class, com.fasterxml.jackson.annotation.JsonIgnore.class, XmlTransient.class);

    private Annotations() {
    }

    public static boolean shouldFieldBeIncludedInSchema(AnnotatedElement element, String name, Class<?> modelClass, RestProperty.Scope scope) {
        boolean isJsonField = Annotations.isCustomInterface(modelClass) || Annotations.isAnyAnnotationPresent(element, propertyAnnotations) || element instanceof Field && Annotations.isAnyAnnotationPresent(modelClass, autodetectAnnotations);
        RestProperty.Scope fieldScope = element.isAnnotationPresent(RestProperty.class) ? element.getAnnotation(RestProperty.class).scope() : RestProperty.Scope.AUTO;
        return isJsonField && !Annotations.isIgnored(name, modelClass) && scope.includes(fieldScope, name);
    }

    private static boolean isIgnored(String name, Class<?> modelClass) {
        try {
            for (PropertyDescriptor propertyDescriptor : Annotations.getPropertyDescriptors(modelClass)) {
                Method getter = propertyDescriptor.getReadMethod();
                if (getter == null || !propertyDescriptor.getName().equals(name) || !Annotations.isAnyAnnotationPresent(getter, ignoreAnnotations)) continue;
                return true;
            }
            Field field = modelClass.getDeclaredField(name);
            return field != null && Annotations.isAnyAnnotationPresent(field, ignoreAnnotations);
        }
        catch (NoSuchFieldException ex) {
            return false;
        }
    }

    private static boolean isCustomInterface(Class<?> modelClass) {
        return !Types.isJDKClass(modelClass) && modelClass.isInterface();
    }

    public static String resolveFieldName(AnnotatedElement element, String defaultName) {
        if (element.isAnnotationPresent(org.codehaus.jackson.annotate.JsonProperty.class)) {
            return Optional.ofNullable(Strings.emptyToNull((String)element.getAnnotation(org.codehaus.jackson.annotate.JsonProperty.class).value())).orElse(defaultName);
        }
        if (element.isAnnotationPresent(XmlElement.class)) {
            String name = element.getAnnotation(XmlElement.class).name();
            return !name.equals("##default") ? name : defaultName;
        }
        if (element.isAnnotationPresent(XmlAttribute.class)) {
            String name = element.getAnnotation(XmlAttribute.class).name();
            return !name.equals("##default") ? name : defaultName;
        }
        return defaultName;
    }

    public static boolean isRequired(AnnotatedElement element) {
        return element.isAnnotationPresent(JsonProperty.class) && element.getAnnotation(JsonProperty.class).required() || element.isAnnotationPresent(RestProperty.class) && element.getAnnotation(RestProperty.class).required();
    }

    public static String getDescription(AnnotatedElement field) {
        if (field != null) {
            if (field.isAnnotationPresent(JsonPropertyDescription.class)) {
                return Strings.emptyToNull((String)field.getAnnotation(JsonPropertyDescription.class).value());
            }
            if (field.isAnnotationPresent(RestProperty.class)) {
                return Strings.emptyToNull((String)field.getAnnotation(RestProperty.class).description());
            }
        }
        return null;
    }

    private static boolean isAnyAnnotationPresent(AnnotatedElement element, Iterable<Class<? extends Annotation>> annotations) {
        for (Class<? extends Annotation> annotation : annotations) {
            if (!element.isAnnotationPresent(annotation)) continue;
            return true;
        }
        return false;
    }

    private static Iterable<PropertyDescriptor> getPropertyDescriptors(Class<?> type) {
        try {
            return Arrays.asList(Introspector.getBeanInfo(type).getPropertyDescriptors());
        }
        catch (IntrospectionException e) {
            return Collections.emptyList();
        }
    }
}

