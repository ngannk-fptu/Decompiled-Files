/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.aspectj;

import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

public interface AspectInstanceFactory
extends Ordered {
    public Object getAspectInstance();

    @Nullable
    public ClassLoader getAspectClassLoader();
}

