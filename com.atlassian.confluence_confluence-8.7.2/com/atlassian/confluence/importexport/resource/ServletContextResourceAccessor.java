/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.springframework.web.context.ServletContextAware
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.importexport.resource.ResourceAccessor;
import java.io.InputStream;
import javax.servlet.ServletContext;
import org.springframework.web.context.ServletContextAware;

public class ServletContextResourceAccessor
implements ServletContextAware,
ResourceAccessor {
    private ServletContext servletContext;

    @Override
    public InputStream getResource(String resourcePath) {
        return this.servletContext.getResourceAsStream(resourcePath);
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}

