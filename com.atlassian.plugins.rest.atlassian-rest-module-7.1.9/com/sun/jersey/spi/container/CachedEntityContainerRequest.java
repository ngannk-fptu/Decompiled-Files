/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.spi.container.AdaptingContainerRequest;
import com.sun.jersey.spi.container.ContainerRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class CachedEntityContainerRequest
extends AdaptingContainerRequest {
    Object entity;

    public CachedEntityContainerRequest(ContainerRequest acr) {
        super(acr);
    }

    @Override
    public <T> T getEntity(Class<T> type) throws ClassCastException {
        if (this.entity == null) {
            T t = this.acr.getEntity(type);
            this.entity = t;
            return t;
        }
        return type.cast(this.entity);
    }

    @Override
    public <T> T getEntity(Class<T> type, Type genericType, Annotation[] as) throws ClassCastException {
        if (this.entity == null) {
            T t = this.acr.getEntity(type, genericType, as);
            this.entity = t;
            return t;
        }
        return type.cast(this.entity);
    }
}

