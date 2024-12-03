/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.space;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.spaces.Space;

public class SpaceUpdateEvent
extends SpaceEvent
implements Updated {
    private static final long serialVersionUID = -7257578803828913813L;
    Space originalSpace;

    public SpaceUpdateEvent(Object src, Space updatedSpace, Space originalSpace) {
        super(src, updatedSpace);
        this.originalSpace = originalSpace;
    }

    public SpaceUpdateEvent(Object src, Space updatedSpace) {
        super(src, updatedSpace);
    }

    public Space getOriginalSpace() {
        return this.originalSpace;
    }
}

