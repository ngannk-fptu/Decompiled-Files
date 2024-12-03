/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.confluence.plugins.files.notifications.conditions.footer;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class DisplayUnresolvedCommentsCondition
implements Condition {
    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        Map metadata = ((Content)context.get("fileContent")).getMetadata();
        return !metadata.isEmpty() && metadata.get("numUnresolvedComments") != null && (Integer)metadata.get("numUnresolvedComments") > 0;
    }
}

