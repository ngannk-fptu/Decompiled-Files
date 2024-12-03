/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition
 *  com.atlassian.plugin.PluginParseException
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 */
package com.atlassian.confluence.plugins.inlinecomments.web;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition;
import com.atlassian.plugin.PluginParseException;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;

@ExperimentalApi
public class InlineStatusCondition
extends AbstractNotificationCondition {
    private Set<String> accepted;
    private static final String DEFAULT_CONTEXT_NAME = "content";

    public void init(Map<String, String> params) throws PluginParseException {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        builder.addAll(params.keySet());
        this.accepted = builder.build();
    }

    protected boolean shouldDisplay(NotificationContext context) {
        Object contextContent = context.get(DEFAULT_CONTEXT_NAME);
        if (contextContent instanceof ContentEntityObject) {
            ContentEntityObject content = (ContentEntityObject)contextContent;
            return this.accepted.contains(content.getProperties().getStringProperty("status"));
        }
        return false;
    }
}

