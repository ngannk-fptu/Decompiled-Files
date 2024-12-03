/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.healthmonitor;

import com.atlassian.confluence.plugins.AtlassianPlaybookBlueprintsContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.plugins.healthmonitor.DefaultHealthMonitorContextProvider;
import com.google.common.collect.Maps;
import java.util.HashMap;

public class ProjectMonitorContextProvider
extends DefaultHealthMonitorContextProvider {
    public ProjectMonitorContextProvider(AtlassianPlaybookBlueprintsContextProvider helper) {
        super(helper);
    }

    @Override
    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        super.updateBlueprintContext(context);
        HashMap soyContext = Maps.newHashMap();
        this.addCurrentDateLozengeToContextMap(soyContext);
        this.addRandomDateLozengesToContextMap(soyContext, 3);
        this.addTextToContextMap(context, soyContext, "name", "hasName", "projectmonitor.blueprint.template.project.name.placeholder");
        this.addMentionsToContextMap(context, soyContext, "owner", "hasOwner", "projectmonitor.blueprint.template.project.owner.placeholder");
        this.addMentionsToContextMap(context, soyContext, "sponsor", "hasSponsor", "projectmonitor.blueprint.template.project.sponsor.placeholder");
        this.addCadenceToContextMap(context, soyContext, "projectmonitor.blueprint.template.project.cadence.placeholder");
        context.put("projectMonitorTemplateXML", (Object)this.renderFromSoy(soyContext));
        return context;
    }
}

