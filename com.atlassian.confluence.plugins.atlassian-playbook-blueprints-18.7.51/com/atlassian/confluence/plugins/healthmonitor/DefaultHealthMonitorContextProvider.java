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
package com.atlassian.confluence.plugins.healthmonitor;

import com.atlassian.confluence.plugins.AtlassianPlaybookBlueprintsContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.plugin.PluginParseException;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class DefaultHealthMonitorContextProvider
extends AbstractBlueprintContextProvider {
    private final AtlassianPlaybookBlueprintsContextProvider helper;
    private String soyTemplateName;
    private String analyticsKey;
    private String pluginResourceKey = "com.atlassian.confluence.plugins.atlassian-playbook-blueprints:healthmonitor-resources";
    private String randomDateLozengeBase = "randomDateLozenge";
    private String currentDateLozenge = "currentDateLozenge";

    public DefaultHealthMonitorContextProvider(AtlassianPlaybookBlueprintsContextProvider helper) {
        this.helper = helper;
    }

    public void init(Map<String, String> params) throws PluginParseException {
        this.soyTemplateName = params.get("soyTemplateName");
        this.analyticsKey = params.get("analyticsKey");
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        this.helper.onBlueprintCreated(this.analyticsKey);
        return context;
    }

    protected I18NBean getI18nBean() {
        return this.helper.getI18nBean();
    }

    protected String renderFromSoy(Map<String, Object> soyContext) {
        return this.helper.renderFromSoy(this.pluginResourceKey, this.soyTemplateName, soyContext);
    }

    protected void addMentionsToContextMap(BlueprintContext context, Map<String, Object> templateContext, String field, String variableCondition, String placeholderI18NKey) {
        String people = (String)context.get(field);
        if (StringUtils.isNotBlank((CharSequence)people)) {
            String[] names = people.split(",");
            templateContext.put(variableCondition, true);
            templateContext.put(field, names);
        } else {
            templateContext.put(variableCondition, false);
            templateContext.put(field, this.getI18nBean().getText(placeholderI18NKey));
        }
    }

    protected void addTextToContextMap(BlueprintContext context, Map<String, Object> templateContext, String field, String templateVariable, String placeholderI18NKey) {
        String text = (String)context.get(field);
        boolean hasVariable = true;
        if (StringUtils.isBlank((CharSequence)text)) {
            text = this.getI18nBean().getText(placeholderI18NKey);
            hasVariable = false;
        }
        templateContext.put(field, text);
        templateContext.put(templateVariable, hasVariable);
    }

    protected void addCadenceToContextMap(BlueprintContext context, Map<String, Object> templateContext, String placeholderI18NKey) {
        String cadence = (String)context.get("cadence");
        boolean hasCadence = true;
        if (cadence.equals("Other")) {
            cadence = (String)context.get("other-cadence");
        }
        if (StringUtils.isBlank((CharSequence)cadence)) {
            hasCadence = false;
            cadence = this.getI18nBean().getText(placeholderI18NKey);
        }
        templateContext.put("cadence", cadence);
        templateContext.put("hasCadence", hasCadence);
    }

    protected void addRandomDateLozengesToContextMap(Map<String, Object> context, int number) {
        for (int i = 1; i <= number; ++i) {
            context.put(this.randomDateLozengeBase + i, this.helper.createStorageFormatAroundToday());
        }
    }

    protected void addCurrentDateLozengeToContextMap(Map<String, Object> context) {
        context.put(this.currentDateLozenge, this.helper.createStorageFormatForToday());
    }

    private String getXMLStringFromSoy(String field, Object fieldValue, String soyTemplate) {
        HashMap soyContext = Maps.newHashMap();
        soyContext.put(field, fieldValue);
        return this.helper.renderFromSoy(this.pluginResourceKey, soyTemplate, soyContext);
    }
}

