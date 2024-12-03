/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.files.notifications.conditions.preview;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.plugins.files.notifications.api.FileContentEventType;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.google.common.collect.ImmutableSet;
import java.util.Map;

public class ResolveCommentCondition
implements Condition {
    private final ImmutableSet<FileContentEventType> SUPPORTED_EVENTS = ImmutableSet.of((Object)((Object)FileContentEventType.CREATE_COMMENT));

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        Content descendantContent = (Content)context.get("descendantContent");
        if (descendantContent == null) {
            return false;
        }
        return this.SUPPORTED_EVENTS.contains(context.get("eventType")) && descendantContent.getAncestors().isEmpty();
    }
}

