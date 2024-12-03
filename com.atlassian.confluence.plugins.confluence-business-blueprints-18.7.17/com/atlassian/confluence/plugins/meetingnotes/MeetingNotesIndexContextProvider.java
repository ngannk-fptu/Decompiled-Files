/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.TemplateRendererHelper
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.meetingnotes;

import com.atlassian.confluence.plugins.BusinessBlueprintsContextProviderHelper;
import com.atlassian.confluence.plugins.createcontent.TemplateRendererHelper;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;

public class MeetingNotesIndexContextProvider
extends AbstractBlueprintContextProvider {
    private final BusinessBlueprintsContextProviderHelper helper;

    public MeetingNotesIndexContextProvider(BusinessBlueprintsContextProviderHelper helper, TemplateRendererHelper templateRendererHelper) {
        super(templateRendererHelper);
        this.helper = helper;
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        String blueprintId = context.getBlueprintId().toString();
        String createLabel = this.helper.getI18nBean().getText("com.atlassian.confluence.plugins.confluence-business-blueprints.meeting-notes-blueprint.create-button-label");
        String spaceKey = context.getSpaceKey();
        String blueprintKey = context.getBlueprintModuleCompleteKey().getCompleteKey();
        String templateLabel = context.getTemplateLabel();
        String blankTitle = this.helper.getI18nBean().getText("com.atlassian.confluence.plugins.confluence-business-blueprints.meeting-notes-blueprint.blank-title");
        String blankDescription = this.helper.getI18nBean().getText("com.atlassian.confluence.plugins.confluence-business-blueprints.meeting-notes-blueprint.blank-description");
        context.put("taskReportMacro", (Object)this.renderTaskReportMacro(spaceKey, templateLabel));
        context.put("createFromTemplateMacro", (Object)this.renderCreateFromTemplateMacro(blueprintId, createLabel, "", blueprintKey));
        context.put("contentReportTableMacro", (Object)this.renderContentReportTableMacro(templateLabel, context.getAnalyticsKey(), spaceKey, blankTitle, blankDescription, createLabel, blueprintId, blueprintKey));
        return context;
    }

    private String renderTaskReportMacro(String spaceKey, String label) {
        HashMap soyContext = Maps.newHashMap();
        soyContext.put("status", "incomplete");
        soyContext.put("spaceAndPage", "space:" + spaceKey);
        soyContext.put("spaces", spaceKey);
        soyContext.put("labels", label);
        soyContext.put("pageSize", "10");
        return this.templateRendererHelper.renderMacroXhtml("tasks-report-macro", (Map)soyContext);
    }
}

