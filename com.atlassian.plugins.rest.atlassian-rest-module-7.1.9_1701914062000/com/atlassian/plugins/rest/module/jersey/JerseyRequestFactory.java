/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.sal.api.net.MarshallingRequestFactory
 *  com.atlassian.sal.api.net.NonMarshallingRequestFactory
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  io.atlassian.util.concurrent.LazyReference
 *  org.osgi.framework.Bundle
 */
package com.atlassian.plugins.rest.module.jersey;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugins.rest.module.ChainingClassLoader;
import com.atlassian.plugins.rest.module.ContextClassLoaderSwitchingProxy;
import com.atlassian.plugins.rest.module.jersey.JerseyEntityHandler;
import com.atlassian.plugins.rest.module.jersey.JerseyRequest;
import com.atlassian.sal.api.net.MarshallingRequestFactory;
import com.atlassian.sal.api.net.NonMarshallingRequestFactory;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import io.atlassian.util.concurrent.LazyReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.osgi.framework.Bundle;

public class JerseyRequestFactory
implements MarshallingRequestFactory {
    private final RequestFactory<? extends Request> delegateRequestFactory;
    private final Plugin plugin;
    private final Bundle bundle;
    private LazyReference<JerseyEntityHandler> jerseyEntityHandlerReference = new LazyReference<JerseyEntityHandler>(){

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected JerseyEntityHandler create() {
            ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
            ChainingClassLoader chainingClassLoader = JerseyRequestFactory.this.getChainingClassLoader(JerseyRequestFactory.this.plugin);
            try {
                Thread.currentThread().setContextClassLoader(chainingClassLoader);
                JerseyEntityHandler jerseyEntityHandler = new JerseyEntityHandler((ContainerManagedPlugin)JerseyRequestFactory.this.plugin, JerseyRequestFactory.this.bundle);
                return jerseyEntityHandler;
            }
            finally {
                Thread.currentThread().setContextClassLoader(oldClassLoader);
            }
        }
    };

    public JerseyRequestFactory(NonMarshallingRequestFactory<? extends Request> delegateRequestFactory, Plugin plugin, Bundle bundle2) {
        this.plugin = plugin;
        this.delegateRequestFactory = delegateRequestFactory;
        this.bundle = bundle2;
    }

    public Request createRequest(Request.MethodType methodType, String s) {
        Request delegateRequest = this.delegateRequestFactory.createRequest(methodType, s);
        JerseyRequest request = new JerseyRequest(delegateRequest, (JerseyEntityHandler)this.jerseyEntityHandlerReference.get(), this.plugin);
        return (Request)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Request.class}, (InvocationHandler)new ContextClassLoaderSwitchingProxy(request, this.getChainingClassLoader(this.plugin)));
    }

    public boolean supportsHeader() {
        return this.delegateRequestFactory.supportsHeader();
    }

    void destroy() {
        if (this.jerseyEntityHandlerReference.isInitialized()) {
            ((JerseyEntityHandler)this.jerseyEntityHandlerReference.get()).destroy();
        }
    }

    private ChainingClassLoader getChainingClassLoader(Plugin plugin) {
        return new ChainingClassLoader(this.getClass().getClassLoader(), plugin.getClassLoader());
    }
}

