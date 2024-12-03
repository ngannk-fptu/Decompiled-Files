/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.filter.GenericFilterBean;

public abstract class OncePerRequestFilter
extends GenericFilterBean {
    public static final String ALREADY_FILTERED_SUFFIX = ".FILTERED";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean hasAlreadyFilteredAttribute;
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("OncePerRequestFilter just supports HTTP requests");
        }
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        String alreadyFilteredAttributeName = this.getAlreadyFilteredAttributeName();
        boolean bl = hasAlreadyFilteredAttribute = request.getAttribute(alreadyFilteredAttributeName) != null;
        if (hasAlreadyFilteredAttribute || this.skipDispatch(httpRequest) || this.shouldNotFilter(httpRequest)) {
            filterChain.doFilter(request, response);
        } else {
            request.setAttribute(alreadyFilteredAttributeName, (Object)Boolean.TRUE);
            try {
                this.doFilterInternal(httpRequest, httpResponse, filterChain);
            }
            finally {
                request.removeAttribute(alreadyFilteredAttributeName);
            }
        }
    }

    private boolean skipDispatch(HttpServletRequest request) {
        if (this.isAsyncDispatch(request) && this.shouldNotFilterAsyncDispatch()) {
            return true;
        }
        return request.getAttribute("javax.servlet.error.request_uri") != null && this.shouldNotFilterErrorDispatch();
    }

    protected boolean isAsyncDispatch(HttpServletRequest request) {
        return WebAsyncUtils.getAsyncManager((ServletRequest)request).hasConcurrentResult();
    }

    protected boolean isAsyncStarted(HttpServletRequest request) {
        return WebAsyncUtils.getAsyncManager((ServletRequest)request).isConcurrentHandlingStarted();
    }

    protected String getAlreadyFilteredAttributeName() {
        String name = this.getFilterName();
        if (name == null) {
            name = this.getClass().getName();
        }
        return name + ALREADY_FILTERED_SUFFIX;
    }

    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return false;
    }

    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }

    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }

    protected abstract void doFilterInternal(HttpServletRequest var1, HttpServletResponse var2, FilterChain var3) throws ServletException, IOException;
}

