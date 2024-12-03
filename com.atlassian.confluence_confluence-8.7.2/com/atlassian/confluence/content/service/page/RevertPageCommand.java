/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.page.PageLocator;
import com.atlassian.confluence.content.service.page.RevertContentToVersionCommand;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.AbstractServiceCommand;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;

public class RevertPageCommand
extends AbstractServiceCommand {
    private final PageManager pageManager;
    private final PageLocator pageLocator;
    private final int version;
    private final String revertComment;
    private final boolean revertTitle;
    private Page page;
    private final RevertContentToVersionCommand delegate;

    public RevertPageCommand(PageManager pageManager, PermissionManager permissionManager, PageLocator pageLocator, String revertComment, int version, boolean revertTitle) {
        this.pageManager = pageManager;
        this.pageLocator = pageLocator;
        this.revertComment = revertComment;
        this.version = version;
        this.revertTitle = revertTitle;
        this.delegate = new RevertContentToVersionCommand(permissionManager, pageManager);
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        this.delegate.validate(validator, this.getPage(), this.getPossibleConflict(), this.version, this.revertTitle);
    }

    @Override
    protected boolean isAuthorizedInternal() {
        return this.delegate.isAuthorized(this.getCurrentUser(), this.getPage());
    }

    @Override
    protected void executeInternal() {
        this.delegate.execute(this.getPage(), this.version, this.revertComment, this.revertTitle);
    }

    private String getSpaceKey() {
        return this.getPage().getSpaceKey();
    }

    private AbstractPage getPage() {
        if (this.page == null && this.pageLocator != null) {
            this.page = this.pageLocator.getPage();
        }
        return this.page;
    }

    private Page getPossibleConflict() {
        ContentEntityObject oldVersion = this.delegate.getVersionToRevert(this.getPage(), this.version);
        if (oldVersion != null) {
            return this.pageManager.getPage(this.getSpaceKey(), oldVersion.getTitle());
        }
        return null;
    }
}

