/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.labels.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.service.AddLabelsCommand;
import com.atlassian.confluence.labels.service.LabelsService;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class AddSpaceToFavouriteAjaxAction
extends ConfluenceActionSupport
implements Beanable,
SpaceAware {
    private long entityId;
    private LabelsService labelsService;
    private AddLabelsCommand command;
    private Space space;

    @Override
    public Map<String, Collection<Label>> getBean() {
        return Collections.singletonMap("labels", this.getCommand().getAddedLabels());
    }

    public String execute() {
        if (!this.getCommand().isValid()) {
            for (ValidationError error : this.getCommand().getValidationErrors()) {
                this.addActionError(error.getMessageKey(), error.getArgs());
            }
            return "error";
        }
        this.getCommand().execute();
        return "success";
    }

    @Override
    public boolean isPermitted() {
        return this.getCommand().isAuthorized();
    }

    public Labelable getEntity() {
        return this.getCommand().getEntity();
    }

    protected AddLabelsCommand getCommand() {
        if (this.command == null) {
            Label favouriteLabel = new Label("favourite", Namespace.PERSONAL, this.getAuthenticatedUser().getName());
            String labelString = favouriteLabel.toStringWithNamespace();
            this.command = this.labelsService.newAddLabelCommand(labelString, this.getAuthenticatedUser(), this.getEntityId());
        }
        return this.command;
    }

    private long getEntityId() {
        if (this.entityId == 0L) {
            this.entityId = this.space.getId();
        }
        return this.entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public boolean isSpaceRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    @Override
    public Space getSpace() {
        return this.space;
    }

    public void setLabelsService(LabelsService labelsService) {
        this.labelsService = labelsService;
    }
}

