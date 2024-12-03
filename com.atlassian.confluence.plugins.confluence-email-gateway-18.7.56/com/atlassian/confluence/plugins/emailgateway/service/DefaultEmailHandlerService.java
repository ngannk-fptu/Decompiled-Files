/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.WeightedPluginModuleTracker
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 */
package com.atlassian.confluence.plugins.emailgateway.service;

import com.atlassian.confluence.plugin.descriptor.WeightedPluginModuleTracker;
import com.atlassian.confluence.plugins.emailgateway.api.EmailHandler;
import com.atlassian.confluence.plugins.emailgateway.api.EmailHandlingException;
import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;
import com.atlassian.confluence.plugins.emailgateway.api.descriptor.EmailHandlerModuleDescriptor;
import com.atlassian.confluence.plugins.emailgateway.service.EmailHandlerService;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;

public class DefaultEmailHandlerService
implements EmailHandlerService {
    private final WeightedPluginModuleTracker<EmailHandler, EmailHandlerModuleDescriptor> weightedPluginModuleTracker;

    public DefaultEmailHandlerService(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager) {
        this.weightedPluginModuleTracker = WeightedPluginModuleTracker.create((PluginAccessor)pluginAccessor, (PluginEventManager)pluginEventManager, EmailHandlerModuleDescriptor.class);
    }

    @Override
    public void handle(ReceivedEmail email) throws EmailHandlingException {
        for (EmailHandler handler : this.weightedPluginModuleTracker.getModules()) {
            if (!handler.handle(email)) continue;
            return;
        }
        throw new EmailHandlingException("No handler capable of handling this email is registered");
    }
}

