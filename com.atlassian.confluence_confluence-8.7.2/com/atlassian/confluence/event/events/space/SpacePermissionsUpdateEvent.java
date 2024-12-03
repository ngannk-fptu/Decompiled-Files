/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.space;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.spaces.Space;

public class SpacePermissionsUpdateEvent
extends SpaceEvent
implements Updated {
    private static final long serialVersionUID = -1591695485644385452L;

    public SpacePermissionsUpdateEvent(Object src, Space space) {
        super(src, space);
    }
}

