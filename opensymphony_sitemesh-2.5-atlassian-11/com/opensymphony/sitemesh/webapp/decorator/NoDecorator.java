/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.opensymphony.sitemesh.webapp.decorator;

import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import com.opensymphony.sitemesh.webapp.decorator.BaseWebAppDecorator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoDecorator
extends BaseWebAppDecorator {
    protected void render(Content content, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, SiteMeshWebAppContext webAppContext) throws IOException, ServletException {
        if (webAppContext.isUsingStream()) {
            PrintWriter writer = new PrintWriter((OutputStream)response.getOutputStream());
            content.writeOriginal(writer);
            writer.flush();
            response.getOutputStream().flush();
        } else {
            PrintWriter writer = response.getWriter();
            content.writeOriginal(writer);
            response.getWriter().flush();
        }
    }
}

