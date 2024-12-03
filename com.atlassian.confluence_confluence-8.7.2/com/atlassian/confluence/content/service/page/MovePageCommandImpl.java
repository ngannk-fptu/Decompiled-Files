/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 *  org.apache.commons.collections4.CollectionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.page.MovePageAbstractCommand;
import com.atlassian.confluence.content.service.page.PageLocator;
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

public class MovePageCommandImpl
extends MovePageAbstractCommand {
    private static final Logger log = LoggerFactory.getLogger(MovePageCommandImpl.class);
    private final Page sourcePage;
    private final long sourcePageId;
    private final Page targetPage;
    private final long targetPageId;
    private final String movePoint;

    public MovePageCommandImpl(PageManager pageManager, PermissionManager permissionManager, PageLocator sourcePageLocator, PageLocator targetPageLocator, String movePoint) {
        super(pageManager, permissionManager);
        this.sourcePage = sourcePageLocator.getPage();
        this.sourcePageId = this.sourcePage != null ? this.sourcePage.getId() : 0L;
        this.targetPage = targetPageLocator.getPage();
        this.targetPageId = this.targetPage != null ? this.targetPage.getId() : 0L;
        this.movePoint = movePoint;
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        if (this.sourcePage == null) {
            validator.addValidationError("movepage.source.notfound", new Object[0]);
        }
        if (this.targetPage == null) {
            validator.addValidationError("movepage.target.notfound", new Object[0]);
        }
        if (!(this.movePoint.equals("append") || this.movePoint.equals("above") || this.movePoint.equals("below"))) {
            validator.addValidationError("movepage.point.invalid", this.movePoint);
        }
        if (this.sourcePage == null || this.targetPage == null) {
            return;
        }
        if (this.sourcePage.getSpace() != this.targetPage.getSpace()) {
            Space targetSpace = this.targetPage.getSpace();
            List<String> existPageTitles = this.listOfPermittedPageTitlesAlreadyExist(this.sourcePage, targetSpace);
            if (!CollectionUtils.isEmpty(existPageTitles)) {
                String targetSpaceName = targetSpace.getName();
                for (String title : existPageTitles) {
                    validator.addValidationError("page.already.exists.inspace", title, targetSpaceName);
                }
            }
            if ((this.sourcePage.hasPermissions("View") || this.sourcePage.hasPermissions("Edit")) && !this.permissionManager.hasPermission(this.getCurrentUser(), Permission.SET_PERMISSIONS, this.targetPage)) {
                validator.addValidationError("save.restrictions.not.permitted", new Object[0]);
            }
        }
        if ("append".equals(this.movePoint)) {
            if (this.targetPage.getAncestors().contains(this.sourcePage)) {
                validator.addValidationError("movepage.target.invalid", new Object[0]);
            }
        } else if (this.targetPage.getParent() != null && this.targetPage.getParent().getAncestors().contains(this.sourcePage)) {
            validator.addValidationError("movepage.target.invalid", new Object[0]);
        }
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
    protected boolean isAuthorizedInternal() {
        if (this.sourcePage == null || this.targetPage == null) {
            return false;
        }
        return this.permissionManager.hasMovePermission(this.getCurrentUser(), this.sourcePage, this.targetPage, this.movePoint);
    }

    @Override
    protected void executeInternal() {
        Page pageToMove = this.pageManager.getPage(this.sourcePageId);
        if (pageToMove == null) {
            log.error("Could not find the page to move with ID {}", (Object)this.sourcePageId);
            return;
        }
        Page destinationPage = this.pageManager.getPage(this.targetPageId);
        if (destinationPage == null) {
            log.error("The destination page with ID {} is not found", (Object)this.targetPageId);
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("move [ {} ] to [ {} ] at [ {} ]", new Object[]{pageToMove.getTitle(), destinationPage.getTitle(), this.movePoint});
        }
        if ("below".equals(this.movePoint)) {
            this.pageManager.movePageAfter(pageToMove, destinationPage);
        } else if ("above".equals(this.movePoint)) {
            this.pageManager.movePageBefore(pageToMove, destinationPage);
        } else if (!(destinationPage.hasChildren() && destinationPage.getChildren().contains(pageToMove) || !"append".equals(this.movePoint))) {
            this.pageManager.movePageAsChild(pageToMove, destinationPage);
        }
    }
}

