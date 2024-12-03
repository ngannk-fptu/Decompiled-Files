/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.beans.PropertyValues
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.TypedStringValue
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import org.eclipse.gemini.blueprint.blueprint.reflect.MetadataUtils;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleServiceReferenceComponentMetadata;
import org.osgi.service.blueprint.reflect.ReferenceMetadata;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;

class SimpleReferenceMetadata
extends SimpleServiceReferenceComponentMetadata
implements ReferenceMetadata {
    private static final String TIMEOUT_PROP = "timeout";
    private static final long DEFAULT_TIMEOUT = 300000L;
    private final long timeout;

    public SimpleReferenceMetadata(String name, BeanDefinition definition) {
        super(name, definition);
        Object value;
        MutablePropertyValues pvs = this.beanDefinition.getPropertyValues();
        this.timeout = pvs.contains(TIMEOUT_PROP) ? Long.parseLong((value = MetadataUtils.getValue((PropertyValues)pvs, TIMEOUT_PROP)) instanceof String ? (String)value : ((TypedStringValue)value).getValue()) : 300000L;
    }

    @Override
    public long getTimeout() {
        return this.timeout;
    }
}

