/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 */
package com.atlassian.confluence.plugins.tasklist.service;

import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.tasklist.service.TaskPaginationService;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import java.util.List;

public class NonCheckingPermissionsTaskPaginationService
extends TaskPaginationService {
    public NonCheckingPermissionsTaskPaginationService(PageManager pageManager, PaginationService paginationService, UserAccessor userAccessor) {
        super(null, pageManager, paginationService, userAccessor);
    }

    @Override
    protected List<AbstractPage> getPermittedPages(ConfluenceUser confluenceUser, List<AbstractPage> pages) {
        return pages;
    }
}

