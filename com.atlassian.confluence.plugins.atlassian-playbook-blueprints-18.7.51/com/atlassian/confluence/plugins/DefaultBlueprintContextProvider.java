/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.atlassian.plugin.PluginParseException
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins;

import com.atlassian.confluence.plugins.AtlassianPlaybookBlueprintsContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.plugin.PluginParseException;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class DefaultBlueprintContextProvider
extends AbstractBlueprintContextProvider {
    private final AtlassianPlaybookBlueprintsContextProvider helper;
    private String soyTemplateName;
    private String analyticsKey;

    public DefaultBlueprintContextProvider(AtlassianPlaybookBlueprintsContextProvider helper) {
        this.helper = helper;
    }

    public void init(Map<String, String> params) throws PluginParseException {
        this.soyTemplateName = params.get("soyTemplateName");
        this.analyticsKey = params.get("analyticsKey");
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        this.helper.onBlueprintCreated(this.analyticsKey);
        Object pluginResourceKey = (String)context.get("blueprintModuleCompleteKey");
        HashMap soyContext = Maps.newHashMap();
        if (StringUtils.isNotBlank((CharSequence)pluginResourceKey) && StringUtils.isNotBlank((CharSequence)this.soyTemplateName)) {
            String[] resourceString = ((String)pluginResourceKey).split(":");
            pluginResourceKey = resourceString[0] + ":" + resourceString[1].replace("-blueprint", "-resources");
            context.put("templateXML", (Object)this.helper.renderFromSoy((String)pluginResourceKey, this.soyTemplateName, soyContext));
        }
        return context;
    }
}

