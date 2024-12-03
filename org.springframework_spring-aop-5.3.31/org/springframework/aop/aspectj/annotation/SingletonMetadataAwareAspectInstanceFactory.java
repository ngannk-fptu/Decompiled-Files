/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.OrderUtils
 */
package org.springframework.aop.aspectj.annotation;

import java.io.Serializable;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.aop.aspectj.annotation.AspectMetadata;
import org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory;
import org.springframework.core.annotation.OrderUtils;

public class SingletonMetadataAwareAspectInstanceFactory
extends SingletonAspectInstanceFactory
implements MetadataAwareAspectInstanceFactory,
Serializable {
    private final AspectMetadata metadata;

    public SingletonMetadataAwareAspectInstanceFactory(Object aspectInstance, String aspectName) {
        super(aspectInstance);
        this.metadata = new AspectMetadata(aspectInstance.getClass(), aspectName);
    }

    @Override
    public final AspectMetadata getAspectMetadata() {
        return this.metadata;
    }

    @Override
    public Object getAspectCreationMutex() {
        return this;
    }

    @Override
    protected int getOrderForAspectClass(Class<?> aspectClass) {
        return OrderUtils.getOrder(aspectClass, (int)Integer.MAX_VALUE);
    }
}

