/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.troubleshooting.stp.spi;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.troubleshooting.stp.spi.BaseWeightedModuleDescriptor;
import com.atlassian.troubleshooting.stp.spi.ClassUtils;
import com.atlassian.troubleshooting.stp.spi.SupportDataAppender;

public class SupportDataModuleDescriptor
extends BaseWeightedModuleDescriptor<SupportDataAppender<?>> {
    public static final int DEFAULT_WEIGHT = 1000;
    private Class<?> contextClass;
    private SupportDataAppender<?> module;

    public SupportDataModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory, 1000);
    }

    public SupportDataAppender<?> getModule() {
        return this.module;
    }

    public Class<?> getContextClass() {
        return this.contextClass;
    }

    public void enabled() {
        super.enabled();
        this.module = (SupportDataAppender)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
        this.contextClass = ClassUtils.getSupportDataAppenderType(this.module.getClass());
    }

    public void disabled() {
        super.disabled();
        this.module = null;
        this.contextClass = null;
    }
}

