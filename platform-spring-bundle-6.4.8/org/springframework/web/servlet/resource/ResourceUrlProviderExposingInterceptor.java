/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

public class ResourceUrlProviderExposingInterceptor
implements HandlerInterceptor {
    public static final String RESOURCE_URL_PROVIDER_ATTR = ResourceUrlProvider.class.getName();
    private final ResourceUrlProvider resourceUrlProvider;

    public ResourceUrlProviderExposingInterceptor(ResourceUrlProvider resourceUrlProvider) {
        Assert.notNull((Object)resourceUrlProvider, "ResourceUrlProvider is required");
        this.resourceUrlProvider = resourceUrlProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            request.setAttribute(RESOURCE_URL_PROVIDER_ATTR, (Object)this.resourceUrlProvider);
        }
        catch (ResourceUrlEncodingFilter.LookupPathIndexException ex) {
            throw new ServletRequestBindingException(ex.getMessage(), ex);
        }
        return true;
    }
}

