/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 */
package com.atlassian.confluence.plugins.retrospectives;

import com.atlassian.confluence.plugins.SoftwareBlueprintsContextProviderHelper;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;

public class RetrospectivesIndexContextProvider
extends AbstractBlueprintContextProvider {
    private SoftwareBlueprintsContextProviderHelper helper;

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        String buttonLabel = this.helper.getText("retrospectives.blueprint.index.template.button.label");
        context.put("createFromTemplateMacro", (Object)this.helper.getCreateFromTemplateMacro(context, buttonLabel, "com.atlassian.confluence.plugins.confluence-software-blueprints:retrospective-resources"));
        return context;
    }

    public void setHelper(SoftwareBlueprintsContextProviderHelper helper) {
        this.helper = helper;
    }
}

