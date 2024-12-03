/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.event;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.types.Removing;
import com.atlassian.confluence.spaces.Space;

public class PluginContentWillBeRemovedForSpaceEvent
extends SpaceEvent
implements Removing {
    private static final long serialVersionUID = 963088292810735433L;

    public PluginContentWillBeRemovedForSpaceEvent(Object src, Space space) {
        super(src, space);
    }
}

