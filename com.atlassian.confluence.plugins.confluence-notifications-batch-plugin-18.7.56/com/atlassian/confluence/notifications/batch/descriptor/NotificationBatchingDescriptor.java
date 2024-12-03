/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.impl.descriptors.AbstractParticipantDescriptor
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  org.dom4j.Element
 */
package com.atlassian.confluence.notifications.batch.descriptor;

import com.atlassian.confluence.notifications.batch.service.BatchingProcessor;
import com.atlassian.confluence.notifications.impl.descriptors.AbstractParticipantDescriptor;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;
import org.dom4j.Element;

public class NotificationBatchingDescriptor
extends AbstractParticipantDescriptor<BatchingProcessor> {
    private ModuleCompleteKey notificationKey;

    public NotificationBatchingDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@for-notification").withError("Missing attribute [for-notification=\"pluginKey:moduleCompleteKey\"] denoting the moduleKey of the notification that this notification batching processor is used for, e.g. [for-notification=\"email-notification-plugin:page-created-notification\"], on")});
    }

    public void init(Plugin plugin, Element descriptor) throws PluginParseException {
        super.init(plugin, descriptor);
        try {
            this.notificationKey = new ModuleCompleteKey(descriptor.attributeValue("for-notification"));
        }
        catch (IllegalArgumentException ignored) {
            this.notificationKey = new ModuleCompleteKey(this.getPluginKey(), descriptor.attributeValue("for-notification"));
        }
    }

    public ModuleCompleteKey getNotificationKey() {
        return this.notificationKey;
    }
}

