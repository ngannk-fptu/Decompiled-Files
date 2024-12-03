/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.space.SpaceLabelsViewEvent;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.SpaceLabelManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction;
import com.atlassian.confluence.spaces.actions.SpaceLabelAware;
import java.util.List;

public class EditSpaceLabelAction
extends AbstractSpaceAdminAction
implements SpaceLabelAware,
Evented<SpaceLabelsViewEvent> {
    private SpaceLabelManager spaceLabelManager;

    public SpaceLabelManager getSpaceLabelManager() {
        return this.spaceLabelManager;
    }

    public void setSpaceLabelManager(SpaceLabelManager spaceLabelManager) {
        this.spaceLabelManager = spaceLabelManager;
    }

    @Override
    public List getTeamLabelsOnThisSpace() {
        return this.getSpaceLabelManager().getTeamLabelsOnSpace(this.getSpace().getKey());
    }

    @Override
    public List getLabelsOnThisSpace() {
        return this.getSpaceLabelManager().getLabelsOnSpace(this.getSpace());
    }

    public List getAvailableTeamLabels() {
        return this.getSpaceLabelManager().getAvailableTeamLabels(this.getSpace().getKey());
    }

    public List getSuggestedLabelsForSpace() throws Exception {
        return this.getSpaceLabelManager().getSuggestedLabelsForSpace(this.getSpace(), this.getAuthenticatedUser());
    }

    public Label addLabel(Space space, String labelName) {
        return this.spaceLabelManager.addLabel(space, labelName);
    }

    @Override
    public SpaceLabelsViewEvent getEventToPublish(String result) {
        return new SpaceLabelsViewEvent(this, this.getSpace());
    }
}

