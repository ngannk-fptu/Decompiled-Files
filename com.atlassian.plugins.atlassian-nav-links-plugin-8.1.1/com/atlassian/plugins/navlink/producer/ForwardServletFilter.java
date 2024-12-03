/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.navlink.producer;

import com.google.common.base.Strings;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForwardServletFilter
implements Filter {
    static final String TARGET_INIT_PARAMETER = "target";
    private final Logger logger = LoggerFactory.getLogger(ForwardServletFilter.class);
    private String target;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.target = Strings.nullToEmpty((String)filterConfig.getInitParameter(TARGET_INIT_PARAMETER));
        if (this.target.isEmpty()) {
            this.logger.warn("No target url was configured for servlet filter with name '{}'; ignoring incoming requests.", (Object)filterConfig.getFilterName());
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!this.target.isEmpty()) {
            this.redirectToTarget(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    private void redirectToTarget(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("redirecting request to {}", (Object)this.target);
        }
        request.getRequestDispatcher(this.target).forward(request, response);
    }

    public void destroy() {
    }
}

