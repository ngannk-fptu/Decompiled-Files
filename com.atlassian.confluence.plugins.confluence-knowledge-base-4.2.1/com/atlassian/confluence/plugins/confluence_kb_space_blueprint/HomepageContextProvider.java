/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;

public class HomepageContextProvider
extends AbstractBlueprintContextProvider {
    public static final String LIVESEARCH_MACRO_RESOURCE = "com.atlassian.confluence.plugins.confluence-knowledge-base:space-kb-web-resource";
    public static final String LIVESEARCH_MACRO_TEMPLATE = "Confluence.SpaceBlueprints.KnowledgeBase.livesearchMacro.soy";
    private final TemplateRenderer templateRenderer;

    public HomepageContextProvider(TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        context.put("livesearchMacro", (Object)this.getLivesearchMacro(context));
        return context;
    }

    private String getLivesearchMacro(BlueprintContext context) {
        HashMap soyContext = Maps.newHashMap();
        soyContext.put("spaceKey", context.getSpaceKey());
        StringBuilder output = new StringBuilder();
        this.templateRenderer.renderTo((Appendable)output, LIVESEARCH_MACRO_RESOURCE, LIVESEARCH_MACRO_TEMPLATE, (Map)soyContext);
        return output.toString();
    }
}

