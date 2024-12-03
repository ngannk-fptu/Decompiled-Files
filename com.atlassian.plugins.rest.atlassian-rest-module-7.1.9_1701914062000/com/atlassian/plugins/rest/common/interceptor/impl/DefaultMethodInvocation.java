/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.interceptor.impl;

import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

class DefaultMethodInvocation
implements MethodInvocation {
    private final List<ResourceInterceptor> interceptors;
    private final Object resource;
    private final HttpContext httpContext;
    private final AbstractResourceMethod method;
    private final Object[] parameters;

    public DefaultMethodInvocation(Object resource, AbstractResourceMethod method, HttpContext httpContext, List<ResourceInterceptor> interceptors, Object[] params) {
        this.resource = resource;
        this.method = method;
        this.httpContext = httpContext;
        this.interceptors = new ArrayList<ResourceInterceptor>(interceptors);
        this.parameters = params;
    }

    @Override
    public Object getResource() {
        return this.resource;
    }

    @Override
    public HttpContext getHttpContext() {
        return this.httpContext;
    }

    @Override
    public AbstractResourceMethod getMethod() {
        return this.method;
    }

    @Override
    public Object[] getParameters() {
        return this.parameters;
    }

    @Override
    public void invoke() throws IllegalAccessException, InvocationTargetException {
        if (this.interceptors.isEmpty()) {
            throw new IllegalStateException("End of interceptor chain");
        }
        ResourceInterceptor interceptor = this.interceptors.remove(0);
        interceptor.intercept(this);
    }
}

