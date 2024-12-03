/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.space;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.types.Viewed;
import com.atlassian.confluence.spaces.Space;

public class SpaceDetailsViewEvent
extends SpaceEvent
implements Viewed {
    private static final long serialVersionUID = -6984673708584562973L;

    public SpaceDetailsViewEvent(Object src, Space space) {
        super(src, space);
    }
}

