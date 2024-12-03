/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.google.common.base.Preconditions
 *  io.atlassian.util.concurrent.ResettableLazyReference
 */
package com.atlassian.confluence.notifications.impl.descriptors;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.notifications.PayloadProcessor;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.google.common.base.Preconditions;
import io.atlassian.util.concurrent.ResettableLazyReference;

@ExperimentalSpi
public class PayloadProcessorDescriptor
extends AbstractModuleDescriptor<PayloadProcessor> {
    private final ResettableLazyReference<PayloadProcessor> moduleReference = new ResettableLazyReference<PayloadProcessor>(){

        protected PayloadProcessor create() {
            return (PayloadProcessor)PayloadProcessorDescriptor.this.moduleFactory.createModule(PayloadProcessorDescriptor.this.moduleClassName, (ModuleDescriptor)PayloadProcessorDescriptor.this);
        }
    };

    public PayloadProcessorDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    protected void loadClass(Plugin plugin, String clazz) throws PluginParseException {
        try {
            this.moduleClass = plugin.loadClass(clazz, null);
            Preconditions.checkArgument((boolean)PayloadProcessor.class.isAssignableFrom(this.moduleClass), (String)"Given class [%s] is not a subtype of [%s]", (Object)clazz, (Object)PayloadProcessor.class.getName());
        }
        catch (ClassNotFoundException e) {
            throw new PluginParseException((Throwable)e);
        }
    }

    public PayloadProcessor getModule() {
        return (PayloadProcessor)this.moduleReference.get();
    }

    public void disabled() {
        this.moduleReference.reset();
        super.disabled();
    }
}

