/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.provider;

import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.core.ApplicationAdapter;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.factory.AbstractRuntimeDelegate;
import javax.ws.rs.core.Application;

public class RuntimeDelegateImpl
extends AbstractRuntimeDelegate {
    @Override
    public <T> T createEndpoint(Application application, Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException {
        if (application instanceof ResourceConfig) {
            return ContainerFactory.createContainer(endpointType, (ResourceConfig)application);
        }
        return ContainerFactory.createContainer(endpointType, new ApplicationAdapter(application));
    }
}

