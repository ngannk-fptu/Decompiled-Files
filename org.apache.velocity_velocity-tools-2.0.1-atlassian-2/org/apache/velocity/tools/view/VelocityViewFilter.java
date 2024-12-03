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
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.velocity.context.Context
 */
package org.apache.velocity.tools.view;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.ServletUtils;
import org.apache.velocity.tools.view.VelocityView;

public class VelocityViewFilter
implements Filter {
    public static final String CONTEXT_KEY = "org.apache.velocity.tools.context.key";
    private VelocityView view;
    private FilterConfig config;
    private String contextKey = null;

    public void init(FilterConfig config) throws ServletException {
        this.config = config;
        this.getVelocityView();
        this.contextKey = this.findInitParameter(CONTEXT_KEY);
    }

    protected FilterConfig getFilterConfig() {
        return this.config;
    }

    protected VelocityView getVelocityView() {
        if (this.view == null) {
            this.view = ServletUtils.getVelocityView(this.getFilterConfig());
            assert (this.view != null);
        }
        return this.view;
    }

    protected String getContextKey() {
        return this.contextKey;
    }

    protected String findInitParameter(String key) {
        FilterConfig conf = this.getFilterConfig();
        String param = conf.getInitParameter(key);
        if (param == null || param.length() == 0) {
            param = conf.getServletContext().getInitParameter(key);
        }
        return param;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (this.contextKey != null && request instanceof HttpServletRequest) {
            Context context = this.createContext((HttpServletRequest)request, (HttpServletResponse)response);
            request.setAttribute(this.contextKey, (Object)context);
        } else {
            this.getVelocityView().publishToolboxes(request);
        }
        chain.doFilter(request, response);
    }

    protected Context createContext(HttpServletRequest request, HttpServletResponse response) {
        return this.getVelocityView().createContext(request, response);
    }

    public void destroy() {
        this.view = null;
        this.config = null;
        this.contextKey = null;
    }
}

