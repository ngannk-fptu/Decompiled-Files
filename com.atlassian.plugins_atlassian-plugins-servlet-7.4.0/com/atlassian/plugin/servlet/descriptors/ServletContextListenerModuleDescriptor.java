/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.RequirePermission
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  javax.annotation.Nonnull
 *  javax.servlet.ServletContextListener
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.servlet.descriptors;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.RequirePermission;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import javax.annotation.Nonnull;
import javax.servlet.ServletContextListener;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequirePermission(value={"execute_java"})
public class ServletContextListenerModuleDescriptor
extends AbstractModuleDescriptor<ServletContextListener> {
    protected static final Logger log = LoggerFactory.getLogger(ServletContextListenerModuleDescriptor.class);

    public ServletContextListenerModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) {
        super.init(plugin, element);
        this.checkPermissions();
    }

    public ServletContextListener getModule() {
        return (ServletContextListener)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }
}

