/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.TemplateRendererHelper
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.plugin.PluginParseException
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.blueprint;

import com.atlassian.confluence.plugins.BusinessBlueprintsContextProviderHelper;
import com.atlassian.confluence.plugins.createcontent.TemplateRendererHelper;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.plugin.PluginParseException;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;

public class DefaultDetailSummaryIndexContextProvider
extends AbstractBlueprintContextProvider {
    protected BusinessBlueprintsContextProviderHelper helper;
    private String i18nKeyPrefix;

    public DefaultDetailSummaryIndexContextProvider(BusinessBlueprintsContextProviderHelper helper, TemplateRendererHelper templateRendererHelper) {
        super(templateRendererHelper);
        this.helper = helper;
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        I18NBean i18NBean = this.helper.getI18nBean();
        this.addDetailsSummaryMacroToContextMap(context, i18NBean);
        this.addCreateFromTemplateMacroToContextMap(context, i18NBean);
        return context;
    }

    protected String getI18nKeyPrefix(BlueprintContext context) {
        return this.i18nKeyPrefix != null ? this.i18nKeyPrefix : context.getBlueprintModuleCompleteKey().getCompleteKey().replace(':', '.');
    }

    protected String getIntroParagraph(BlueprintContext context, I18NBean i18NBean) {
        return null;
    }

    private void addCreateFromTemplateMacroToContextMap(BlueprintContext context, I18NBean i18NBean) {
        String blueprintKey = context.getBlueprintModuleCompleteKey().getCompleteKey();
        String buttonLabel = i18NBean.getText(this.getI18nKeyPrefix(context) + ".create-button-label");
        String createFromTemplateMacro = this.renderCreateFromTemplateMacro(context.getBlueprintId().toString(), buttonLabel, "", blueprintKey);
        context.put("createFromTemplateMacro", (Object)createFromTemplateMacro);
    }

    private void addDetailsSummaryMacroToContextMap(BlueprintContext context, I18NBean i18NBean) {
        String templateLabel = context.getTemplateLabel();
        String spaceKey = context.getSpaceKey();
        String i18nPrefix = this.getI18nKeyPrefix(context);
        String firstColumn = i18NBean.getText(i18nPrefix + ".first-column");
        String headings = i18NBean.getText(i18nPrefix + ".headings");
        String blankTitle = i18NBean.getText(i18nPrefix + ".blank-title");
        String blankDescription = i18NBean.getText(i18nPrefix + ".blank-description");
        String createButtonLabel = i18NBean.getText(i18nPrefix + ".create-button-label");
        String blueprintModuleCompleteKey = "";
        if (context.getBlueprintModuleCompleteKey() != null) {
            blueprintModuleCompleteKey = context.getBlueprintModuleCompleteKey().getCompleteKey();
        }
        String contentBlueprintId = "";
        if (context.getBlueprintId() != null) {
            contentBlueprintId = context.getBlueprintId().toString();
        }
        HashMap soyContext = Maps.newHashMap();
        soyContext.put("label", templateLabel);
        soyContext.put("spaces", spaceKey);
        soyContext.put("firstcolumn", firstColumn);
        soyContext.put("headings", headings);
        soyContext.put("blueprintModuleCompleteKey", blueprintModuleCompleteKey);
        soyContext.put("blankTitle", blankTitle);
        soyContext.put("blankDescription", blankDescription);
        soyContext.put("createButtonLabel", createButtonLabel);
        soyContext.put("contentBlueprintId", contentBlueprintId);
        String detailsSummaryMacro = this.helper.renderFromSoy("com.atlassian.confluence.plugins.confluence-business-blueprints:common-template-resources", "Confluence.Blueprints.Common.Index.detailsSummaryMacro.soy", soyContext);
        context.put("detailsSummaryMacro", (Object)detailsSummaryMacro);
        String introParagraph = this.getIntroParagraph(context, i18NBean);
        context.put("introParagraph", (Object)(introParagraph != null ? introParagraph : ""));
    }

    public void init(Map<String, String> params) throws PluginParseException {
        this.i18nKeyPrefix = params.get("i18nKeyPrefix");
    }
}

