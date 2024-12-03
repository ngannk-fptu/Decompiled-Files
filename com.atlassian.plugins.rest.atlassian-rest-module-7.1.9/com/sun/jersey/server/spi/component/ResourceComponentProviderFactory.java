/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.spi.component;

import com.sun.jersey.core.spi.component.ComponentProviderFactory;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.server.spi.component.ResourceComponentProvider;

public interface ResourceComponentProviderFactory
extends ComponentProviderFactory<ResourceComponentProvider> {
    public ComponentScope getScope(Class var1);

    public ResourceComponentProvider getComponentProvider(IoCComponentProvider var1, Class<?> var2);
}

