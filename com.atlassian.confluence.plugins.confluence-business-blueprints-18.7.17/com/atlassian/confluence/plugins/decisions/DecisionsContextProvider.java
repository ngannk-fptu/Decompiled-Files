/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.decisions;

import com.atlassian.confluence.plugins.BusinessBlueprintsContextProviderHelper;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.google.common.collect.Maps;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

public class DecisionsContextProvider
extends AbstractBlueprintContextProvider {
    private BusinessBlueprintsContextProviderHelper helper;

    public DecisionsContextProvider(BusinessBlueprintsContextProviderHelper helper) {
        this.helper = helper;
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        I18NBean i18nBean = this.helper.getI18nBean();
        this.addStatusAndOutcomeToContextMap(context, i18nBean);
        this.addMentionsToContextMap(context, i18nBean, "stakeholders", "mentions", "decisions.stakeholders.mentions.placeholder");
        this.addMentionsToContextMap(context, i18nBean, "owner", "owner", "decisions.blueprint.wizard.form.owner.placeholder");
        this.addPlaceholderToContextMap(context, i18nBean, "background", "background-placeholder", "decisions.blueprint.wizard.form.background.placeholder");
        context.put("due-date", (Object)this.helper.createStorageFormatForDate((String)context.get("due-date")));
        this.addPlaceholderToContextMap(context, i18nBean, "due-date", "due-date-placeholder", "decisions.blueprint.template.duedate.placeholder");
        return context;
    }

    private void addPlaceholderToContextMap(BlueprintContext context, I18NBean i18nBean, String field, String placeholderField, String placeholderI18nKey) {
        String fieldValue = (String)context.get(field);
        if (StringUtils.isBlank((CharSequence)fieldValue)) {
            String placeholderText = i18nBean.getText(placeholderI18nKey);
            HashMap soyContext = Maps.newHashMap();
            soyContext.put("placeholderText", placeholderText);
            String placeholder = this.helper.renderFromSoy("com.atlassian.confluence.plugins.confluence-business-blueprints:decisions-resources", "Confluence.Blueprints.Decisions.placeholder.soy", soyContext);
            context.put(placeholderField, (Object)placeholder);
        } else {
            context.put(field, (Object)fieldValue);
        }
    }

    private void addStatusAndOutcomeToContextMap(BlueprintContext context, I18NBean i18nBean) {
        StatusMacroDetails status;
        try {
            status = StatusMacroDetails.valueOf((String)context.get("status"));
            if (status != StatusMacroDetails.GREEN) {
                context.put("final-decision", (Object)"");
            }
        }
        catch (IllegalArgumentException e) {
            status = StatusMacroDetails.getDefault();
        }
        catch (NullPointerException e) {
            status = StatusMacroDetails.getDefault();
        }
        HashMap soyContext = Maps.newHashMap();
        soyContext.put("status", i18nBean.getText(status.getI18nKey()));
        soyContext.put("statusColour", status.getMacroColour());
        String statusTemplate = this.helper.renderFromSoy("com.atlassian.confluence.plugins.confluence-business-blueprints:decisions-resources", "Confluence.Blueprints.Decisions.statusTemplate.soy", soyContext);
        context.put("status", (Object)statusTemplate);
        this.addPlaceholderToContextMap(context, i18nBean, "final-decision", "final-decision-placeholder", "decisions.blueprint.wizard.form.final.decision.placeholder");
    }

    private void addMentionsToContextMap(BlueprintContext context, I18NBean i18nBean, String field, String templateVariable, String placeholderText) {
        String mentions;
        HashMap soyContext = Maps.newHashMap();
        String people = (String)context.get(field);
        if (StringUtils.isNotBlank((CharSequence)people)) {
            String[] userKeys = people.split(",");
            soyContext.put("userKeys", userKeys);
            mentions = this.helper.renderFromSoy("com.atlassian.confluence.plugins.confluence-business-blueprints:decisions-resources", "Confluence.Blueprints.Decisions.mentionXml.soy", soyContext);
        } else {
            soyContext.put("placeholderText", i18nBean.getText(placeholderText));
            mentions = this.helper.renderFromSoy("com.atlassian.confluence.plugins.confluence-business-blueprints:decisions-resources", "Confluence.Blueprints.Decisions.mentionsPlaceholder.soy", soyContext);
        }
        context.put(templateVariable, (Object)mentions);
    }

    public static enum StatusMacroDetails {
        GREY("Grey", "decisions.blueprint.wizard.form.status.open"),
        GREEN("Green", "decisions.blueprint.wizard.form.status.closed"),
        YELLOW("Yellow", "decisions.blueprint.wizard.form.status.progress");

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

