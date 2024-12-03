/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 */
package com.atlassian.confluence.event.events.space;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.types.Removing;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.core.util.ProgressMeter;

public class SpaceWillRemoveEvent
extends SpaceEvent
implements Removing {
    private static final long serialVersionUID = -6111739362823662340L;
    private final ProgressMeter progressMeter;

    public SpaceWillRemoveEvent(Object src, Space removedSpace, ProgressMeter progressMeter) {
        super(src, removedSpace);
        this.progressMeter = progressMeter;
    }

    public ProgressMeter getProgressMeter() {
        return this.progressMeter;
    }
}

