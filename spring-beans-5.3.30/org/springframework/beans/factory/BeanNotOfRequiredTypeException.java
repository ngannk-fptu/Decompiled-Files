/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.ClassUtils
 */
package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.util.ClassUtils;

public class BeanNotOfRequiredTypeException
extends BeansException {
    private final String beanName;
    private final Class<?> requiredType;
    private final Class<?> actualType;

    public BeanNotOfRequiredTypeException(String beanName, Class<?> requiredType, Class<?> actualType) {
        super("Bean named '" + beanName + "' is expected to be of type '" + ClassUtils.getQualifiedName(requiredType) + "' but was actually of type '" + ClassUtils.getQualifiedName(actualType) + "'");
        this.beanName = beanName;
        this.requiredType = requiredType;
        this.actualType = actualType;
    }

    public String getBeanName() {
        return this.beanName;
    }

    public Class<?> getRequiredType() {
        return this.requiredType;
    }

    public Class<?> getActualType() {
        return this.actualType;
    }
}

