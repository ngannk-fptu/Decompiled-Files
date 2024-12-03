/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.sun.xml.ws.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.AbstractInstanceResolver;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WSWebServiceContext;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public final class SingletonResolver<T>
extends AbstractInstanceResolver<T> {
    @NotNull
    private final T singleton;

    public SingletonResolver(@NotNull T singleton) {
        this.singleton = singleton;
    }

    @Override
    @NotNull
    public T resolve(Packet request) {
        return this.singleton;
    }

    @Override
    public void start(WSWebServiceContext wsc, WSEndpoint endpoint) {
        SingletonResolver.getResourceInjector(endpoint).inject(wsc, this.singleton);
        SingletonResolver.invokeMethod(this.findAnnotatedMethod(this.singleton.getClass(), PostConstruct.class), this.singleton, new Object[0]);
    }

    @Override
    public void dispose() {
        SingletonResolver.invokeMethod(this.findAnnotatedMethod(this.singleton.getClass(), PreDestroy.class), this.singleton, new Object[0]);
    }
}

