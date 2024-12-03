/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.WebApplication;

public interface ContainerProvider<T> {
    public T createContainer(Class<T> var1, ResourceConfig var2, WebApplication var3) throws ContainerException;
}

