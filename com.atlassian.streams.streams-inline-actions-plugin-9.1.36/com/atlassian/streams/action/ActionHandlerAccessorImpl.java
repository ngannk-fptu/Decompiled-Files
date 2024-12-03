/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.action;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.streams.action.ActionHandlerAccessor;
import com.atlassian.streams.action.modules.ActionHandlersModuleDescriptor;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

public class ActionHandlerAccessorImpl
implements ActionHandlerAccessor {
    private final PluginAccessor pluginAccessor;
    private static final Function<ActionHandlersModuleDescriptor, String> toCompleteModuleKey = new Function<ActionHandlersModuleDescriptor, String>(){

        public String apply(ActionHandlersModuleDescriptor module) {
            return module.getCompleteKey();
        }
    };

    public ActionHandlerAccessorImpl(PluginAccessor pluginAccessor) {
        this.pluginAccessor = (PluginAccessor)Preconditions.checkNotNull((Object)pluginAccessor, (Object)"pluginAccessor");
    }

    @Override
    public Iterable<String> getActionHandlerModuleKeys() {
        return Iterables.transform(this.getActionHandlerModuleDescriptors(), toCompleteModuleKey);
    }

    private Iterable<ActionHandlersModuleDescriptor> getActionHandlerModuleDescriptors() {
        return this.pluginAccessor.getEnabledModuleDescriptorsByClass(ActionHandlersModuleDescriptor.class);
    }
}

