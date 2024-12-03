/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked;
import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.content.service.SpaceService;
import com.atlassian.confluence.content.service.page.MovePageCommandHelper;
import com.atlassian.confluence.content.service.page.PageLocator;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.pages.actions.AbstractCommandAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@ReadOnlyAccessBlocked
public class MovePageAction
extends AbstractCommandAction {
    private PageService pageService;
    private SpaceService spaceService;
    private MovePageCommandHelper movePageCommandHelper;
    private long pageId;
    private long targetId;
    private String position;
    private String spaceKey;
    private String targetTitle;
    private String mode = MovePageCommandHelper.MovePageMode.ASYNC.name();

    @Override
    @PermittedMethods(value={HttpMethod.POST})
    public String execute() {
        return super.execute();
    }

    @Override
    protected ServiceCommand createCommand() {
        PageLocator sourcePageLocator = this.pageService.getIdPageLocator(this.pageId);
        if ("topLevel".equals(this.position)) {
            assert (this.spaceKey != null);
            return this.movePageCommandHelper.newMovePageCommand(sourcePageLocator, this.spaceService.getKeySpaceLocator(this.spaceKey), MovePageCommandHelper.MovePageMode.valueOf(this.mode));
        }
        PageLocator targetPageLocator = this.targetId != 0L ? this.pageService.getIdPageLocator(this.targetId) : this.pageService.getTitleAndSpaceKeyPageLocator(this.spaceKey, this.targetTitle);
        return this.movePageCommandHelper.newMovePageCommand(sourcePageLocator, targetPageLocator, this.position, MovePageCommandHelper.MovePageMode.valueOf(this.mode));
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public void setTargetTitle(String targetTitle) {
        this.targetTitle = targetTitle;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setPageService(PageService pageService) {
        this.pageService = pageService;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public void setSpaceService(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public void setMovePageCommandHelper(MovePageCommandHelper movePageCommandHelper) {
        this.movePageCommandHelper = movePageCommandHelper;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}

