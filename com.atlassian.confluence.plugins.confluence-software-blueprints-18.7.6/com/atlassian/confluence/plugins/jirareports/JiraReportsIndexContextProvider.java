/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.atlassian.core.util.XMLUtils
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.jirareports;

import com.atlassian.confluence.plugins.SoftwareBlueprintsContextProviderHelper;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.core.util.XMLUtils;
import com.google.common.collect.Maps;
import java.util.HashMap;

public class JiraReportsIndexContextProvider
extends AbstractBlueprintContextProvider {
    private SoftwareBlueprintsContextProviderHelper helper;
    private static final String SOY_CREATE_FROM_TEMPLATE_MACRO = "Confluence.Blueprints.JiraReports.Template.createFromTemplateMacro.soy";

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        String spaceKey = XMLUtils.escape((String)context.getSpaceKey());
        String blueprintKey = XMLUtils.escape((String)context.getBlueprintModuleCompleteKey().getCompleteKey());
        context.put("createFromTemplateMacro", (Object)this.getCreateFromTemplateMacro(blueprintKey, spaceKey));
        return context;
    }

    private String getCreateFromTemplateMacro(String blueprintKey, String spaceKey) {
        HashMap templateContext = Maps.newHashMap();
        templateContext.put("blueprintKey", blueprintKey);
        templateContext.put("spaceKey", spaceKey);
        return this.helper.renderFromSoy("com.atlassian.confluence.plugins.confluence-software-blueprints:jirareports-resources", SOY_CREATE_FROM_TEMPLATE_MACRO, templateContext);
    }

    public void setHelper(SoftwareBlueprintsContextProviderHelper helper) {
        this.helper = helper;
    }
}

