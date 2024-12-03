/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.component;

import com.sun.jersey.core.spi.component.ComponentProvider;

public interface ComponentProviderFactory<C extends ComponentProvider> {
    public C getComponentProvider(Class<?> var1);
}

