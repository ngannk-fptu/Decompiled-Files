/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.core.ResourceContext;
import com.sun.jersey.api.core.Traceable;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderFactory;
import com.sun.jersey.spi.MessageBodyWorkers;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import com.sun.jersey.spi.container.ExceptionMapperContext;
import com.sun.jersey.spi.monitoring.DispatchingListener;
import com.sun.jersey.spi.monitoring.RequestListener;
import com.sun.jersey.spi.monitoring.ResponseListener;
import java.io.IOException;
import javax.ws.rs.ext.Providers;

public interface WebApplication
extends Traceable {
    public boolean isInitiated();

    public void initiate(ResourceConfig var1) throws IllegalArgumentException, ContainerException;

    public void initiate(ResourceConfig var1, IoCComponentProviderFactory var2) throws IllegalArgumentException, ContainerException;

    public WebApplication clone();

    public FeaturesAndProperties getFeaturesAndProperties();

    public Providers getProviders();

    public ResourceContext getResourceContext();

    public MessageBodyWorkers getMessageBodyWorkers();

    public ExceptionMapperContext getExceptionMapperContext();

    public HttpContext getThreadLocalHttpContext();

    public ServerInjectableProviderFactory getServerInjectableProviderFactory();

    public RequestListener getRequestListener();

    public DispatchingListener getDispatchingListener();

    public ResponseListener getResponseListener();

    public void handleRequest(ContainerRequest var1, ContainerResponseWriter var2) throws IOException;

    public void handleRequest(ContainerRequest var1, ContainerResponse var2) throws IOException;

    public void destroy();
}

