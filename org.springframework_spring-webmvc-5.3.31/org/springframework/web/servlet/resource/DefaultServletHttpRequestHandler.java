/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 *  org.springframework.web.HttpRequestHandler
 *  org.springframework.web.context.ServletContextAware
 */
package org.springframework.web.servlet.resource;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.ServletContextAware;

public class DefaultServletHttpRequestHandler
implements HttpRequestHandler,
ServletContextAware {
    private static final String COMMON_DEFAULT_SERVLET_NAME = "default";
    private static final String GAE_DEFAULT_SERVLET_NAME = "_ah_default";
    private static final String RESIN_DEFAULT_SERVLET_NAME = "resin-file";
    private static final String WEBLOGIC_DEFAULT_SERVLET_NAME = "FileServlet";
    private static final String WEBSPHERE_DEFAULT_SERVLET_NAME = "SimpleFileServlet";
    @Nullable
    private String defaultServletName;
    @Nullable
    private ServletContext servletContext;

    public void setDefaultServletName(String defaultServletName) {
        this.defaultServletName = defaultServletName;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        if (!StringUtils.hasText((String)this.defaultServletName)) {
            if (this.servletContext.getNamedDispatcher(COMMON_DEFAULT_SERVLET_NAME) != null) {
                this.defaultServletName = COMMON_DEFAULT_SERVLET_NAME;
            } else if (this.servletContext.getNamedDispatcher(GAE_DEFAULT_SERVLET_NAME) != null) {
                this.defaultServletName = GAE_DEFAULT_SERVLET_NAME;
            } else if (this.servletContext.getNamedDispatcher(RESIN_DEFAULT_SERVLET_NAME) != null) {
                this.defaultServletName = RESIN_DEFAULT_SERVLET_NAME;
            } else if (this.servletContext.getNamedDispatcher(WEBLOGIC_DEFAULT_SERVLET_NAME) != null) {
                this.defaultServletName = WEBLOGIC_DEFAULT_SERVLET_NAME;
            } else if (this.servletContext.getNamedDispatcher(WEBSPHERE_DEFAULT_SERVLET_NAME) != null) {
                this.defaultServletName = WEBSPHERE_DEFAULT_SERVLET_NAME;
            } else {
                throw new IllegalStateException("Unable to locate the default servlet for serving static content. Please set the 'defaultServletName' property explicitly.");
            }
        }
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Assert.state((this.servletContext != null ? 1 : 0) != 0, (String)"No ServletContext set");
        RequestDispatcher rd = this.servletContext.getNamedDispatcher(this.defaultServletName);
        if (rd == null) {
            throw new IllegalStateException("A RequestDispatcher could not be located for the default servlet '" + this.defaultServletName + "'");
        }
        rd.forward((ServletRequest)request, (ServletResponse)response);
    }
}

