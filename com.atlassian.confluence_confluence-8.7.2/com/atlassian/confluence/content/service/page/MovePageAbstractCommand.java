/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.page.MovePageCommand;
import com.atlassian.confluence.core.service.AbstractServiceCommand;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.List;

public abstract class MovePageAbstractCommand
extends AbstractServiceCommand
implements MovePageCommand {
    protected final PageManager pageManager;
    protected final PermissionManager permissionManager;

    MovePageAbstractCommand(PageManager pageManager, PermissionManager permissionManager) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
    }

    List<String> listOfPermittedPageTitlesAlreadyExist(Page sourcePage, Space targetSpace) {
        ArrayList<String> existTitles = new ArrayList<String>();
        if (this.pageManager.getPage(targetSpace.getKey(), sourcePage.getTitle()) != null) {
            existTitles.add(sourcePage.getTitle());
        }
        List<String> descendantTitles = this.pageManager.getDescendantTitles(sourcePage);
        for (String title : descendantTitles) {
            Page page = this.pageManager.getPage(targetSpace.getKey(), title);
            if (page == null || !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, page)) continue;
            existTitles.add(title);
        }
        return existTitles;
    }
}

