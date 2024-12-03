/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.space;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.spaces.Space;

public class SpaceTrashEmptyEvent
extends SpaceEvent
implements Removed {
    private static final long serialVersionUID = 4543821181056799414L;

    public SpaceTrashEmptyEvent(Object src, Space space) {
        super(src, space);
    }
}

