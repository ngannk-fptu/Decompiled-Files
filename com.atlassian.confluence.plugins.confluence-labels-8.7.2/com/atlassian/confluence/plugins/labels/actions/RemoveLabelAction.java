/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.Beanable
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.service.ValidationError
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.labels.service.LabelsService
 *  com.atlassian.confluence.labels.service.RemoveLabelCommand
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.labels.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.service.LabelsService;
import com.atlassian.confluence.labels.service.RemoveLabelCommand;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RemoveLabelAction
extends ConfluenceActionSupport
implements Beanable {
    private Map<String, Collection<String>> bean = new HashMap<String, Collection<String>>();
    private long entityId;
    private long labelId;
    private String labelString;
    private LabelsService labelsService;
    private RemoveLabelCommand command;

    public Map<String, Collection<String>> getBean() {
        if (this.bean.isEmpty()) {
            this.bean.put("labels", this.getCommand().getRemovedLabels());
        }
        return this.bean;
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

    private RemoveLabelCommand getCommand() {
        if (this.command == null) {
            this.command = this.labelId != 0L ? this.labelsService.newRemoveLabelCommand(this.labelId, (User)this.getAuthenticatedUser(), this.entityId) : this.labelsService.newRemoveLabelCommand(this.labelString, (User)this.getAuthenticatedUser(), this.entityId);
        }
        return this.command;
    }

    public boolean isPermitted() {
        return this.getCommand().isAuthorized();
    }

    public Labelable getEntity() {
        return this.getCommand().getEntity();
    }

    public long getEntityId() {
        return this.entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public long getLabelId() {
        return this.labelId;
    }

    public void setLabelId(long labelId) {
        this.labelId = labelId;
    }

    public String getLabelString() {
        return this.labelString;
    }

    public void setLabelString(String labelString) {
        this.labelString = labelString;
    }

    public void setLabelsService(LabelsService labelsService) {
        this.labelsService = labelsService;
    }
}

