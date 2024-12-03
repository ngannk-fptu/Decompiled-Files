/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.hostcontainer.DefaultHostContainer
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.webresource.WebResourceModuleDescriptor
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.action.modules;

import com.atlassian.plugin.hostcontainer.DefaultHostContainer;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;
import com.atlassian.streams.action.ActionHandlerAccessor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.List;

public class ActionHandlersWebResourceModuleDescriptor
extends WebResourceModuleDescriptor {
    private final ActionHandlerAccessor actionHandlerAccessor;

    public ActionHandlersWebResourceModuleDescriptor(ModuleFactory moduleFactory, ActionHandlerAccessor actionHandlerAccessor) {
        super(moduleFactory, (HostContainer)new DefaultHostContainer());
        this.actionHandlerAccessor = (ActionHandlerAccessor)Preconditions.checkNotNull((Object)actionHandlerAccessor, (Object)"actionHandlerAccessor");
    }

    public List<String> getDependencies() {
        return ImmutableList.copyOf((Iterable)Iterables.concat((Iterable)super.getDependencies(), this.actionHandlerAccessor.getActionHandlerModuleKeys()));
    }
}

