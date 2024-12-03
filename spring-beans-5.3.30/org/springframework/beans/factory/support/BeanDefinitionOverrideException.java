/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.NonNull
 */
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.lang.NonNull;

public class BeanDefinitionOverrideException
extends BeanDefinitionStoreException {
    private final BeanDefinition beanDefinition;
    private final BeanDefinition existingDefinition;

    public BeanDefinitionOverrideException(String beanName, BeanDefinition beanDefinition, BeanDefinition existingDefinition) {
        super(beanDefinition.getResourceDescription(), beanName, "Cannot register bean definition [" + beanDefinition + "] for bean '" + beanName + "': There is already [" + existingDefinition + "] bound.");
        this.beanDefinition = beanDefinition;
        this.existingDefinition = existingDefinition;
    }

    @Override
    @NonNull
    public String getResourceDescription() {
        return String.valueOf(super.getResourceDescription());
    }

    @Override
    @NonNull
    public String getBeanName() {
        return String.valueOf(super.getBeanName());
    }

    public BeanDefinition getBeanDefinition() {
        return this.beanDefinition;
    }

    public BeanDefinition getExistingDefinition() {
        return this.existingDefinition;
    }
}

