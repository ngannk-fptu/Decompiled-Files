/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.StateAware
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.google.common.base.Preconditions
 *  javax.servlet.http.HttpServlet
 */
package com.atlassian.plugin.servlet.descriptors;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.StateAware;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.servlet.ServletModuleManager;
import com.atlassian.plugin.servlet.descriptors.BaseServletModuleDescriptor;
import com.google.common.base.Preconditions;
import javax.servlet.http.HttpServlet;

public class ServletModuleDescriptor
extends BaseServletModuleDescriptor<HttpServlet>
implements StateAware {
    private final ServletModuleManager servletModuleManager;

    public ServletModuleDescriptor(ModuleFactory moduleFactory, ServletModuleManager servletModuleManager) {
        super(moduleFactory);
        this.servletModuleManager = (ServletModuleManager)Preconditions.checkNotNull((Object)servletModuleManager);
    }

    public void enabled() {
        super.enabled();
        this.servletModuleManager.addServletModule(this);
    }

    public void disabled() {
        this.servletModuleManager.removeServletModule(this);
        super.disabled();
    }

    public HttpServlet getModule() {
        return (HttpServlet)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }
}

