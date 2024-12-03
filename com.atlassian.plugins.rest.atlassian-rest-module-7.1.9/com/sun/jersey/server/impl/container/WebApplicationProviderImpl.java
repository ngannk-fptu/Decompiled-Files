/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.container;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.server.impl.application.WebApplicationImpl;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.WebApplicationProvider;

public class WebApplicationProviderImpl
implements WebApplicationProvider {
    @Override
    public WebApplication createWebApplication() throws ContainerException {
        return new WebApplicationImpl();
    }
}

