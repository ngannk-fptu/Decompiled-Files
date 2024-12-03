/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.labels.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.service.LabelsService;
import com.atlassian.confluence.labels.service.RemoveLabelCommand;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RemoveFavouriteAction
extends ConfluenceActionSupport
implements Beanable {
    private Map<String, Collection<String>> bean = new HashMap<String, Collection<String>>();
    private long entityId;
    private LabelsService labelsService;
    private RemoveLabelCommand command;
    private RemoveLabelCommand americanCommand;

    @Override
    public Map<String, Collection<String>> getBean() {
        if (this.bean.isEmpty()) {
            this.getCommand().getRemovedLabels().addAll(this.getAmericanCommand().getRemovedLabels());
            this.bean.put("labels", this.getCommand().getRemovedLabels());
        }
        return this.bean;
    }

    public String execute() {
        if (!this.runCommand(this.getCommand())) {
            return "error";
        }
        if (!this.runCommand(this.getAmericanCommand())) {
            return "error";
        }
        return "success";
    }

    private boolean runCommand(RemoveLabelCommand command) {
        if (!command.isValid()) {
            for (ValidationError error : command.getValidationErrors()) {
                this.addActionError(error.getMessageKey(), error.getArgs());
            }
            return false;
        }
        command.execute();
        return true;
    }

    private RemoveLabelCommand getCommand() {
        if (this.command == null) {
            Label favouriteLabel = new Label("favourite", Namespace.PERSONAL, this.getAuthenticatedUser());
            this.command = this.labelsService.newRemoveLabelCommand(favouriteLabel, (User)this.getAuthenticatedUser(), this.entityId);
        }
        return this.command;
    }

    private RemoveLabelCommand getAmericanCommand() {
        if (this.americanCommand == null) {
            Label americanSpelling = new Label("favorite", Namespace.PERSONAL, this.getAuthenticatedUser());
            this.americanCommand = this.labelsService.newRemoveLabelCommand(americanSpelling, (User)this.getAuthenticatedUser(), this.entityId);
        }
        return this.americanCommand;
    }

    @Override
    public boolean isPermitted() {
        return this.getCommand().isAuthorized();
    }

    public Labelable getEntity() {
        return this.getCommand().getEntity();
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public void setLabelsService(LabelsService labelsService) {
        this.labelsService = labelsService;
    }
}

