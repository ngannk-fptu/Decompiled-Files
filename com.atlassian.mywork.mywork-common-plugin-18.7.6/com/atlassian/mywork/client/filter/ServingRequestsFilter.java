/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 */
package com.atlassian.mywork.client.filter;

import com.atlassian.mywork.client.service.ServingRequestsAware;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ServingRequestsFilter
implements Filter {
    private final AtomicBoolean servingRequests = new AtomicBoolean(false);
    private final ServingRequestsAware servingRequestsAware;

    public ServingRequestsFilter(ServingRequestsAware servingRequestsAware) {
        this.servingRequestsAware = servingRequestsAware;
    }

    public void init(FilterConfig filterConfig) {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (this.servingRequests.compareAndSet(false, true)) {
            this.servingRequestsAware.onServingRequests();
        }
        chain.doFilter(request, response);
    }

    public void destroy() {
    }
}

