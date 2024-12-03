/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.opensymphony.module.sitemesh.Decorator
 *  com.opensymphony.module.sitemesh.Page
 *  com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper
 *  com.opensymphony.module.sitemesh.mapper.DefaultDecorator
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.struts2.views.velocity.VelocityManager
 */
package com.atlassian.confluence.setup.sitemesh;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.plugin.descriptor.DecoratorModuleDescriptor;
import com.atlassian.confluence.plugin.module.PluginProvidedDecoratorModule;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.spring.container.LazyComponentReference;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;
import com.opensymphony.module.sitemesh.mapper.DefaultDecorator;
import java.util.List;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.views.velocity.VelocityManager;

public class PluginDecoratorMapper
extends AbstractDecoratorMapper {
    private final Supplier<PluginAccessor> pluginAccessor;
    private final Supplier<VelocityManager> velocityManager;

    public PluginDecoratorMapper(PluginAccessor pluginAccessor, VelocityManager velocityManager) {
        this.pluginAccessor = () -> pluginAccessor;
        this.velocityManager = () -> velocityManager;
    }

    @Deprecated
    public PluginDecoratorMapper() {
        this.pluginAccessor = new LazyComponentReference("pluginAccessor");
        this.velocityManager = () -> (VelocityManager)BootstrapUtils.getBootstrapContext().getBean("velocityManager", VelocityManager.class);
    }

    public Decorator getDecorator(HttpServletRequest httpServletRequest, Page page) {
        if (this.getPluginAccessor() != null) {
            List decoratorModuleDescriptors = this.getPluginAccessor().getEnabledModuleDescriptorsByClass(DecoratorModuleDescriptor.class);
            for (DecoratorModuleDescriptor desc : decoratorModuleDescriptors) {
                String path;
                PluginProvidedDecoratorModule module = desc.getModule();
                if (!module.matches(path = httpServletRequest.getServletPath() + (httpServletRequest.getPathInfo() == null ? "" : httpServletRequest.getPathInfo()))) continue;
                return this.createDefaultDecorator(module);
            }
        }
        return super.getDecorator(httpServletRequest, page);
    }

    public Decorator getNamedDecorator(HttpServletRequest httpServletRequest, String name) {
        if (this.getPluginAccessor() != null) {
            List decoratorModuleDescriptors = this.getPluginAccessor().getEnabledModuleDescriptorsByClass(DecoratorModuleDescriptor.class);
            for (DecoratorModuleDescriptor desc : decoratorModuleDescriptors) {
                if (!name.equals(desc.getName())) continue;
                return this.createDefaultDecorator(desc.getModule());
            }
        }
        return super.getNamedDecorator(httpServletRequest, name);
    }

    private Decorator createDefaultDecorator(PluginProvidedDecoratorModule module) {
        try {
            this.velocityManager.get().getVelocityEngine().getTemplate(module.getTemplate());
        }
        catch (Exception e) {
            throw new RuntimeException("Couldn't find decorator template " + module.getTemplate(), e);
        }
        return new DefaultDecorator(module.key(), module.getTemplate(), null);
    }

    private PluginAccessor getPluginAccessor() {
        return this.pluginAccessor.get();
    }
}

