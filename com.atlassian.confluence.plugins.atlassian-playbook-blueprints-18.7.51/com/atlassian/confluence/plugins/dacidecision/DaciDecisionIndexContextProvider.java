/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.TemplateRendererHelper
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Maps
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.dacidecision;

import com.atlassian.confluence.plugins.AtlassianPlaybookBlueprintsContextProvider;
import com.atlassian.confluence.plugins.createcontent.TemplateRendererHelper;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Maps;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;

public class DaciDecisionIndexContextProvider
extends AbstractBlueprintContextProvider {
    protected AtlassianPlaybookBlueprintsContextProvider helper;
    private String pluginResourceKey = "com.atlassian.confluence.plugins.atlassian-playbook-blueprints:dacidecision-resources";
    private String i18nKeyPrefix = "dacidecision.blueprint.index";

    @Autowired
    public DaciDecisionIndexContextProvider(AtlassianPlaybookBlueprintsContextProvider helper, @ComponentImport TemplateRendererHelper templateRendererHelper) {
        super(templateRendererHelper);
        this.helper = helper;
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        I18NBean i18NBean = this.helper.getI18nBean();
        String closedLabel = i18NBean.getText(this.i18nKeyPrefix + ".closed.label");
        this.addDetailsSummaryMacroToContextMap(context, i18NBean, this.i18nKeyPrefix + ".outstanding", closedLabel, "outstandingSummaryMacro");
        this.addDetailsSummaryMacroToContextMap(context, i18NBean, this.i18nKeyPrefix + ".closed", closedLabel, "closedSummaryMacro");
        this.addCreateFromTemplateMacroToContextMap(context, i18NBean);
        return context;
    }

    protected String getIntroParagraph(BlueprintContext context, I18NBean i18NBean) {
        return null;
    }

    private void addCreateFromTemplateMacroToContextMap(BlueprintContext context, I18NBean i18NBean) {
        String blueprintKey = context.getBlueprintModuleCompleteKey().getCompleteKey();
        String buttonLabel = i18NBean.getText(this.i18nKeyPrefix + ".create-button-label");
        String createFromTemplateMacro = this.renderCreateFromTemplateMacro(context.getBlueprintId().toString(), buttonLabel, "", blueprintKey);
        context.put("createFromTemplateMacro", (Object)createFromTemplateMacro);
    }

    private void addDetailsSummaryMacroToContextMap(BlueprintContext context, I18NBean i18NBean, String i18nPrefix, String filter, String template) {
        String templateLabel = context.getTemplateLabel();
        String spaceKey = context.getSpaceKey();
        String firstColumn = i18NBean.getText(i18nPrefix + ".first-column");
        String headings = i18NBean.getText(i18nPrefix + ".headings");
        String blankTitle = i18NBean.getText(i18nPrefix + ".blank-title");
        String blankDescription = i18NBean.getText(i18nPrefix + ".blank-description");
        HashMap soyContext = Maps.newHashMap();
        soyContext.put("label1", templateLabel);
        soyContext.put("label2", filter);
        soyContext.put("spaces", spaceKey);
        soyContext.put("firstcolumn", firstColumn);
        soyContext.put("headings", headings);
        soyContext.put("blankTitle", blankTitle);
        soyContext.put("blankDescription", blankDescription);
        String detailsSummaryMacro = this.helper.renderFromSoy(this.pluginResourceKey, "Atlassian.TeamPlaybook.DaciDecisions." + template + ".soy", soyContext);
        context.put(template, (Object)detailsSummaryMacro);
        String introParagraph = this.getIntroParagraph(context, i18NBean);
        context.put("introParagraph", (Object)(introParagraph != null ? introParagraph : ""));
    }
}

