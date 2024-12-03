/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.Provider
 */
package com.sun.xml.ws.server.provider;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.Invoker;
import com.sun.xml.ws.api.server.ProviderInvokerTubeFactory;
import com.sun.xml.ws.binding.SOAPBindingImpl;
import com.sun.xml.ws.server.InvokerTube;
import com.sun.xml.ws.server.provider.ProviderArgumentsBuilder;
import com.sun.xml.ws.server.provider.ProviderEndpointModel;
import javax.xml.ws.Provider;

public abstract class ProviderInvokerTube<T>
extends InvokerTube<Provider<T>> {
    protected ProviderArgumentsBuilder<T> argsBuilder;

    ProviderInvokerTube(Invoker invoker, ProviderArgumentsBuilder<T> argsBuilder) {
        super(invoker);
        this.argsBuilder = argsBuilder;
    }

    public static <T> ProviderInvokerTube<T> create(Class<T> implType, WSBinding binding, Invoker invoker, Container container) {
        ProviderEndpointModel<T> model = new ProviderEndpointModel<T>(implType, binding);
        ProviderArgumentsBuilder<?> argsBuilder = ProviderArgumentsBuilder.create(model, binding);
        if (binding instanceof SOAPBindingImpl) {
            ((SOAPBindingImpl)binding).setMode(model.mode);
        }
        return ProviderInvokerTubeFactory.create(null, container, implType, invoker, argsBuilder, model.isAsync);
    }
}

