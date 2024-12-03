/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.server.impl.application.ResourceMethodDispatcherFactory;
import com.sun.jersey.spi.container.JavaMethodInvoker;
import com.sun.jersey.spi.container.ResourceMethodCustomInvokerDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import com.sun.jersey.spi.inject.Errors;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceMethodCustomInvokerDispatchFactory {
    private static final Logger LOGGER = Logger.getLogger(ResourceMethodDispatcherFactory.class.getName());
    final Set<ResourceMethodCustomInvokerDispatchProvider> customInvokerDispatchProviders;

    public ResourceMethodCustomInvokerDispatchFactory(ProviderServices providerServices) {
        this.customInvokerDispatchProviders = providerServices.getProvidersAndServices(ResourceMethodCustomInvokerDispatchProvider.class);
    }

    public RequestDispatcher getDispatcher(AbstractResourceMethod abstractResourceMethod, JavaMethodInvoker invoker) {
        if (invoker == null) {
            return null;
        }
        Errors.mark();
        for (ResourceMethodCustomInvokerDispatchProvider rmdp : this.customInvokerDispatchProviders) {
            try {
                RequestDispatcher d = rmdp.create(abstractResourceMethod, invoker);
                if (d == null) continue;
                Errors.reset();
                return d;
            }
            catch (Exception e) {
                LOGGER.log(Level.SEVERE, ImplMessages.ERROR_PROCESSING_METHOD(abstractResourceMethod.getMethod(), rmdp.getClass().getName()), e);
            }
        }
        Errors.unmark();
        return null;
    }
}

