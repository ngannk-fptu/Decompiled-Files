/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.service;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.core.service.ValidationError;
import java.util.Collection;

public class CommandActionHelper {
    private final ServiceCommand command;

    public CommandActionHelper(ServiceCommand command) {
        this.command = command;
    }

    public void validate(ConfluenceActionSupport action) {
        if (!this.command.isAuthorized()) {
            action.addActionError("command.action.auth", new Object[0]);
        } else if (!this.command.isValid()) {
            Collection<ValidationError> validationErrors = this.command.getValidationErrors();
            for (ValidationError e : validationErrors) {
                action.addActionError(e.getMessageKey(), e.getArgs());
            }
        }
    }

    public String execute(ConfluenceActionSupport action) {
        try {
            this.command.execute();
            return "success";
        }
        catch (IllegalStateException e) {
            for (ValidationError error : this.command.getValidationErrors()) {
                action.addActionError(error.getMessageKey(), error.getArgs());
            }
            return "error";
        }
    }

    public Collection getValidationErrors() {
        return this.command.getValidationErrors();
    }

    public boolean isAuthorized() {
        return this.command.isAuthorized();
    }

    public boolean isValid() {
        return this.command.isValid();
    }

    public ServiceCommand getCommand() {
        return this.command;
    }
}

