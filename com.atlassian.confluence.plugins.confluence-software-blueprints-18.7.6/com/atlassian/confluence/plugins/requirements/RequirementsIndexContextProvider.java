/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.plugins.requirements;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class RequirementsIndexContextProvider
extends AbstractBlueprintContextProvider {
    private TemplateRenderer templateRenderer;

    public RequirementsIndexContextProvider(TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        ImmutableMap soyContext = ImmutableMap.of((Object)"blueprintKey", (Object)context.getBlueprintModuleCompleteKey().getCompleteKey(), (Object)"spaceKey", (Object)context.getSpaceKey(), (Object)"createFromTemplateLabel", (Object)context.getCreateFromTemplateLabel());
        StringBuilder createFromTemplateMacro = new StringBuilder();
        this.templateRenderer.renderTo((Appendable)createFromTemplateMacro, "com.atlassian.confluence.plugins.confluence-software-blueprints:requirements-resources", "Confluence.Blueprints.Requirements.createFromTemplateMacro.soy", (Map)soyContext);
        context.put("createFromTemplateMacro", (Object)createFromTemplateMacro.toString());
        return context;
    }
}

