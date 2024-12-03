/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.space.SpaceAdminViewEvent;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction;

public class SpaceAdminAction
extends AbstractSpaceAdminAction
implements Evented<SpaceAdminViewEvent> {
    @Override
    public SpaceAdminViewEvent getEventToPublish(String result) {
        return new SpaceAdminViewEvent(this, this.getSpace());
    }
}

