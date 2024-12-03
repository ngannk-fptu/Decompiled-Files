/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.core.service.AbstractServiceCommand;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;

public abstract class RemoveAbstractPageVersionCommand
extends AbstractServiceCommand {
    private final PageManager pageManager;
    private final PermissionManager permissionManager;

    public RemoveAbstractPageVersionCommand(PageManager pageManager, PermissionManager permissionManager) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        AbstractPage originalVersion;
        AbstractPage page = this.getPage();
        if (this.getPage() == null) {
            validator.addValidationError("page.doesnt.exist", new Object[0]);
        }
        if ((originalVersion = page.getOriginalVersionPage()).isDeleted()) {
            validator.addValidationError("page.is.deleted", new Object[0]);
        }
    }

    @Override
    protected boolean isAuthorizedInternal() {
        if (this.getPage() == null) {
            return false;
        }
        Space space = this.getPage().getOriginalVersionPage().getSpace();
        return space != null && this.permissionManager.hasPermission(this.getCurrentUser(), Permission.ADMINISTER, space);
    }

    @Override
    protected void executeInternal() {
        this.pageManager.removeHistoricalVersion(this.getPage());
    }

    protected abstract AbstractPage getPage();
}

