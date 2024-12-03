/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.osgi.javaconfig.moduletypes.PluginContextModuleDescriptorFactory
 */
package com.atlassian.webhooks.internal.module;

import com.atlassian.plugins.osgi.javaconfig.moduletypes.PluginContextModuleDescriptorFactory;
import com.atlassian.webhooks.internal.module.SimpleWebhookModuleDescriptor;

public class WebhookModuleDescriptorFactory
extends PluginContextModuleDescriptorFactory<SimpleWebhookModuleDescriptor> {
    public WebhookModuleDescriptorFactory() {
        super("webhook", SimpleWebhookModuleDescriptor.class);
    }
}

