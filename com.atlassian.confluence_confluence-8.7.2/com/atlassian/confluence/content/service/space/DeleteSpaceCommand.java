/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.space;

import com.atlassian.confluence.content.service.space.SpaceLocator;
import com.atlassian.confluence.core.service.AbstractServiceCommand;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;

public class DeleteSpaceCommand
extends AbstractServiceCommand {
    private final SpaceManager spaceManager;
    private final PermissionManager permissionManager;
    private final SpaceLocator spaceLocator;

    public DeleteSpaceCommand(SpaceManager spaceManager, PermissionManager permissionManager, SpaceLocator spaceLocator) {
        this.spaceManager = spaceManager;
        this.permissionManager = permissionManager;
        this.spaceLocator = spaceLocator;
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        if (this.getSpace() == null) {
            validator.addValidationError("space.doesnt.exist", new Object[0]);
        }
    }

    @Override
    protected boolean isAuthorizedInternal() {
        return this.getSpace() == null || this.permissionManager.hasPermission(this.getCurrentUser(), Permission.REMOVE, this.getSpace());
    }

    @Override
    protected void executeInternal() {
        this.spaceManager.removeSpace(this.getSpace());
    }

    public Space getSpace() {
        return this.spaceLocator.getSpace();
    }
}

