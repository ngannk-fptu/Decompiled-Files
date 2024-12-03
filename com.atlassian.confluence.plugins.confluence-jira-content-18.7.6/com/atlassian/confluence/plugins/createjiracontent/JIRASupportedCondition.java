/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.confluence.plugins.createjiracontent;

import com.atlassian.confluence.plugins.createjiracontent.JiraResourcesManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class JIRASupportedCondition
implements Condition {
    private JiraResourcesManager jiraResourcesManager;

    public JIRASupportedCondition(JiraResourcesManager jiraResourcesManager) {
        this.jiraResourcesManager = jiraResourcesManager;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> params) {
        return !this.jiraResourcesManager.getSupportedJiraServers().isEmpty();
    }
}

