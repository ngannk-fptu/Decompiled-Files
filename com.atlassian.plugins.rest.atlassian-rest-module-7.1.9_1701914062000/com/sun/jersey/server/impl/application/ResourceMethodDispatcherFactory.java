/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.application;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.spi.container.ResourceMethodDispatchAdapter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import com.sun.jersey.spi.inject.Errors;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ResourceMethodDispatcherFactory
implements ResourceMethodDispatchProvider {
    private static final Logger LOGGER = Logger.getLogger(ResourceMethodDispatcherFactory.class.getName());
    private final Set<ResourceMethodDispatchProvider> dispatchers;

    private ResourceMethodDispatcherFactory(ProviderServices providerServices) {
        this.dispatchers = providerServices.getProvidersAndServices(ResourceMethodDispatchProvider.class);
    }

    public static ResourceMethodDispatchProvider create(ProviderServices providerServices) {
        ResourceMethodDispatchProvider p = new ResourceMethodDispatcherFactory(providerServices);
        for (ResourceMethodDispatchAdapter a : providerServices.getProvidersAndServices(ResourceMethodDispatchAdapter.class)) {
            p = a.adapt(p);
        }
        return p;
    }

    @Override
    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod) {
        Errors.mark();
        for (ResourceMethodDispatchProvider rmdp : this.dispatchers) {
            try {
                RequestDispatcher d = rmdp.create(abstractResourceMethod);
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

