/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 */
package com.atlassian.confluence.event.events.space;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.core.util.ProgressMeter;

public class SpaceRemoveEvent
extends SpaceEvent
implements Removed {
    private static final long serialVersionUID = 7429087390324189054L;
    private final ProgressMeter progressMeter;

    public SpaceRemoveEvent(Object src, Space removedSpace, ProgressMeter progressMeter) {
        super(src, removedSpace);
        this.progressMeter = progressMeter;
    }

    public ProgressMeter getProgressMeter() {
        return this.progressMeter;
    }
}

