/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.uri.rules;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.uri.rules.UriMatchResultContext;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRules;
import java.util.List;

public interface UriRuleContext
extends HttpContext,
UriMatchResultContext {
    public ContainerRequest getContainerRequest();

    public void setContainerRequest(ContainerRequest var1);

    public ContainerResponse getContainerResponse();

    public void setContainerResponse(ContainerResponse var1);

    public void pushContainerResponseFilters(List<ContainerResponseFilter> var1);

    public Object getResource(Class var1);

    public UriRules<UriRule> getRules(Class var1);

    public void pushMatch(UriTemplate var1, List<String> var2);

    public void pushResource(Object var1);

    public void pushMethod(AbstractResourceMethod var1);

    public void pushRightHandPathLength(int var1);
}

