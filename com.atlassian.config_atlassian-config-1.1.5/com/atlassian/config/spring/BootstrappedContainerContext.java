/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.SpringContainerContext
 *  javax.servlet.ServletContext
 *  org.springframework.context.ApplicationContext
 *  org.springframework.web.context.ContextLoader
 *  org.springframework.web.context.WebApplicationContext
 *  org.springframework.web.context.support.WebApplicationContextUtils
 */
package com.atlassian.config.spring;

import com.atlassian.config.spring.BootstrappedContextLoader;
import com.atlassian.spring.container.SpringContainerContext;
import javax.servlet.ServletContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class BootstrappedContainerContext
extends SpringContainerContext {
    public synchronized void refresh() {
        ContextLoader loader = this.createContextLoader();
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext((ServletContext)this.getServletContext());
        if (ctx != null) {
            loader.closeWebApplicationContext(this.getServletContext());
        }
        loader.initWebApplicationContext(this.getServletContext());
        if (this.getApplicationContext() == null) {
            this.setApplicationContext((ApplicationContext)WebApplicationContextUtils.getWebApplicationContext((ServletContext)this.getServletContext()));
        }
        this.contextReloaded();
    }

    private ContextLoader createContextLoader() {
        return new BootstrappedContextLoader();
    }
}

