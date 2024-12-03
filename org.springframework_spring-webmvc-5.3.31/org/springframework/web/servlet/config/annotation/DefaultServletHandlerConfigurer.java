/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.servlet.config.annotation;

import java.util.Collections;
import javax.servlet.ServletContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;

public class DefaultServletHandlerConfigurer {
    private final ServletContext servletContext;
    @Nullable
    private DefaultServletHttpRequestHandler handler;

    public DefaultServletHandlerConfigurer(ServletContext servletContext) {
        Assert.notNull((Object)servletContext, (String)"ServletContext is required");
        this.servletContext = servletContext;
    }

    public void enable() {
        this.enable(null);
    }

    public void enable(@Nullable String defaultServletName) {
        this.handler = new DefaultServletHttpRequestHandler();
        if (defaultServletName != null) {
            this.handler.setDefaultServletName(defaultServletName);
        }
        this.handler.setServletContext(this.servletContext);
    }

    @Nullable
    protected SimpleUrlHandlerMapping buildHandlerMapping() {
        if (this.handler == null) {
            return null;
        }
        return new SimpleUrlHandlerMapping(Collections.singletonMap("/**", this.handler), Integer.MAX_VALUE);
    }
}

