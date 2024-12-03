/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand.interceptor;

import com.atlassian.plugins.rest.common.expand.AdditionalExpandsProvider;
import com.atlassian.plugins.rest.common.expand.EntityCrawler;
import com.atlassian.plugins.rest.common.expand.parameter.DefaultExpandParameter;
import com.atlassian.plugins.rest.common.expand.resolver.EntityExpanderResolver;
import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.HttpResponseContext;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;

public class ExpandInterceptor
implements ResourceInterceptor {
    private final EntityExpanderResolver expanderResolver;
    private final String expandParameterName;
    private final Collection<? extends AdditionalExpandsProvider> additionalExpandsProviders;

    public ExpandInterceptor(EntityExpanderResolver expanderResolver, Collection<? extends AdditionalExpandsProvider> additionalExpandsProviders) {
        this("expand", expanderResolver, additionalExpandsProviders);
    }

    public ExpandInterceptor(String expandParameterName, EntityExpanderResolver expanderResolver, Collection<? extends AdditionalExpandsProvider> additionalExpandsProviders) {
        this.expanderResolver = expanderResolver;
        this.expandParameterName = Objects.requireNonNull(expandParameterName);
        this.additionalExpandsProviders = additionalExpandsProviders;
    }

    @Override
    public void intercept(MethodInvocation invocation) throws IllegalAccessException, InvocationTargetException {
        invocation.invoke();
        HttpRequestContext request = invocation.getHttpContext().getRequest();
        HttpResponseContext response = invocation.getHttpContext().getResponse();
        DefaultExpandParameter expandParameter = new DefaultExpandParameter((Collection)request.getQueryParameters().get(this.expandParameterName));
        new EntityCrawler(this.additionalExpandsProviders).crawl(response.getEntity(), expandParameter, this.expanderResolver);
    }
}

