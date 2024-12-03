/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.xml;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;
import javax.ws.rs.core.Context;

public abstract class LazySingletonContextProvider<T>
implements InjectableProvider<Context, Type> {
    private final Class<T> t;
    private final AtomicReference<T> rf = new AtomicReference();

    protected LazySingletonContextProvider(Class<T> t) {
        this.t = t;
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
                    return LazySingletonContextProvider.this.get();
                }
            };
        }
        return null;
    }

    private T get() {
        T f = this.rf.get();
        if (f == null) {
            T nf = this.getInstance();
            this.rf.compareAndSet(null, nf);
            f = this.rf.get();
        }
        return f;
    }

    protected abstract T getInstance();
}

