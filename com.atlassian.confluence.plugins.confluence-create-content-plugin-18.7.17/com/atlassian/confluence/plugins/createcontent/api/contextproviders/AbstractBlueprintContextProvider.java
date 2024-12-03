/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.createcontent.api.contextproviders;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.confluence.plugins.createcontent.TemplateRendererHelper;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

@PublicSpi
public abstract class AbstractBlueprintContextProvider
implements ContextProvider {
    protected TemplateRendererHelper templateRendererHelper;

    @Deprecated
    public AbstractBlueprintContextProvider() {
    }

    public AbstractBlueprintContextProvider(TemplateRendererHelper templateRendererHelper) {
        this.templateRendererHelper = templateRendererHelper;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public final Map<String, Object> getContextMap(Map<String, Object> context) {
        return this.updateBlueprintContext(new BlueprintContext(context)).getMap();
    }

    protected abstract BlueprintContext updateBlueprintContext(BlueprintContext var1);

    protected String renderCreateFromTemplateMacro(String contentBlueprintId, String createButtonLabel, String spaceKey, String blueprintModuleCompleteKey) {
        if (this.templateRendererHelper == null) {
            throw new UnsupportedOperationException("Cannot use this method without first specifying a templateRendererHelper.");
        }
        HashMap createFromTemplateMacroParams = Maps.newHashMap();
        createFromTemplateMacroParams.put("contentBlueprintId", contentBlueprintId);
        createFromTemplateMacroParams.put("createButtonLabel", createButtonLabel);
        if (StringUtils.isNotBlank((CharSequence)spaceKey)) {
            createFromTemplateMacroParams.put("spaceKey", spaceKey);
        }
        createFromTemplateMacroParams.put("contentBlueprintId", contentBlueprintId);
        createFromTemplateMacroParams.put("blueprintModuleCompleteKey", blueprintModuleCompleteKey);
        return this.templateRendererHelper.renderMacroXhtml("create-from-template", createFromTemplateMacroParams);
    }

    protected String renderContentReportTableMacro(String templateLabel, String analyticsKey, String spaceKey, String blankTitle, String blankDescription, String createButtonLabel, String contentBlueprintId, String blueprintModuleCompleteKey) {
        if (this.templateRendererHelper == null) {
            throw new UnsupportedOperationException("Cannot use this method without first specifying a templateRendererHelper.");
        }
        HashMap contentReportTableMacroParams = Maps.newHashMap();
        contentReportTableMacroParams.put("labels", templateLabel);
        contentReportTableMacroParams.put("analyticsKey", analyticsKey);
        contentReportTableMacroParams.put("spaces", spaceKey);
        contentReportTableMacroParams.put("blankTitle", blankTitle);
        contentReportTableMacroParams.put("blankDescription", blankDescription);
        contentReportTableMacroParams.put("createButtonLabel", createButtonLabel);
        contentReportTableMacroParams.put("contentBlueprintId", contentBlueprintId);
        contentReportTableMacroParams.put("blueprintModuleCompleteKey", blueprintModuleCompleteKey);
        return this.templateRendererHelper.renderMacroXhtml("content-report-table", contentReportTableMacroParams);
    }

    public void setTemplateRendererHelper(TemplateRendererHelper templateRendererHelper) {
        this.templateRendererHelper = templateRendererHelper;
    }
}

