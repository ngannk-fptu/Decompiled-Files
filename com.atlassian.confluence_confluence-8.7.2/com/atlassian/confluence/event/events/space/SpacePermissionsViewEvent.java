/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.space;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.types.Viewed;
import com.atlassian.confluence.spaces.Space;

public class SpacePermissionsViewEvent
extends SpaceEvent
implements Viewed {
    private static final long serialVersionUID = -1616504766027006373L;

    public SpacePermissionsViewEvent(Object src, Space space) {
        super(src, space);
    }
}

