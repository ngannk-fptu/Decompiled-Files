/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.actions.AbstractDiffPagesAction;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.util.breadcrumbs.spaceia.ContentDetailAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@RequiresAnyConfluenceAccess
public class DiffPagesAction
extends AbstractDiffPagesAction
implements ContentDetailAction {
    private long originalId = 0L;

    @Override
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        return super.execute();
    }

    public long getOriginalId() {
        return this.originalId;
    }

    public void setOriginalId(long originalId) {
        this.originalId = originalId;
    }

    @Override
    public AbstractPage getOriginalPage() {
        if (this.originalPage == null && this.originalId != 0L) {
            this.originalPage = this.pageManager.getAbstractPage(this.originalId);
            return this.originalPage;
        }
        return this.originalPage;
    }

    @Override
    public boolean isLatestVersionRequired() {
        return false;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }
}

