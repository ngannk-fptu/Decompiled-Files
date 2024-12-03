/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.createcontent.api.events;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.plugins.createcontent.impl.SpaceBlueprint;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Map;

@PublicApi
public class SpaceBlueprintHomePageCreateEvent
extends com.atlassian.confluence.plugins.createcontent.events.SpaceBlueprintHomePageCreateEvent {
    public SpaceBlueprintHomePageCreateEvent(Object src, Space space, SpaceBlueprint spaceBlueprint, ConfluenceUser creator, Map<String, Object> context) {
        super(src, space, spaceBlueprint, creator, context);
    }
}

