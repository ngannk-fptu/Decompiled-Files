/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.StateAware
 *  com.atlassian.webhooks.Webhook
 */
package com.atlassian.webhooks.module;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.StateAware;
import com.atlassian.webhooks.Webhook;

public interface WebhookModuleDescriptor
extends ModuleDescriptor<Webhook>,
StateAware {
}

