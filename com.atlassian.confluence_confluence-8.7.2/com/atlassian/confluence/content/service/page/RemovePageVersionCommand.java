/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.page.PageLocator;
import com.atlassian.confluence.content.service.page.RemoveAbstractPageVersionCommand;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;

public class RemovePageVersionCommand
extends RemoveAbstractPageVersionCommand {
    private final PageLocator pageLocator;

    public RemovePageVersionCommand(PageManager pageManager, PermissionManager permissionManager, PageLocator pageLocator) {
        super(pageManager, permissionManager);
        this.pageLocator = pageLocator;
    }

    @Override
    protected Page getPage() {
        return this.pageLocator.getPage();
    }
}

