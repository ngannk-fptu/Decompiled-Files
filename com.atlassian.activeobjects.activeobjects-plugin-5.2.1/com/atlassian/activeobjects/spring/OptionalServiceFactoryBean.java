/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.FactoryBean
 */
package com.atlassian.activeobjects.spring;

import java.util.Objects;
import org.springframework.beans.factory.FactoryBean;

public final class OptionalServiceFactoryBean<T>
implements FactoryBean<T> {
    private final Class<T> type;
    private final T service;
    private final T defaultValue;

    public OptionalServiceFactoryBean(Class<T> type, T service, T defaultValue) {
        this.type = Objects.requireNonNull(type);
        this.service = Objects.requireNonNull(service);
        this.defaultValue = Objects.requireNonNull(defaultValue);
    }

    public T getObject() {
        try {
            this.service.toString();
            return this.service;
        }
        catch (RuntimeException e) {
            if ("ServiceUnavailableException".equals(e.getClass().getSimpleName())) {
                return this.defaultValue;
            }
            throw e;
        }
    }

    public Class<T> getObjectType() {
        return this.type;
    }

    public boolean isSingleton() {
        return true;
    }
}

