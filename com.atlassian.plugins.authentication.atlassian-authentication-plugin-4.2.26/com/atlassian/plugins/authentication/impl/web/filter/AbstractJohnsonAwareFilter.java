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
package com.atlassian.plugins.authentication.impl.web.filter;

import com.atlassian.plugins.authentication.impl.johnson.JohnsonChecker;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public abstract class AbstractJohnsonAwareFilter
implements Filter {
    private JohnsonChecker johnsonChecker;

    public AbstractJohnsonAwareFilter(JohnsonChecker johnsonChecker) {
        this.johnsonChecker = johnsonChecker;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (this.johnsonChecker.isInstanceJohnsoned(request.getServletContext())) {
            chain.doFilter(request, response);
        } else {
            this.doFilterInternal(request, response, chain);
        }
    }

    protected abstract void doFilterInternal(ServletRequest var1, ServletResponse var2, FilterChain var3) throws IOException, ServletException;

    public void destroy() {
    }
}

