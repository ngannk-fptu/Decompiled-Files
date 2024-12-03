/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.Ordered
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.aop.aspectj;

import java.io.Serializable;
import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class SingletonAspectInstanceFactory
implements AspectInstanceFactory,
Serializable {
    private final Object aspectInstance;

    public SingletonAspectInstanceFactory(Object aspectInstance) {
        Assert.notNull((Object)aspectInstance, (String)"Aspect instance must not be null");
        this.aspectInstance = aspectInstance;
    }

    @Override
    public final Object getAspectInstance() {
        return this.aspectInstance;
    }

    @Override
    @Nullable
    public ClassLoader getAspectClassLoader() {
        return this.aspectInstance.getClass().getClassLoader();
    }

    public int getOrder() {
        if (this.aspectInstance instanceof Ordered) {
            return ((Ordered)this.aspectInstance).getOrder();
        }
        return this.getOrderForAspectClass(this.aspectInstance.getClass());
    }

    protected int getOrderForAspectClass(Class<?> aspectClass) {
        return Integer.MAX_VALUE;
    }
}

