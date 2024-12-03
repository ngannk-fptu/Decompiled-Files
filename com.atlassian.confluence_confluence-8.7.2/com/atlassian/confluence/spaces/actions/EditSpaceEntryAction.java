/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.space.SpaceDetailsViewEvent;
import com.atlassian.confluence.spaces.actions.AbstractEditSpaceAction;

public class EditSpaceEntryAction
extends AbstractEditSpaceAction
implements Evented<SpaceDetailsViewEvent> {
    public void setSpaceManager() {
    }

    @Override
    public SpaceDetailsViewEvent getEventToPublish(String result) {
        return new SpaceDetailsViewEvent(this, this.getSpace());
    }
}

