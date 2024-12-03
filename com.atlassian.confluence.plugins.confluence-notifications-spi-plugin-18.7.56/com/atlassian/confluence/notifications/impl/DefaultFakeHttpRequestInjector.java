/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.core.filters.ServletContextThreadLocalFilter
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.google.common.base.Throwables
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.notifications.impl.FakeHttpRequestInjector;
import com.atlassian.confluence.notifications.impl.spi.DeclarativeRenderContextFactory;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.core.filters.ServletContextThreadLocalFilter;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.springframework.mock.web.MockHttpServletRequest;
import com.atlassian.springframework.mock.web.MockHttpServletResponse;
import com.google.common.base.Throwables;
import java.util.concurrent.Callable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultFakeHttpRequestInjector
implements FakeHttpRequestInjector {
    private final ServletContextThreadLocalFilter SERVLET_CONTEXT_THREAD_LOCAL_FILTER = new ServletContextThreadLocalFilter();
    private final ApplicationProperties applicationProperties;

    public DefaultFakeHttpRequestInjector(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public <T> T withRequest(Callable<T> callback) {
        try {
            if (ServletContextThreadLocal.getRequest() == null || ServletContextThreadLocal.getResponse() == null) {
                HttpServletRequest mockRequest = this.buildRequest();
                MockHttpServletResponse mockResponse = new MockHttpServletResponse();
                this.SERVLET_CONTEXT_THREAD_LOCAL_FILTER.doFilter(mockRequest, (HttpServletResponse)mockResponse, (request, response) -> {
                    try {
                        request.setAttribute(DeclarativeRenderContextFactory.class.getName(), callback.call());
                    }
                    catch (Exception e) {
                        throw Throwables.propagate((Throwable)e);
                    }
                });
                return (T)mockRequest.getAttribute(DeclarativeRenderContextFactory.class.getName());
            }
            return callback.call();
        }
        catch (Exception e) {
            throw Throwables.propagate((Throwable)e);
        }
    }

    private HttpServletRequest buildRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath(this.applicationProperties.getBaseUrl(UrlMode.RELATIVE));
        return request;
    }
}

