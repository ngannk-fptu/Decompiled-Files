/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.method;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.server.impl.container.filter.FilterFactory;
import com.sun.jersey.server.impl.model.method.ResourceMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.inject.Errors;
import java.lang.reflect.Method;
import java.util.List;

public final class ResourceHttpMethod
extends ResourceMethod {
    private final AbstractResourceMethod arm;

    public ResourceHttpMethod(ResourceMethodDispatchProvider dp, FilterFactory ff, AbstractResourceMethod arm) {
        this(dp, ff, UriTemplate.EMPTY, arm);
    }

    public ResourceHttpMethod(ResourceMethodDispatchProvider dp, FilterFactory ff, UriTemplate template, AbstractResourceMethod arm) {
        this(dp, ff, ff.getResourceFilters(arm), template, arm);
    }

    public ResourceHttpMethod(ResourceMethodDispatchProvider dp, FilterFactory ff, List<ResourceFilter> resourceFilters, UriTemplate template, AbstractResourceMethod arm) {
        super(arm.getHttpMethod(), template, arm.getSupportedInputTypes(), arm.getSupportedOutputTypes(), arm.areOutputTypesDeclared(), dp.create(arm), FilterFactory.getRequestFilters(resourceFilters), FilterFactory.getResponseFilters(resourceFilters));
        this.arm = arm;
        if (this.getDispatcher() == null) {
            Method m = arm.getMethod();
            String msg = ImplMessages.NOT_VALID_HTTPMETHOD(m, arm.getHttpMethod(), m.getDeclaringClass());
            Errors.error(msg);
        }
    }

    @Override
    public AbstractResourceMethod getAbstractResourceMethod() {
        return this.arm;
    }

    public String toString() {
        Method m = this.arm.getMethod();
        return ImplMessages.RESOURCE_METHOD(m.getDeclaringClass(), m.getName());
    }
}

