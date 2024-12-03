/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.Provider
 *  javax.xml.ws.WebServiceContext
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.AsyncProvider;
import com.sun.xml.ws.api.server.AsyncProviderCallback;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WSWebServiceContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceContext;

public abstract class Invoker
extends com.sun.xml.ws.server.sei.Invoker {
    private static final Method invokeMethod;
    private static final Method asyncInvokeMethod;

    public void start(@NotNull WSWebServiceContext wsc, @NotNull WSEndpoint endpoint) {
        this.start(wsc);
    }

    public void start(@NotNull WebServiceContext wsc) {
        throw new IllegalStateException("deprecated version called");
    }

    public void dispose() {
    }

    public <T> T invokeProvider(@NotNull Packet p, T arg) throws IllegalAccessException, InvocationTargetException {
        return (T)this.invoke(p, invokeMethod, arg);
    }

    public <T> void invokeAsyncProvider(@NotNull Packet p, T arg, AsyncProviderCallback cbak, WebServiceContext ctxt) throws IllegalAccessException, InvocationTargetException {
        this.invoke(p, asyncInvokeMethod, arg, cbak, ctxt);
    }

    static {
        try {
            invokeMethod = Provider.class.getMethod("invoke", Object.class);
        }
        catch (NoSuchMethodException e) {
            throw new AssertionError((Object)e);
        }
        try {
            asyncInvokeMethod = AsyncProvider.class.getMethod("invoke", Object.class, AsyncProviderCallback.class, WebServiceContext.class);
        }
        catch (NoSuchMethodException e) {
            throw new AssertionError((Object)e);
        }
    }
}

