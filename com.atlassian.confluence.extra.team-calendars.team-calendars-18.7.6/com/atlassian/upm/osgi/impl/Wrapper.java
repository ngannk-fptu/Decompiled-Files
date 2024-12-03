/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.osgi.impl;

import com.atlassian.upm.osgi.impl.Wrapper2;
import java.util.Collection;
import javax.annotation.Nullable;

public abstract class Wrapper<In, Out> {
    private final Wrapper2<Void, In, Out> wrapper2;

    public Wrapper(String name) {
        this.wrapper2 = new Wrapper2<Void, In, Out>(name){

            @Override
            protected Out wrap(@Nullable Void key, @Nullable In value) {
                return Wrapper.this.wrap(value);
            }
        };
    }

    protected abstract Out wrap(@Nullable In var1);

    public final Out fromSingleton(@Nullable In in) {
        return this.wrapper2.fromSingleton(null, in);
    }

    public final Iterable<Out> fromArray(@Nullable In[] in) {
        return this.wrapper2.fromArray(null, in);
    }

    public final Collection<Out> fromIterable(@Nullable Iterable<In> in) {
        return this.wrapper2.fromIterable(null, in);
    }
}

