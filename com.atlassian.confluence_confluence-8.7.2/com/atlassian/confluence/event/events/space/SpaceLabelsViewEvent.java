/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.space;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.types.Viewed;
import com.atlassian.confluence.spaces.Space;

public class SpaceLabelsViewEvent
extends SpaceEvent
implements Viewed {
    private static final long serialVersionUID = 8012166972896270682L;

    public SpaceLabelsViewEvent(Object src, Space space) {
        super(src, space);
    }
}

