/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.google.common.base.Strings
 */
package com.atlassian.confluence.plugins.tasklist.provider;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.plugins.tasklist.provider.TaskReportBlueprintContextProviderHelper;
import com.google.common.base.Strings;
import java.util.HashMap;

public class TeamTaskReportContextProvider
extends AbstractBlueprintContextProvider {
    private final TaskReportBlueprintContextProviderHelper helper;

    public TeamTaskReportContextProvider(TaskReportBlueprintContextProviderHelper contextProviderHelper) {
        this.helper = contextProviderHelper;
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        String includeCompletedTask;
        HashMap<String, Object> soyContext = new HashMap<String, Object>();
        String teamMembers = (String)context.get("teamMembers");
        if (!teamMembers.isEmpty()) {
            soyContext.put("assignees", this.helper.prepareUserKeys(teamMembers));
        }
        if (!Strings.isNullOrEmpty((String)(includeCompletedTask = (String)context.get("completed")))) {
            soyContext.put("showCompletedTasks", true);
        }
        String reportContentXHTML = this.helper.renderFromSoy("Confluence.InlineTasks.Report.Templates.teamReportContent.soy", soyContext);
        context.put("reportContentXHTML", (Object)reportContentXHTML);
        return context;
    }
}

