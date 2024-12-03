/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.confluence.plugins.createjiracontent;

import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class JIRAApplicationLinkCondition
implements Condition {
    private ReadOnlyApplicationLinkService applicationLinkService;

    public JIRAApplicationLinkCondition(ReadOnlyApplicationLinkService applicationLinkService) {
        this.applicationLinkService = applicationLinkService;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> arg0) {
        return this.applicationLinkService.getApplicationLinks(JiraApplicationType.class).iterator().hasNext();
    }
}

