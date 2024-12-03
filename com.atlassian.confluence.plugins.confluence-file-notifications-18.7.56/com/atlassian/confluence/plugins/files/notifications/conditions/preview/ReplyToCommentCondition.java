/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.confluence.plugins.files.notifications.conditions.preview;

import com.atlassian.confluence.plugins.files.notifications.api.FileContentEventType;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ReplyToCommentCondition
implements Condition {
    private final List<FileContentEventType> SUPPORTED_EVENTS = Collections.singletonList(FileContentEventType.CREATE_COMMENT);

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        return this.SUPPORTED_EVENTS.contains(context.get("eventType"));
    }
}

