/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.aspectj.annotation;

import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.aop.aspectj.annotation.AspectMetadata;
import org.springframework.lang.Nullable;

public interface MetadataAwareAspectInstanceFactory
extends AspectInstanceFactory {
    public AspectMetadata getAspectMetadata();

    @Nullable
    public Object getAspectCreationMutex();
}

