/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 *  org.apache.commons.collections4.CollectionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.page.MovePageAbstractCommand;
import com.atlassian.confluence.content.service.page.PageLocator;
import com.atlassian.confluence.content.service.space.SpaceLocator;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.core.util.ProgressMeter;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class MovePageToTopOfSpaceCommand
extends MovePageAbstractCommand {
    private static final Logger log = LoggerFactory.getLogger(MovePageToTopOfSpaceCommand.class);
    private final Page sourcePage;
    private final Space targetSpace;

    public MovePageToTopOfSpaceCommand(PageManager pageManager, PermissionManager permissionManager, PageLocator sourcePageLocator, SpaceLocator targetSpaceLocator) {
        super(pageManager, permissionManager);
        this.sourcePage = sourcePageLocator.getPage();
        this.targetSpace = targetSpaceLocator.getSpace();
    }

    @Override
    public Page getPage() {
        return this.sourcePage;
    }

    @Override
    public ProgressMeter getProgressMeter() {
        return new ProgressMeter();
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    protected void executeInternal() {
        if (log.isDebugEnabled()) {
            log.debug("move [ " + this.sourcePage + " ] to the top level of space: [ " + this.targetSpace.getKey() + " ]");
        }
        this.pageManager.movePageToTopLevel(this.sourcePage, this.targetSpace);
    }

    @Override
    protected boolean isAuthorizedInternal() {
        boolean canMove;
        if (this.sourcePage == null || this.targetSpace == null) {
            return false;
        }
        boolean canEditSourcePage = this.permissionManager.hasPermission(this.getCurrentUser(), Permission.EDIT, this.sourcePage);
        boolean canCreatePagesInTargetSpace = this.permissionManager.hasCreatePermission(this.getCurrentUser(), (Object)this.targetSpace, Page.class);
        boolean bl = canMove = canEditSourcePage && canCreatePagesInTargetSpace;
        if (canMove && !this.sourcePage.isDraft() && !this.sourcePage.getSpace().equals(this.targetSpace)) {
            return this.permissionManager.hasPermission(this.getCurrentUser(), Permission.REMOVE, this.sourcePage);
        }
        return canMove;
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        if (this.sourcePage == null) {
            validator.addValidationError("movepage.source.notfound", new Object[0]);
        }
        if (this.targetSpace == null) {
            validator.addValidationError("movepage.target.space.notfound", new Object[0]);
        }
        if (this.sourcePage == null || this.targetSpace == null) {
            return;
        }
        if (this.sourcePage.getSpace() != this.targetSpace) {
            List<String> existPageTitles = this.listOfPermittedPageTitlesAlreadyExist(this.sourcePage, this.targetSpace);
            if (!CollectionUtils.isEmpty(existPageTitles)) {
                String targetSpaceName = this.targetSpace.getName();
                for (String title : existPageTitles) {
                    validator.addValidationError("page.already.exists.inspace", title, targetSpaceName);
                }
            }
            if ((this.sourcePage.hasPermissions("View") || this.sourcePage.hasPermissions("Edit")) && !this.permissionManager.hasPermission(this.getCurrentUser(), Permission.SET_PERMISSIONS, this.targetSpace)) {
                validator.addValidationError("save.restrictions.not.permitted", new Object[0]);
            }
        }
    }
}

