/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Context
 */
package com.sun.jersey.core.impl.provider.xml;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import java.lang.reflect.Type;
import javax.ws.rs.core.Context;

public abstract class ThreadLocalSingletonContextProvider<T>
implements InjectableProvider<Context, Type> {
    private final Class<T> t;
    private final ThreadLocal<T> rf;

    protected ThreadLocalSingletonContextProvider(Class<T> t) {
        this.t = t;
        this.rf = new ThreadLocal<T>(){

            @Override
            protected synchronized T initialValue() {
                return ThreadLocalSingletonContextProvider.this.getInstance();
            }
        };
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.Singleton;
    }

    @Override
    public Injectable<T> getInjectable(ComponentContext ic, Context a, Type c) {
        if (c == this.t) {
            return new Injectable<T>(){

                @Override
                public T getValue() {
                    return ThreadLocalSingletonContextProvider.this.rf.get();
                }
            };
        }
        return null;
    }

    protected abstract T getInstance();
}

