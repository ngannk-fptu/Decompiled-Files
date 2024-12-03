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
import com.opensymphony.sitemesh.Decorator;
import com.opensymphony.sitemesh.SiteMeshContext;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseWebAppDecorator
implements Decorator {
    protected abstract void render(Content var1, HttpServletRequest var2, HttpServletResponse var3, ServletContext var4, SiteMeshWebAppContext var5) throws IOException, ServletException;

    public void render(Content content, SiteMeshContext context) {
        SiteMeshWebAppContext webAppContext = (SiteMeshWebAppContext)context;
        try {
            this.render(content, webAppContext.getRequest(), webAppContext.getResponse(), webAppContext.getServletContext(), webAppContext);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}

