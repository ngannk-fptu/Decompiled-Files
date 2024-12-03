/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.plugin.servlet.filter.JohnsonServletFilterModuleContainerFilter
 *  com.atlassian.plugin.servlet.ServletModuleManager
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.LazyReference
 *  com.atlassian.util.concurrent.LazyReference$InitializationException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.servlet.filter;

import com.atlassian.confluence.setup.SetupContext;
import com.atlassian.johnson.plugin.servlet.filter.JohnsonServletFilterModuleContainerFilter;
import com.atlassian.plugin.servlet.ServletModuleManager;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.LazyReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletFilterModuleContainerFilter
extends JohnsonServletFilterModuleContainerFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ServletFilterModuleContainerFilter.class);
    private final LazyReference<ServletModuleManager> moduleManagerReference = new LazyComponentReference("servletModuleManager");

    protected ServletModuleManager getServletModuleManager() {
        if (SetupContext.isAvailable()) {
            return (ServletModuleManager)SetupContext.get().getBean("setupServletModuleManager", ServletModuleManager.class);
        }
        if (!ContainerManager.isContainerSetup()) {
            return null;
        }
        try {
            return (ServletModuleManager)this.moduleManagerReference.get();
        }
        catch (LazyReference.InitializationException | IllegalStateException e) {
            LOG.debug("Application context has not been properly initialized", e);
            LOG.error("Application context has not been properly initialized: ", (Object)e.getMessage());
            return null;
        }
    }
}

