/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.annotation.AbstractAliasAwareAnnotationAttributeExtractor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

class MapAnnotationAttributeExtractor
extends AbstractAliasAwareAnnotationAttributeExtractor<Map<String, Object>> {
    MapAnnotationAttributeExtractor(Map<String, Object> attributes, Class<? extends Annotation> annotationType, @Nullable AnnotatedElement annotatedElement) {
        super(annotationType, annotatedElement, MapAnnotationAttributeExtractor.enrichAndValidateAttributes(attributes, annotationType));
    }

    @Override
    @Nullable
    protected Object getRawAttributeValue(Method attributeMethod) {
        return this.getRawAttributeValue(attributeMethod.getName());
    }

    @Override
    @Nullable
    protected Object getRawAttributeValue(String attributeName) {
        return ((Map)this.getSource()).get(attributeName);
    }

    private static Map<String, Object> enrichAndValidateAttributes(Map<String, Object> originalAttributes, Class<? extends Annotation> annotationType) {
        LinkedHashMap<String, Object> attributes = new LinkedHashMap<String, Object>(originalAttributes);
        Map<String, List<String>> attributeAliasMap = AnnotationUtils.getAttributeAliasMap(annotationType);
        for (Method attributeMethod : AnnotationUtils.getAttributeMethods(annotationType)) {
            Class<?> nestedAnnotationType;
            Class<?> actualReturnType;
            Object defaultValue;
            List<String> aliasNames;
            String attributeName = attributeMethod.getName();
            Object attributeValue = attributes.get(attributeName);
            if (attributeValue == null && (aliasNames = attributeAliasMap.get(attributeName)) != null) {
                for (String aliasName : aliasNames) {
                    Object aliasValue = attributes.get(aliasName);
                    if (aliasValue == null) continue;
                    attributeValue = aliasValue;
                    attributes.put(attributeName, attributeValue);
                    break;
                }
            }
            if (attributeValue == null && (defaultValue = AnnotationUtils.getDefaultValue(annotationType, attributeName)) != null) {
                attributeValue = defaultValue;
                attributes.put(attributeName, attributeValue);
            }
            Assert.notNull(attributeValue, () -> String.format("Attributes map %s returned null for required attribute '%s' defined by annotation type [%s].", attributes, attributeName, annotationType.getName()));
            Class<?> requiredReturnType = attributeMethod.getReturnType();
            if (ClassUtils.isAssignable(requiredReturnType, actualReturnType = attributeValue.getClass())) continue;
            boolean converted = false;
            if (requiredReturnType.isArray() && requiredReturnType.getComponentType() == actualReturnType) {
                Object array = Array.newInstance(requiredReturnType.getComponentType(), 1);
                Array.set(array, 0, attributeValue);
                attributes.put(attributeName, array);
                converted = true;
            } else if (Annotation.class.isAssignableFrom(requiredReturnType) && Map.class.isAssignableFrom(actualReturnType)) {
                nestedAnnotationType = requiredReturnType;
                Map map = (Map)attributeValue;
                attributes.put(attributeName, AnnotationUtils.synthesizeAnnotation(map, nestedAnnotationType, null));
                converted = true;
            } else if (requiredReturnType.isArray() && actualReturnType.isArray() && Annotation.class.isAssignableFrom(requiredReturnType.getComponentType()) && Map.class.isAssignableFrom(actualReturnType.getComponentType())) {
                nestedAnnotationType = requiredReturnType.getComponentType();
                Map[] maps = (Map[])attributeValue;
                attributes.put(attributeName, AnnotationUtils.synthesizeAnnotationArray((Map[])maps, nestedAnnotationType));
                converted = true;
            }
            Assert.isTrue(converted, () -> String.format("Attributes map %s returned a value of type [%s] for attribute '%s', but a value of type [%s] is required as defined by annotation type [%s].", attributes, actualReturnType.getName(), attributeName, requiredReturnType.getName(), annotationType.getName()));
        }
        return attributes;
    }
}

