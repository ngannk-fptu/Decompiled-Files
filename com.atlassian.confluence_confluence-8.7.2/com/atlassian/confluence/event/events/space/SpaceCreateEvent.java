/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.space;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.spaces.Space;

public class SpaceCreateEvent
extends SpaceEvent
implements Created {
    private static final long serialVersionUID = 8399531812047890333L;

    public SpaceCreateEvent(Object src, Space space) {
        super(src, space);
    }
}

