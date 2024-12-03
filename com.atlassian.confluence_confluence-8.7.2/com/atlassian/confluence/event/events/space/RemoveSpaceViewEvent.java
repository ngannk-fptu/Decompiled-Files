/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.space;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.types.Viewed;
import com.atlassian.confluence.spaces.Space;

public class RemoveSpaceViewEvent
extends SpaceEvent
implements Viewed {
    private static final long serialVersionUID = 3878060495167672272L;

    public RemoveSpaceViewEvent(Object obj, Space space) {
        super(obj, space);
    }
}

