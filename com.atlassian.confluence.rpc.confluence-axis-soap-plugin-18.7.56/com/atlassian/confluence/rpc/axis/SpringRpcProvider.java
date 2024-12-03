/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.rpc.SoapModuleDescriptor
 *  com.atlassian.confluence.rpc.auth.TokenAuthenticationInvocationHandler
 */
package com.atlassian.confluence.rpc.axis;

import com.atlassian.confluence.plugin.descriptor.rpc.SoapModuleDescriptor;
import com.atlassian.confluence.rpc.auth.TokenAuthenticationInvocationHandler;
import org.apache.axis.MessageContext;
import org.apache.axis.providers.java.RPCProvider;

public class SpringRpcProvider
extends RPCProvider {
    private Object service;

    public SpringRpcProvider(SoapModuleDescriptor descriptor) throws ClassNotFoundException {
        Object service = descriptor.getModule();
        if (descriptor.isAuthenticated()) {
            service = TokenAuthenticationInvocationHandler.makeAuthenticatingProxy((Object)service, (Class)descriptor.getPublishedInterface());
        }
        this.service = service;
    }

    @Override
    protected Object makeNewServiceObject(MessageContext msgContext, String clsName) throws Exception {
        return this.service;
    }
}

