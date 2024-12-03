/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.ServletContextFactory
 *  javax.servlet.ServletContext
 *  org.springframework.web.context.ServletContextAware
 */
package com.atlassian.confluence.servlet;

import com.atlassian.plugin.servlet.ServletContextFactory;
import javax.servlet.ServletContext;
import org.springframework.web.context.ServletContextAware;

public class ConfluenceServletContextFactory
implements ServletContextAware,
ServletContextFactory {
    private ServletContext servletContext;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }
}

