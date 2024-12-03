/*
 * Decompiled with CFR 0.152.
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
    public static void copyPropertiesToBean(Annotation ann, Object bean2, String ... excludedProperties) {
        AnnotationBeanUtils.copyPropertiesToBean(ann, bean2, null, excludedProperties);
    }

    public static void copyPropertiesToBean(Annotation ann, Object bean2, @Nullable StringValueResolver valueResolver, String ... excludedProperties) {
        HashSet<String> excluded = excludedProperties.length == 0 ? Collections.emptySet() : new HashSet<String>(Arrays.asList(excludedProperties));
        Method[] annotationProperties = ann.annotationType().getDeclaredMethods();
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean2);
        for (Method annotationProperty : annotationProperties) {
            String propertyName = annotationProperty.getName();
            if (excluded.contains(propertyName) || !bw.isWritableProperty(propertyName)) continue;
            Object value = ReflectionUtils.invokeMethod(annotationProperty, ann);
            if (valueResolver != null && value instanceof String) {
                value = valueResolver.resolveStringValue((String)value);
            }
            bw.setPropertyValue(propertyName, value);
        }
    }
}

