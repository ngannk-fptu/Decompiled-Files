/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.container.httpserver;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.server.impl.container.httpserver.HttpHandlerContainer;
import com.sun.jersey.spi.container.ContainerProvider;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.net.httpserver.HttpHandler;

public final class HttpHandlerContainerProvider
implements ContainerProvider<HttpHandler> {
    @Override
    public HttpHandler createContainer(Class<HttpHandler> type, ResourceConfig resourceConfig, WebApplication application) throws ContainerException {
        if (type != HttpHandler.class) {
            return null;
        }
        return new HttpHandlerContainer(application);
    }
}

