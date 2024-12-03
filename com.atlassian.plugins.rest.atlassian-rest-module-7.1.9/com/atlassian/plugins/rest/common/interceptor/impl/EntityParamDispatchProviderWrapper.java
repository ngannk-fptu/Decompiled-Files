/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.interceptor.impl;

import com.atlassian.plugins.rest.common.interceptor.impl.DispatchProviderHelper;
import com.atlassian.plugins.rest.common.interceptor.impl.InterceptorChainBuilder;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.server.impl.model.method.dispatch.EntityParamDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import javax.ws.rs.core.Context;

public class EntityParamDispatchProviderWrapper
extends EntityParamDispatchProvider {
    @Context
    private InterceptorChainBuilder interceptorChainBuilder;

    @Override
    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod) {
        DispatchProviderHelper helper = new DispatchProviderHelper(this.interceptorChainBuilder);
        return helper.create(abstractResourceMethod, this.getInjectableValuesProvider(abstractResourceMethod));
    }
}

