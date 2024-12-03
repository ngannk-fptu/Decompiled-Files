/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.Ordered
 *  org.springframework.lang.Nullable
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

