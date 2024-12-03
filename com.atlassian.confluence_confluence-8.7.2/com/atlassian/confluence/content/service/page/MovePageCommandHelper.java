/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.page.LongRunningTaskMovePageCommandDecorator;
import com.atlassian.confluence.content.service.page.MovePageCommand;
import com.atlassian.confluence.content.service.page.MovePageCommandImpl;
import com.atlassian.confluence.content.service.page.MovePageToTopOfSpaceCommand;
import com.atlassian.confluence.content.service.page.PageLocator;
import com.atlassian.confluence.content.service.space.SpaceLocator;
import com.atlassian.confluence.internal.longrunning.LongRunningTaskManagerInternal;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.spring.container.ContainerManager;
import java.util.ArrayList;
import java.util.List;

public class MovePageCommandHelper {
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final LongRunningTaskManagerInternal longRunningTaskManager;

    @Deprecated
    public MovePageCommandHelper(PageManager pageManager) {
        this.pageManager = pageManager;
        this.longRunningTaskManager = (LongRunningTaskManagerInternal)ContainerManager.getComponent((String)"longRunningTaskManager");
        this.permissionManager = (PermissionManager)ContainerManager.getComponent((String)"permissionManager");
    }

    public MovePageCommandHelper(PageManager pageManager, PermissionManager permissionManager, LongRunningTaskManagerInternal longRunningTaskManager) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.longRunningTaskManager = longRunningTaskManager;
    }

    public boolean childPageTitleAlreadyExists(Page sourcePage, Space targetSpace) {
        for (Page childPage : sourcePage.getChildren()) {
            if (this.pageManager.getPage(targetSpace.getKey(), childPage.getTitle()) != null) {
                return true;
            }
            if (!this.childPageTitleAlreadyExists(childPage, targetSpace)) continue;
            return true;
        }
        return false;
    }

    @Deprecated
    public List<String> listOfPageTitlesAlreadyExist(Page sourcePage, Space targetSpace) {
        ArrayList<String> existTitles = new ArrayList<String>();
        if (this.pageManager.getPage(targetSpace.getKey(), sourcePage.getTitle()) != null) {
            existTitles.add(sourcePage.getTitle());
        }
        List<String> descendantTitles = this.pageManager.getDescendantTitles(sourcePage);
        for (String title : descendantTitles) {
            if (this.pageManager.getPage(targetSpace.getKey(), title) == null) continue;
            existTitles.add(title);
        }
        return existTitles;
    }

    public MovePageCommand newMovePageCommand(PageLocator sourcePageLocator, PageLocator targetPageLocator, String position, MovePageMode movePageMode) {
        MovePageCommandImpl command = new MovePageCommandImpl(this.pageManager, this.permissionManager, sourcePageLocator, targetPageLocator, position);
        return movePageMode == MovePageMode.ASYNC ? new LongRunningTaskMovePageCommandDecorator(command, this.longRunningTaskManager) : command;
    }

    public MovePageCommand newMovePageCommand(PageLocator sourcePageLocator, SpaceLocator targetSpaceLocator, MovePageMode movePageMode) {
        MovePageToTopOfSpaceCommand command = new MovePageToTopOfSpaceCommand(this.pageManager, this.permissionManager, sourcePageLocator, targetSpaceLocator);
        return movePageMode == MovePageMode.ASYNC ? new LongRunningTaskMovePageCommandDecorator(command, this.longRunningTaskManager) : command;
    }

    public static enum MovePageMode {
        LEGACY,
        ASYNC;

    }
}

