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
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetPageOrderCommand
extends AbstractServiceCommand {
    private static final Logger log = LoggerFactory.getLogger(SetPageOrderCommand.class);
    private PageManager pageManager;
    private PermissionManager permissionManager;
    private Page parentPage;
    private List<Long> childPageIds;

    public SetPageOrderCommand(PageManager pageManager, PermissionManager permissionManager, PageLocator parentPageLocator, List<Long> childPageIds) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.childPageIds = childPageIds;
        this.parentPage = parentPageLocator.getPage();
    }

    @Override
    protected void executeInternal() {
        if (log.isDebugEnabled()) {
            log.debug("set child page order for [ " + this.parentPage + " ]");
        }
        this.pageManager.setChildPageOrder(this.parentPage, this.childPageIds);
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
            return;
        }
        if (this.childPageIds == null) {
            validator.addValidationError("setpageorder.children.not.present", new Object[0]);
            return;
        }
        if (!this.hasMatchingChildIds(this.parentPage.getChildren(), this.childPageIds)) {
            validator.addValidationError("setpageorder.children.mismatch", new Object[0]);
        }
    }

    private boolean hasMatchingChildIds(List<Page> childPages, List<Long> argumentIds) {
        if (childPages.size() != argumentIds.size()) {
            return false;
        }
        ArrayList<Long> workingList = new ArrayList<Long>(argumentIds);
        for (Page childPage : childPages) {
            if (workingList.remove(childPage.getId())) continue;
            return false;
        }
        return workingList.isEmpty();
    }

    public long getParentPageId() {
        return this.parentPage.getId();
    }

    public List<Long> getChildPageIds() {
        return this.childPageIds;
    }
}

