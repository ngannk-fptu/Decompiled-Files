/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory
 */
package com.atlassian.plugins.rest.module;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory;
import com.atlassian.plugins.rest.module.RestModuleDescriptor;
import com.atlassian.plugins.rest.module.servlet.RestServletModuleManager;
import java.util.Objects;

public class RestModuleDescriptorFactory
extends SingleModuleDescriptorFactory<RestModuleDescriptor> {
    private final RestServletModuleManager servletModuleManager;
    private final ModuleFactory moduleFactory;
    private final String restContextPath;

    public RestModuleDescriptorFactory(HostContainer hostContainer, ModuleFactory moduleFactory, RestServletModuleManager servletModuleManager, String restContextPath) {
        super(Objects.requireNonNull(hostContainer, "hostContainer can't be null"), "rest", RestModuleDescriptor.class);
        this.moduleFactory = moduleFactory;
        this.servletModuleManager = Objects.requireNonNull(servletModuleManager, "servletModuleManager can't be null");
        this.restContextPath = Objects.requireNonNull(restContextPath, "restContextPath can't be null");
    }

    public ModuleDescriptor getModuleDescriptor(String type) {
        return this.hasModuleDescriptor(type) ? new RestModuleDescriptor(this.moduleFactory, this.servletModuleManager, this.restContextPath) : null;
    }
}

