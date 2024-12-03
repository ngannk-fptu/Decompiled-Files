/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.page.PageLocator;
import com.atlassian.confluence.core.DefaultDeleteContext;
import com.atlassian.confluence.core.service.AbstractServiceCommand;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;

public class DeletePageCommand
extends AbstractServiceCommand {
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final PageLocator pageLocator;

    public DeletePageCommand(PageManager pageManager, PermissionManager permissionManager, PageLocator pageLocator) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.pageLocator = pageLocator;
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        if (this.getPage() == null) {
            validator.addValidationError("page.doesnt.exist", new Object[0]);
        }
    }

    @Override
    protected boolean isAuthorizedInternal() {
        return this.getPage() == null || this.permissionManager.hasPermission(this.getCurrentUser(), Permission.REMOVE, this.getPage());
    }

    @Override
    protected void executeInternal() {
        this.pageManager.trashPage(this.getPage(), DefaultDeleteContext.DEFAULT);
    }

    private Page getPage() {
        return this.pageLocator.getPage();
    }
}

