/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.pages.actions.AbstractCommandAction;

public class RevertPageOrderAction
extends AbstractCommandAction {
    private PageService pageService;
    private long pageId;

    @Override
    protected ServiceCommand createCommand() {
        return this.pageService.newRevertPageOrderCommand(this.pageService.getIdPageLocator(this.pageId));
    }

    public void setPageService(PageService pageService) {
        this.pageService = pageService;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }
}

