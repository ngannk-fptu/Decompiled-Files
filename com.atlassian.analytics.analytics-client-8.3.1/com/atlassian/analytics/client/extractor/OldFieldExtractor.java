/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.google.common.collect.Maps
 *  org.springframework.beans.BeanWrapperImpl
 */
package com.atlassian.analytics.client.extractor;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.analytics.client.extractor.FieldExtractor;
import com.atlassian.analytics.client.extractor.PropertyExtractor;
import com.google.common.collect.Maps;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeanWrapperImpl;

public class OldFieldExtractor
implements FieldExtractor {
    private final PropertyExtractor propertyExtractor;

    public OldFieldExtractor(PropertyExtractor propertyExtractor) {
        this.propertyExtractor = propertyExtractor;
    }

    @Override
    public Map<String, Object> extractEventProperties(Object event) {
        HashMap result = Maps.newHashMap();
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(event);
        for (PropertyDescriptor property : beanWrapper.getPropertyDescriptors()) {
            String name = property.getName();
            if (this.propertyExtractor.isExcluded(name) || this.isEventName(property)) continue;
            Object value = beanWrapper.getPropertyValue(name);
            result.putAll(this.propertyExtractor.extractProperty(name, value));
        }
        result.putAll(this.propertyExtractor.enrichProperties(event));
        return result;
    }

    private boolean isEventName(PropertyDescriptor property) {
        Method readMethod = property.getReadMethod();
        if (readMethod != null) {
            for (Annotation annotation : readMethod.getDeclaredAnnotations()) {
                if (!annotation.annotationType().equals(EventName.class)) continue;
                return true;
            }
        }
        return false;
    }
}

