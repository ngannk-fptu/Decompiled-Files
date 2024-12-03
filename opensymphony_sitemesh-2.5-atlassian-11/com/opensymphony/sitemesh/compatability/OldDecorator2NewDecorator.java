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
package com.opensymphony.sitemesh.compatability;

import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.compatability.Content2HTMLPage;
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

public class OldDecorator2NewDecorator
extends BaseWebAppDecorator
implements RequestConstants {
    private final Decorator oldDecorator;

    public OldDecorator2NewDecorator(Decorator oldDecorator) {
        this.oldDecorator = oldDecorator;
    }

    protected void render(Content content, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, SiteMeshWebAppContext webAppContext) throws IOException, ServletException {
        request.setAttribute(PAGE, (Object)new Content2HTMLPage(content, request));
        if (this.oldDecorator.getURIPath() != null && servletContext.getContext(this.oldDecorator.getURIPath()) != null) {
            servletContext = servletContext.getContext(this.oldDecorator.getURIPath());
        }
        RequestDispatcher dispatcher = servletContext.getRequestDispatcher(this.oldDecorator.getPage());
        dispatcher.include((ServletRequest)request, (ServletResponse)response);
        request.removeAttribute(PAGE);
    }
}

