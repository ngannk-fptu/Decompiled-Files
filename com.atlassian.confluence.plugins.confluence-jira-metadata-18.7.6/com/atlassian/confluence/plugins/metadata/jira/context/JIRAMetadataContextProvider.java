/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 */
package com.atlassian.confluence.plugins.metadata.jira.context;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraAggregate;
import com.atlassian.confluence.plugins.metadata.jira.service.JiraMetadataService;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import java.util.HashMap;
import java.util.Map;

public class JIRAMetadataContextProvider
implements ContextProvider {
    private final JiraMetadataService jiraMetadataService;
    private final ConfluenceWebResourceManager webResourceManager;

    public JIRAMetadataContextProvider(JiraMetadataService jiraMetadataService, ConfluenceWebResourceManager webResourceManager) {
        this.jiraMetadataService = jiraMetadataService;
        this.webResourceManager = webResourceManager;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> params) {
        HashMap<String, Object> context = new HashMap<String, Object>();
        AbstractPage page = (AbstractPage)params.get("page");
        if (page != null) {
            JiraAggregate aggregateData = this.jiraMetadataService.getAggregateDataIfCached(page.getId());
            this.webResourceManager.putMetadata("jira-metadata-count", String.valueOf(aggregateData.getCount()));
            if (aggregateData.getCount() > 0) {
                this.webResourceManager.putMetadata("jira-metadata-count-incomplete", String.valueOf(aggregateData.isIncomplete()));
            }
        }
        return context;
    }
}

