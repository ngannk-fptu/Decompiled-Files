/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory
 *  javax.annotation.Nullable
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.AutowireCapableBeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.ApplicationContextException
 */
package com.atlassian.plugins.osgi.javaconfig.moduletypes;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextException;

@PublicApi
public abstract class PluginContextModuleDescriptorFactory<D extends ModuleDescriptor>
implements ListableModuleDescriptorFactory,
ApplicationContextAware {
    private final Class<D> moduleDescriptorClass;
    private final String xmlElementType;
    private ApplicationContext applicationContext;

    protected PluginContextModuleDescriptorFactory(String xmlElementType, Class<D> moduleDescriptorClass) {
        this.moduleDescriptorClass = Objects.requireNonNull(moduleDescriptorClass, "moduleDescriptorClass cannot be null");
        this.xmlElementType = Objects.requireNonNull(xmlElementType, "xmlElementType cannot be null");
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext == null) {
            throw new ApplicationContextException("ApplicationContext cannot be null");
        }
        this.applicationContext = applicationContext;
    }

    @Nullable
    public ModuleDescriptor getModuleDescriptor(String type) {
        if (this.xmlElementType.equals(type)) {
            AutowireCapableBeanFactory autowireCapableBeanFactory = this.applicationContext.getAutowireCapableBeanFactory();
            Object moduleDescriptor = autowireCapableBeanFactory.createBean(this.moduleDescriptorClass, 3, false);
            return (ModuleDescriptor)moduleDescriptor;
        }
        return null;
    }

    public boolean hasModuleDescriptor(String type) {
        return this.xmlElementType.equals(type);
    }

    @Nullable
    public Class<? extends ModuleDescriptor> getModuleDescriptorClass(String type) {
        return this.xmlElementType.equals(type) ? this.moduleDescriptorClass : null;
    }

    public Iterable<String> getModuleDescriptorKeys() {
        return Collections.singleton(this.xmlElementType);
    }

    public Set<Class<? extends ModuleDescriptor>> getModuleDescriptorClasses() {
        return Collections.singleton(this.moduleDescriptorClass);
    }
}

