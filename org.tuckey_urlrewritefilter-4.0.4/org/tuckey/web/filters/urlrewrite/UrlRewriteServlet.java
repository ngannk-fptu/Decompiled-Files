/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.UnavailableException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

public class UrlRewriteServlet
extends HttpServlet {
    private static final long serialVersionUID = 2186203405866227539L;
    private UrlRewriteFilter urlRewriteFilter = new UrlRewriteFilter();

    public void init(ServletConfig servletConfig) throws ServletException {
        this.urlRewriteFilter.init(new ConfigWrapper(servletConfig));
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!this.urlRewriteFilter.isLoaded()) {
            throw new UnavailableException("not initialised");
        }
        FilterChainWrapper filterChainWrapper = new FilterChainWrapper();
        this.urlRewriteFilter.doFilter((ServletRequest)request, (ServletResponse)response, filterChainWrapper);
    }

    public void destroy() {
        this.urlRewriteFilter.destroy();
    }

    static class ConfigWrapper
    implements FilterConfig {
        private ServletConfig servletConfig;

        public ConfigWrapper(ServletConfig servletConfig) {
            this.servletConfig = servletConfig;
        }

        public String getFilterName() {
            return this.servletConfig.getServletName();
        }

        public ServletContext getServletContext() {
            return this.servletConfig.getServletContext();
        }

        public String getInitParameter(String string) {
            return this.servletConfig.getInitParameter(string);
        }

        public Enumeration getInitParameterNames() {
            return this.servletConfig.getInitParameterNames();
        }
    }

    static class FilterChainWrapper
    implements FilterChain {
        FilterChainWrapper() {
        }

        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
        }
    }
}

