/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.ServletModuleManager
 *  com.atlassian.plugin.servlet.filter.ServletFilterModuleContainerFilter
 */
package com.atlassian.plugins.rest.module.servlet;

import com.atlassian.plugin.servlet.ServletModuleManager;
import com.atlassian.plugin.servlet.filter.ServletFilterModuleContainerFilter;
import com.atlassian.plugins.rest.module.servlet.RestServletModuleManager;
import java.util.Objects;

public class RestServletFilterModuleContainerServlet
extends ServletFilterModuleContainerFilter {
    private final ServletModuleManager servletModuleManager;

    public RestServletFilterModuleContainerServlet(RestServletModuleManager servletModuleManager) {
        this.servletModuleManager = Objects.requireNonNull(servletModuleManager);
    }

    protected ServletModuleManager getServletModuleManager() {
        return this.servletModuleManager;
    }
}

