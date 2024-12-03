/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.page.PageLocator;
import com.atlassian.confluence.core.service.AbstractServiceCommand;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RevertPageOrderCommand
extends AbstractServiceCommand {
    private static final Logger log = LoggerFactory.getLogger(RevertPageOrderCommand.class);
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private Page parentPage;

    public RevertPageOrderCommand(PageManager pageManager, PermissionManager permissionManager, PageLocator parentPageLocator) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.parentPage = parentPageLocator.getPage();
    }

    @Override
    protected void executeInternal() {
        if (log.isDebugEnabled()) {
            log.debug("revert child page order for [ " + this.parentPage + " ]");
        }
        this.pageManager.revertChildPageOrder(this.parentPage);
    }

    @Override
    protected boolean isAuthorizedInternal() {
        if (this.parentPage == null) {
            return false;
        }
        if (!this.permissionManager.hasPermission(this.getCurrentUser(), Permission.EDIT, this.parentPage)) {
            return false;
        }
        List<Page> childPages = this.parentPage.getChildren();
        for (Page child : childPages) {
            if (this.permissionManager.hasPermission(this.getCurrentUser(), Permission.EDIT, child)) continue;
            return false;
        }
        return true;
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        if (this.parentPage == null) {
            validator.addValidationError("parent.page.doesnt.exist", new Object[0]);
        }
    }
}

