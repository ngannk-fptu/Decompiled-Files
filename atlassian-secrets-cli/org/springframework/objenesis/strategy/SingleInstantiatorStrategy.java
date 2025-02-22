/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.objenesis.strategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.strategy.InstantiatorStrategy;

public class SingleInstantiatorStrategy
implements InstantiatorStrategy {
    private Constructor<?> constructor;

    public <T extends ObjectInstantiator<?>> SingleInstantiatorStrategy(Class<T> instantiator) {
        try {
            this.constructor = instantiator.getConstructor(Class.class);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }

    @Override
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
        try {
            return (ObjectInstantiator)this.constructor.newInstance(type);
        }
        catch (InstantiationException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
        catch (InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }
}

