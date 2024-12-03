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
 */
package com.opensymphony.sitemesh.webapp.decorator;

import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import com.opensymphony.sitemesh.webapp.decorator.BaseWebAppDecorator;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DispatchedDecorator
extends BaseWebAppDecorator {
    public static final String CONTENT_KEY = "com.opensymphony.sitemesh.CONTENT";
    public static final String CONTEXT_KEY = "com.opensymphony.sitemesh.CONTEXT";
    private final String path;

    public DispatchedDecorator(String path) {
        this.path = path;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void render(Content content, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, SiteMeshWebAppContext webAppContext) throws IOException, ServletException {
        Object oldContent = request.getAttribute(CONTENT_KEY);
        Object oldWebAppContext = request.getAttribute(CONTEXT_KEY);
        request.setAttribute(CONTENT_KEY, (Object)content);
        request.setAttribute(CONTEXT_KEY, (Object)webAppContext);
        try {
            RequestDispatcher dispatcher = servletContext.getRequestDispatcher(this.path);
            dispatcher.include((ServletRequest)request, (ServletResponse)response);
        }
        finally {
            request.setAttribute(CONTENT_KEY, oldContent);
            request.setAttribute(CONTEXT_KEY, oldWebAppContext);
        }
    }

    protected ServletContext locateWebApp(ServletContext context) {
        return context;
    }
}

