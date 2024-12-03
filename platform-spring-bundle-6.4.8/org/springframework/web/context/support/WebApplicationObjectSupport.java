/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.springframework.web.context.support;

import java.io.File;
import javax.servlet.ServletContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.WebUtils;

public abstract class WebApplicationObjectSupport
extends ApplicationObjectSupport
implements ServletContextAware {
    @Nullable
    private ServletContext servletContext;

    @Override
    public final void setServletContext(ServletContext servletContext) {
        if (servletContext != this.servletContext) {
            this.servletContext = servletContext;
            this.initServletContext(servletContext);
        }
    }

    @Override
    protected boolean isContextRequired() {
        return true;
    }

    @Override
    protected void initApplicationContext(ApplicationContext context) {
        super.initApplicationContext(context);
        if (this.servletContext == null && context instanceof WebApplicationContext) {
            this.servletContext = ((WebApplicationContext)context).getServletContext();
            if (this.servletContext != null) {
                this.initServletContext(this.servletContext);
            }
        }
    }

    protected void initServletContext(ServletContext servletContext) {
    }

    @Nullable
    protected final WebApplicationContext getWebApplicationContext() throws IllegalStateException {
        ApplicationContext ctx = this.getApplicationContext();
        if (ctx instanceof WebApplicationContext) {
            return (WebApplicationContext)this.getApplicationContext();
        }
        if (this.isContextRequired()) {
            throw new IllegalStateException("WebApplicationObjectSupport instance [" + this + "] does not run in a WebApplicationContext but in: " + ctx);
        }
        return null;
    }

    @Nullable
    protected final ServletContext getServletContext() throws IllegalStateException {
        if (this.servletContext != null) {
            return this.servletContext;
        }
        ServletContext servletContext = null;
        WebApplicationContext wac = this.getWebApplicationContext();
        if (wac != null) {
            servletContext = wac.getServletContext();
        }
        if (servletContext == null && this.isContextRequired()) {
            throw new IllegalStateException("WebApplicationObjectSupport instance [" + this + "] does not run within a ServletContext. Make sure the object is fully configured!");
        }
        return servletContext;
    }

    protected final File getTempDir() throws IllegalStateException {
        ServletContext servletContext = this.getServletContext();
        Assert.state(servletContext != null, "ServletContext is required");
        return WebUtils.getTempDir(servletContext);
    }
}

