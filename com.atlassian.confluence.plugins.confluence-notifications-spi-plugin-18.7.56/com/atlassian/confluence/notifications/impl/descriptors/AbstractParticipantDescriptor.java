/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ContainerAccessor
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.util.concurrent.ResettableLazyReference
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.notifications.impl.descriptors;

import com.atlassian.confluence.notifications.Participant;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ContainerAccessor;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.util.concurrent.ResettableLazyReference;
import com.google.common.base.Preconditions;

public abstract class AbstractParticipantDescriptor<P extends Participant>
extends AbstractModuleDescriptor<P> {
    private final ResettableLazyReference<P> moduleReference = new ResettableLazyReference<P>(){

        protected P create() throws Exception {
            return (Participant)AbstractParticipantDescriptor.this.moduleFactory.createModule(AbstractParticipantDescriptor.this.moduleClassName, (ModuleDescriptor)AbstractParticipantDescriptor.this);
        }
    };

    public AbstractParticipantDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public P getModule() {
        return (P)((Participant)this.moduleReference.get());
    }

    public void disabled() {
        this.moduleReference.reset();
        super.disabled();
    }

    protected void loadClass(Plugin plugin, String clazz) throws PluginParseException {
        try {
            this.moduleClass = plugin.loadClass(clazz, null);
            Preconditions.checkArgument((boolean)Participant.class.isAssignableFrom(this.moduleClass), (String)"Given class [%s] is not a subtype of [%s]", (Object)clazz, (Object)Participant.class.getName());
        }
        catch (ClassNotFoundException e) {
            throw new PluginParseException((Throwable)e);
        }
    }

    protected <T> Class<T> loadClassUnchecked(String className) {
        try {
            return this.plugin.loadClass(className, null);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException(String.format("Failed to load class [%s] with the class loader from plugin [%s].", className, this.plugin), e);
        }
    }

    protected ContainerAccessor getContainerAccessor() {
        return ((ContainerManagedPlugin)this.plugin).getContainerAccessor();
    }
}

