/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.Beanable
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.service.ValidationError
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.labels.service.AddLabelsCommand
 *  com.atlassian.confluence.labels.service.LabelsService
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.labels.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.service.AddLabelsCommand;
import com.atlassian.confluence.labels.service.LabelsService;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class AddLabelAction
extends ConfluenceActionSupport
implements Beanable {
    private long entityId;
    private String labelString;
    private String entityType;
    private LabelsService labelsService;
    private AddLabelsCommand command;

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

    public boolean isPermitted() {
        return this.getCommand().isAuthorized();
    }

    protected AddLabelsCommand getCommand() {
        if (this.command == null) {
            this.command = this.labelsService.newAddLabelCommand(this.labelString, (User)this.getAuthenticatedUser(), this.entityId);
        }
        return this.command;
    }

    public Labelable getEntity() {
        return this.getCommand().getEntity();
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public void setLabelString(String labelString) {
        this.labelString = labelString;
    }

    public void setLabelsService(LabelsService labelsService) {
        this.labelsService = labelsService;
    }

    public void setEntityType(String type) {
        this.entityType = type;
    }

    public String getEntityType() {
        return this.entityType;
    }
}

