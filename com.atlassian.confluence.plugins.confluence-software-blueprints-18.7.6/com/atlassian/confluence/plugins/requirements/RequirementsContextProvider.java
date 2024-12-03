/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.requirements;

import com.atlassian.confluence.plugins.SoftwareBlueprintsContextProviderHelper;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.HashMap;

public class RequirementsContextProvider
extends AbstractBlueprintContextProvider {
    private static final String DOCUMENT_OWNER = "documentOwner";
    private static final String TEMPLATE_PROVIDER_PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-software-blueprints:requirements-resources";
    private static final String MENTION_TEMPLATE_NAME = "Confluence.Templates.Requirements.userMention.soy";
    private static final String OWNER_KEY = "userKey";
    private TemplateRenderer templateRenderer;
    private final SoftwareBlueprintsContextProviderHelper helper;

    public RequirementsContextProvider(TemplateRenderer templateRenderer, SoftwareBlueprintsContextProviderHelper helper) {
        this.templateRenderer = templateRenderer;
        this.helper = helper;
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        StringBuilder userMention = new StringBuilder();
        HashMap<String, String> contextMap = new HashMap<String, String>();
        if (user != null) {
            contextMap.put(OWNER_KEY, user.getKey().getStringValue());
        }
        this.templateRenderer.renderTo((Appendable)userMention, TEMPLATE_PROVIDER_PLUGIN_KEY, MENTION_TEMPLATE_NAME, contextMap);
        context.put(DOCUMENT_OWNER, (Object)userMention.toString());
        this.helper.publishAnalyticEvent("confluence.software.blueprints.product.requirement.create");
        return context;
    }
}

