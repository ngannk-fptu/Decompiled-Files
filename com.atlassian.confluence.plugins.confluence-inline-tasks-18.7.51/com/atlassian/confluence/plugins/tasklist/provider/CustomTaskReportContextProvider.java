/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 */
package com.atlassian.confluence.plugins.tasklist.provider;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.plugins.tasklist.provider.TaskReportBlueprintContextProviderHelper;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import java.util.HashMap;

public class CustomTaskReportContextProvider
extends AbstractBlueprintContextProvider {
    private final TaskReportBlueprintContextProviderHelper helper;

    public CustomTaskReportContextProvider(TaskReportBlueprintContextProviderHelper contextProviderHelper) {
        this.helper = contextProviderHelper;
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        HashMap<String, Object> soyContext = new HashMap<String, Object>();
        if (AuthenticatedUserThreadLocal.getUsername() != null) {
            soyContext.put("creators", this.helper.prepareUserKeys(AuthenticatedUserThreadLocal.getUsername()));
        }
        String reportContentXHTML = this.helper.renderFromSoy("Confluence.InlineTasks.Report.Templates.customReportContent.soy", soyContext);
        context.put("reportContentXHTML", (Object)reportContentXHTML);
        return context;
    }
}

