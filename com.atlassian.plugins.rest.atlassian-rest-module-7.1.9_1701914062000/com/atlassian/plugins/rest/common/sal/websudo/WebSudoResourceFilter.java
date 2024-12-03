/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 */
package com.atlassian.plugins.rest.common.sal.websudo;

import com.atlassian.plugins.rest.common.sal.websudo.WebSudoRequiredException;
import com.atlassian.plugins.rest.common.sal.websudo.WebSudoResourceContext;
import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import java.lang.reflect.Method;
import java.util.Objects;

final class WebSudoResourceFilter
implements ResourceFilter,
ContainerRequestFilter {
    private final AbstractMethod abstractMethod;
    private final WebSudoResourceContext webSudoResourceContext;

    public WebSudoResourceFilter(AbstractMethod abstractMethod, WebSudoResourceContext webSudoResourceContext) {
        this.abstractMethod = Objects.requireNonNull(abstractMethod, "abstractMethod can't be null");
        this.webSudoResourceContext = Objects.requireNonNull(webSudoResourceContext, "webSudoResourceContext can't be null");
    }

    @Override
    public ContainerRequestFilter getRequestFilter() {
        return this;
    }

    @Override
    public ContainerResponseFilter getResponseFilter() {
        return null;
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        if (this.requiresWebSudo() && this.webSudoResourceContext.shouldEnforceWebSudoProtection()) {
            throw new WebSudoRequiredException("This resource requires WebSudo.");
        }
        return request;
    }

    private boolean requiresWebSudo() {
        Method m = this.abstractMethod.getMethod();
        if (null != m && m.getAnnotation(WebSudoRequired.class) != null) {
            return true;
        }
        if (null != m && m.getAnnotation(WebSudoNotRequired.class) != null) {
            return false;
        }
        AbstractResource resource = this.abstractMethod.getResource();
        if (resource.isAnnotationPresent(WebSudoRequired.class)) {
            return true;
        }
        if (resource.isAnnotationPresent(WebSudoNotRequired.class)) {
            return false;
        }
        Package p = this.abstractMethod.getResource().getResourceClass().getPackage();
        if (p.getAnnotation(WebSudoRequired.class) != null) {
            return true;
        }
        if (p.getAnnotation(WebSudoNotRequired.class) != null) {
            return false;
        }
        return false;
    }
}

