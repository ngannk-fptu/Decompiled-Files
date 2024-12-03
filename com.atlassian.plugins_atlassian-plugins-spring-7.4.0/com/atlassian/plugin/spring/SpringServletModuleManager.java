/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.servlet.DefaultServletModuleManager
 *  com.atlassian.plugin.servlet.ServletModuleManager
 *  com.atlassian.plugin.servlet.util.ServletContextServletModuleManagerAccessor
 *  javax.annotation.Nonnull
 *  javax.servlet.ServletContext
 *  org.springframework.web.context.ServletContextAware
 */
package com.atlassian.plugin.spring;

import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.servlet.DefaultServletModuleManager;
import com.atlassian.plugin.servlet.ServletModuleManager;
import com.atlassian.plugin.servlet.util.ServletContextServletModuleManagerAccessor;
import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import org.springframework.web.context.ServletContextAware;

public class SpringServletModuleManager
extends DefaultServletModuleManager
implements ServletContextAware {
    public SpringServletModuleManager(PluginEventManager pluginEventManager) {
        super(pluginEventManager);
    }

    public void setServletContext(@Nonnull ServletContext servletContext) {
        ServletContextServletModuleManagerAccessor.setServletModuleManager((ServletContext)servletContext, (ServletModuleManager)this);
    }
}

