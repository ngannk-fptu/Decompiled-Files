/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.objenesis.strategy;

import org.springframework.objenesis.instantiator.ObjectInstantiator;

public interface InstantiatorStrategy {
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> var1);
}

