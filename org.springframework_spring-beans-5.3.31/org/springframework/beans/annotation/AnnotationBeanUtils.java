/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringValueResolver
 */
package org.springframework.beans.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringValueResolver;

@Deprecated
public abstract class AnnotationBeanUtils {
    public static void copyPropertiesToBean(Annotation ann, Object bean, String ... excludedProperties) {
        AnnotationBeanUtils.copyPropertiesToBean(ann, bean, null, excludedProperties);
    }

    public static void copyPropertiesToBean(Annotation ann, Object bean, @Nullable StringValueResolver valueResolver, String ... excludedProperties) {
        HashSet<String> excluded = excludedProperties.length == 0 ? Collections.emptySet() : new HashSet<String>(Arrays.asList(excludedProperties));
        Method[] annotationProperties = ann.annotationType().getDeclaredMethods();
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        for (Method annotationProperty : annotationProperties) {
            String propertyName = annotationProperty.getName();
            if (excluded.contains(propertyName) || !bw.isWritableProperty(propertyName)) continue;
            Object value = ReflectionUtils.invokeMethod((Method)annotationProperty, (Object)ann);
            if (valueResolver != null && value instanceof String) {
                value = valueResolver.resolveStringValue((String)value);
            }
            bw.setPropertyValue(propertyName, value);
        }
    }
}

