/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.component.ioc;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;

public interface IoCComponentProviderFactory
extends ComponentProviderFactory<IoCComponentProvider> {
    @Override
    public IoCComponentProvider getComponentProvider(Class<?> var1);

    public IoCComponentProvider getComponentProvider(ComponentContext var1, Class<?> var2);
}

