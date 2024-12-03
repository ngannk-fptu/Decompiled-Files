/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.springframework.web.context.support;

import javax.servlet.ServletContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextResource;

public class ServletContextResourceLoader
extends DefaultResourceLoader {
    private final ServletContext servletContext;

    public ServletContextResourceLoader(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    protected Resource getResourceByPath(String path) {
        return new ServletContextResource(this.servletContext, path);
    }
}

