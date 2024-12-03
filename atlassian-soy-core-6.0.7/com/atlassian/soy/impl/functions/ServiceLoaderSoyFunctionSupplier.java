/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyFunction
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.soy.impl.functions;

import com.atlassian.soy.renderer.SoyFunction;
import com.atlassian.soy.spi.functions.SoyFunctionSupplier;
import com.google.common.collect.ImmutableList;
import java.util.ServiceLoader;

public class ServiceLoaderSoyFunctionSupplier
implements SoyFunctionSupplier {
    private final ServiceLoader<SoyFunction> serviceLoader;

    public ServiceLoaderSoyFunctionSupplier() {
        this.serviceLoader = ServiceLoader.load(SoyFunction.class);
    }

    public ServiceLoaderSoyFunctionSupplier(ClassLoader classLoader) {
        this.serviceLoader = ServiceLoader.load(SoyFunction.class, classLoader);
    }

    @Override
    public Iterable<SoyFunction> get() {
        return ImmutableList.copyOf(this.serviceLoader.iterator());
    }
}

