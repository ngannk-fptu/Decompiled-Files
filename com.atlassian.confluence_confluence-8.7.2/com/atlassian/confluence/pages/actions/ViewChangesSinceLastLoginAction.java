/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.actions.AbstractDiffPagesAction;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.util.breadcrumbs.spaceia.ContentDetailAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.List;

@RequiresAnyConfluenceAccess
public class ViewChangesSinceLastLoginAction
extends AbstractDiffPagesAction
implements ContentDetailAction {
    @Override
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        return super.execute();
    }

    @Override
    public AbstractPage getOriginalPage() {
        if (this.originalPage == null) {
            List<VersionHistorySummary> previousVersions = this.pageManager.getVersionHistorySummaries(this.getPage());
            for (VersionHistorySummary versionHistorySummary : previousVersions) {
                if (!versionHistorySummary.getLastModificationDate().before(this.getPreviousLoginDate())) continue;
                this.originalPage = this.pageManager.getPage(versionHistorySummary.getId());
                break;
            }
            if (this.originalPage == null) {
                this.originalPage = this.getPage();
            }
        }
        return this.originalPage;
    }
}

