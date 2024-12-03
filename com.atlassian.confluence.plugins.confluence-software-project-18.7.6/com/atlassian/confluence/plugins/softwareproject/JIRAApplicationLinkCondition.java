/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.confluence.plugins.softwareproject;

import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class JIRAApplicationLinkCondition
implements Condition {
    private ReadOnlyApplicationLinkService readOnlyApplicationLinkService;

    public JIRAApplicationLinkCondition(ReadOnlyApplicationLinkService readOnlyApplicationLinkService) {
        this.readOnlyApplicationLinkService = readOnlyApplicationLinkService;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> arg0) {
        return this.readOnlyApplicationLinkService.getApplicationLinks(JiraApplicationType.class).iterator().hasNext();
    }
}

