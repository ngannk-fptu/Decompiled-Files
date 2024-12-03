/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.hostcontainer.DefaultHostContainer
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.webresource.WebResourceModuleDescriptor
 */
package com.atlassian.streams.action.modules;

import com.atlassian.plugin.hostcontainer.DefaultHostContainer;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;

public class ActionHandlersModuleDescriptor
extends WebResourceModuleDescriptor {
    public ActionHandlersModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory, (HostContainer)new DefaultHostContainer());
    }
}

