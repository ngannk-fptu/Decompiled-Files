/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.sun.xml.ws.server;

import com.sun.xml.ws.api.server.AbstractInstanceResolver;
import com.sun.xml.ws.api.server.ResourceInjector;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WSWebServiceContext;
import java.lang.reflect.Method;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public abstract class AbstractMultiInstanceResolver<T>
extends AbstractInstanceResolver<T> {
    protected final Class<T> clazz;
    private WSWebServiceContext webServiceContext;
    protected WSEndpoint owner;
    private final Method postConstructMethod;
    private final Method preDestroyMethod;
    private ResourceInjector resourceInjector;

    public AbstractMultiInstanceResolver(Class<T> clazz) {
        this.clazz = clazz;
        this.postConstructMethod = this.findAnnotatedMethod(clazz, PostConstruct.class);
        this.preDestroyMethod = this.findAnnotatedMethod(clazz, PreDestroy.class);
    }

    protected final void prepare(T t) {
        assert (this.webServiceContext != null);
        this.resourceInjector.inject(this.webServiceContext, t);
        AbstractMultiInstanceResolver.invokeMethod(this.postConstructMethod, t, new Object[0]);
    }

    protected final T create() {
        T t = AbstractMultiInstanceResolver.createNewInstance(this.clazz);
        this.prepare(t);
        return t;
    }

    @Override
    public void start(WSWebServiceContext wsc, WSEndpoint endpoint) {
        this.resourceInjector = AbstractMultiInstanceResolver.getResourceInjector(endpoint);
        this.webServiceContext = wsc;
        this.owner = endpoint;
    }

    protected final void dispose(T instance) {
        AbstractMultiInstanceResolver.invokeMethod(this.preDestroyMethod, instance, new Object[0]);
    }
}

