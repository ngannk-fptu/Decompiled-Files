/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.plugin.PluginParseException
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.dacidecision;

import com.atlassian.confluence.plugins.AtlassianPlaybookBlueprintsContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.plugin.PluginParseException;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class DaciDecisionContextProvider
extends AbstractBlueprintContextProvider {
    private final AtlassianPlaybookBlueprintsContextProvider helper;
    private String soyTemplateName;
    private String analyticsKey;
    private String pluginResourceKey = "com.atlassian.confluence.plugins.atlassian-playbook-blueprints:dacidecision-resources";

    public DaciDecisionContextProvider(AtlassianPlaybookBlueprintsContextProvider helper) {
        this.helper = helper;
    }

    public void init(Map<String, String> params) throws PluginParseException {
        this.soyTemplateName = params.get("soyTemplateName");
        this.analyticsKey = params.get("analyticsKey");
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        this.helper.onBlueprintCreated(this.analyticsKey);
        HashMap soyContext = Maps.newHashMap();
        I18NBean i18nBean = this.helper.getI18nBean();
        this.addStatusAndOutcomeToContextMap(context, soyContext, i18nBean);
        this.addMentionsToContextMap(context, soyContext, i18nBean, "driver", "hasDriver", "dacidecision.blueprint.template.driver.placeholder");
        this.addMentionsToContextMap(context, soyContext, i18nBean, "approver", "hasApprover", "dacidecision.blueprint.template.approver.placeholder");
        soyContext.put("dueDate", this.helper.createStorageFormatForDate((String)context.get("due-date")));
        context.put("daciDecisionTemplateXML", (Object)this.helper.renderFromSoy(this.pluginResourceKey, this.soyTemplateName, soyContext));
        return context;
    }

    private void addMentionsToContextMap(BlueprintContext context, Map<String, Object> templateContext, I18NBean i18nBean, String field, String variableCondition, String placeholderI18NKey) {
        String people = (String)context.get(field);
        if (StringUtils.isNotBlank((CharSequence)people)) {
            String[] userKeys = people.split(",");
            templateContext.put(variableCondition, true);
            templateContext.put(field, userKeys);
        } else {
            templateContext.put(variableCondition, false);
            templateContext.put(field, i18nBean.getText(placeholderI18NKey));
        }
    }

    private void addPlaceholderToContextMap(BlueprintContext context, Map<String, Object> templateContext, I18NBean i18nBean, String field, String variableCondition, String placeholderI18nKey) {
        String fieldValue = (String)context.get(field);
        boolean hasVariable = true;
        if (StringUtils.isBlank((CharSequence)fieldValue)) {
            fieldValue = i18nBean.getText(placeholderI18nKey);
            hasVariable = false;
        }
        templateContext.put(variableCondition, hasVariable);
        templateContext.put(field, fieldValue);
    }

    private void addStatusAndOutcomeToContextMap(BlueprintContext context, Map<String, Object> templateContext, I18NBean i18nBean) {
        StatusMacroDetails status;
        try {
            status = StatusMacroDetails.valueOf((String)context.get("status"));
            if (status != StatusMacroDetails.GREEN) {
                context.put("outcome", (Object)"");
            }
        }
        catch (IllegalArgumentException | NullPointerException e) {
            status = StatusMacroDetails.getDefault();
        }
        templateContext.put("status", i18nBean.getText(status.getI18nKey()));
        templateContext.put("statusColour", status.getMacroColour());
        this.addPlaceholderToContextMap(context, templateContext, i18nBean, "outcome", "hasOutcome", "dacidecision.blueprint.wizard.form.label.outcome.placeholder");
    }

    public static enum StatusMacroDetails {
        GREY("Grey", "dacidecision.blueprint.wizard.form.status.open"),
        GREEN("Green", "dacidecision.blueprint.wizard.form.status.decided"),
        YELLOW("Yellow", "dacidecision.blueprint.wizard.form.status.progress");

        private final String macroColour;
        private final String i18nKey;

        private StatusMacroDetails(String macroColour, String i18nKey) {
            this.macroColour = macroColour;
            this.i18nKey = i18nKey;
        }

        public String getMacroColour() {
            return this.macroColour;
        }

        public String getI18nKey() {
            return this.i18nKey;
        }

        public static StatusMacroDetails getDefault() {
            return GREY;
        }
    }
}

