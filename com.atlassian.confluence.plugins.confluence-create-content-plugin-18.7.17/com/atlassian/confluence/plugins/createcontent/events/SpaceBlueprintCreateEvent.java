/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.space.SpaceEvent
 *  com.atlassian.confluence.event.events.types.Created
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.createcontent.events;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.plugins.createcontent.impl.SpaceBlueprint;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Map;

@Deprecated
public class SpaceBlueprintCreateEvent
extends SpaceEvent
implements Created {
    private final SpaceBlueprint spaceBlueprint;
    private final ConfluenceUser creator;
    private final Map<String, Object> context;

    public SpaceBlueprintCreateEvent(Object src, Space space, SpaceBlueprint spaceBlueprint, ConfluenceUser creator, Map<String, Object> context) {
        super(src, space);
        this.spaceBlueprint = spaceBlueprint;
        this.creator = creator;
        this.context = context;
    }

    public Map<String, Object> getContext() {
        return this.context;
    }

    public SpaceBlueprint getSpaceBlueprint() {
        return this.spaceBlueprint;
    }

    public ConfluenceUser getCreator() {
        return this.creator;
    }
}

