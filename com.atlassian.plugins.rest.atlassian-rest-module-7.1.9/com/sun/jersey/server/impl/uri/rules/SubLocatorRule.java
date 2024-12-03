/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri.rules;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.api.model.AbstractSubResourceLocator;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.server.impl.uri.rules.BaseRule;
import com.sun.jersey.server.probes.UriRuleProbeProvider;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.SubjectSecurityContext;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.monitoring.DispatchingListener;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.SecurityContext;

public final class SubLocatorRule
extends BaseRule {
    private final List<AbstractHttpContextInjectable> is;
    private final Method m;
    private final List<ContainerRequestFilter> requestFilters;
    private final List<ContainerResponseFilter> responseFilters;
    private final DispatchingListener dispatchingListener;
    private final AbstractSubResourceLocator locator;

    public SubLocatorRule(UriTemplate template, List<Injectable> is, List<ContainerRequestFilter> requestFilters, List<ContainerResponseFilter> responseFilters, DispatchingListener dispatchingListener, AbstractSubResourceLocator locator) {
        super(template);
        this.is = AbstractHttpContextInjectable.transform(is);
        this.requestFilters = requestFilters;
        this.responseFilters = responseFilters;
        this.dispatchingListener = dispatchingListener;
        this.locator = locator;
        this.m = locator.getMethod();
    }

    @Override
    public boolean accept(CharSequence path, Object resource, UriRuleContext context) {
        UriRuleProbeProvider.ruleAccept(SubLocatorRule.class.getSimpleName(), path, resource);
        this.pushMatch(context);
        Object subResource = this.invokeSubLocator(resource, context);
        if (subResource == null) {
            if (context.isTracingEnabled()) {
                this.trace(resource, subResource, context);
            }
            return false;
        }
        if (subResource instanceof Class) {
            subResource = context.getResource((Class)subResource);
        }
        this.dispatchingListener.onSubResource(Thread.currentThread().getId(), subResource.getClass());
        context.pushResource(subResource);
        if (context.isTracingEnabled()) {
            this.trace(resource, subResource, context);
        }
        Iterator<UriRule> matches = context.getRules(subResource.getClass()).match(path, context);
        while (matches.hasNext()) {
            if (!matches.next().accept(path, subResource, context)) continue;
            return true;
        }
        return false;
    }

    private void trace(Object resource, Object subResource, UriRuleContext context) {
        String prevPath = context.getUriInfo().getMatchedURIs().get(1);
        String currentPath = context.getUriInfo().getMatchedURIs().get(0);
        context.trace(String.format("accept sub-resource locator: \"%s\" : \"%s\" -> @Path(\"%s\") %s = %s", prevPath, currentPath.substring(prevPath.length()), this.getTemplate().getTemplate(), ReflectionHelper.methodInstanceToString(resource, this.m), subResource));
    }

    private Object invokeSubLocator(final Object resource, final UriRuleContext context) {
        context.pushContainerResponseFilters(this.responseFilters);
        ContainerRequest containerRequest = context.getContainerRequest();
        if (!this.requestFilters.isEmpty()) {
            for (ContainerRequestFilter f : this.requestFilters) {
                containerRequest = f.filter(containerRequest);
                context.setContainerRequest(containerRequest);
            }
        }
        this.dispatchingListener.onSubResourceLocator(Thread.currentThread().getId(), this.locator);
        SecurityContext sc = containerRequest.getSecurityContext();
        if (sc instanceof SubjectSecurityContext) {
            return ((SubjectSecurityContext)sc).doAsSubject(new PrivilegedAction(){

                public Object run() {
                    return SubLocatorRule.this.dispatch(resource, context);
                }
            });
        }
        return this.dispatch(resource, context);
    }

    private Object dispatch(Object resource, UriRuleContext context) {
        try {
            if (this.is.isEmpty()) {
                return this.m.invoke(resource, new Object[0]);
            }
            Object[] params = new Object[this.is.size()];
            int index = 0;
            for (AbstractHttpContextInjectable i : this.is) {
                params[index++] = i.getValue(context);
            }
            return this.m.invoke(resource, params);
        }
        catch (InvocationTargetException e) {
            throw new MappableContainerException(e.getTargetException());
        }
        catch (IllegalAccessException e) {
            throw new ContainerException(e);
        }
        catch (WebApplicationException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw new ContainerException("Exception injecting parameters for sub-locator method: " + this.m, e);
        }
    }
}

