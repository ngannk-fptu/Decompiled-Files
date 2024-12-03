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

public class LeadershipMonitorContextProvider
extends DefaultHealthMonitorContextProvider {
    public LeadershipMonitorContextProvider(AtlassianPlaybookBlueprintsContextProvider helper) {
        super(helper);
    }

    @Override
    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        super.updateBlueprintContext(context);
        HashMap soyContext = Maps.newHashMap();
        this.addCurrentDateLozengeToContextMap(soyContext);
        this.addRandomDateLozengesToContextMap(soyContext, 4);
        this.addTextToContextMap(context, soyContext, "team", "hasTeam", "leadershipmonitor.blueprint.template.team.placeholder");
        this.addMentionsToContextMap(context, soyContext, "sponsor", "hasSponsor", "leadershipmonitor.blueprint.template.sponsor.placeholder");
        this.addCadenceToContextMap(context, soyContext, "leadershipmonitor.blueprint.template.cadence.placeholder");
        context.put("leadershipMonitorTemplateXML", (Object)this.renderFromSoy(soyContext));
        return context;
    }
}

