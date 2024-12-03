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

public class LocationTaskReportContextProvider
extends AbstractBlueprintContextProvider {
    private final TaskReportBlueprintContextProviderHelper helper;

    public LocationTaskReportContextProvider(TaskReportBlueprintContextProviderHelper contextProviderHelper) {
        this.helper = contextProviderHelper;
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        String includeCompletedTask;
        String labels;
        String pageIds;
        HashMap<String, Object> soyContext = new HashMap<String, Object>();
        String spaceKeys = (String)context.get("locationsSpace");
        if (!Strings.isNullOrEmpty((String)spaceKeys)) {
            soyContext.put("spaceKeys", spaceKeys);
        }
        if (!Strings.isNullOrEmpty((String)(pageIds = (String)context.get("locationsPage")))) {
            soyContext.put("pageIds", pageIds);
        }
        if (!(labels = (String)context.get("labels")).isEmpty()) {
            soyContext.put("labels", labels);
        }
        if (!Strings.isNullOrEmpty((String)(includeCompletedTask = (String)context.get("completed")))) {
            soyContext.put("showCompletedTasks", true);
        }
        String reportContentXHTML = this.helper.renderFromSoy("Confluence.InlineTasks.Report.Templates.locationReportContent.soy", soyContext);
        context.put("reportContentXHTML", (Object)reportContentXHTML);
        return context;
    }
}

