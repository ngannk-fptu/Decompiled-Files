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
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  org.dom4j.Element
 */
package com.atlassian.confluence.notifications.batch.descriptor;

import com.atlassian.confluence.notifications.batch.service.BatchSectionProvider;
import com.atlassian.confluence.notifications.impl.descriptors.AbstractParticipantDescriptor;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.dom4j.Element;

public class BatchSectionProviderDescriptor
extends AbstractParticipantDescriptor<BatchSectionProvider> {
    private Set<ModuleCompleteKey> notificationKeys;
    private int weight;

    public BatchSectionProviderDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"forNotification").withError("Missing elements [forNnotification] denoting the notifications that this notification batching processor is used for ")});
    }

    public void init(Plugin plugin, Element descriptor) throws PluginParseException {
        super.init(plugin, descriptor);
        ImmutableSet.Builder notificationKeysBuilder = ImmutableSet.builder();
        for (Element notification : descriptor.elements("forNotification")) {
            notificationKeysBuilder.add((Object)this.getNotificationKey(notification.getTextTrim()));
        }
        this.notificationKeys = notificationKeysBuilder.build();
        String weightAttribute = descriptor.attributeValue("weight");
        this.weight = weightAttribute != null ? Integer.valueOf(weightAttribute) : 0;
    }

    public ModuleCompleteKey getNotificationKey(String notificationKey) {
        try {
            return new ModuleCompleteKey(notificationKey);
        }
        catch (IllegalArgumentException ignored) {
            return new ModuleCompleteKey(this.getPluginKey(), notificationKey);
        }
    }

    public Set<ModuleCompleteKey> getNotificationKeys() {
        return this.notificationKeys;
    }

    public int getWeight() {
        return this.weight;
    }
}

