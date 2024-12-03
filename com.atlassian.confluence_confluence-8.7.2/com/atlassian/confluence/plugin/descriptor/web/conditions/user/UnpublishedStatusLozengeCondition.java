/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContributionStatus;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collections;
import java.util.Map;

public class UnpublishedStatusLozengeCondition
extends BaseConfluenceCondition {
    private ContentEntityManager contentEntityManager;
    private DraftsTransitionHelper draftsTransitionHelper;

    @Override
    protected final boolean shouldDisplay(WebInterfaceContext webInterfaceContext) {
        AbstractPage page = webInterfaceContext.getPage();
        if (page == null || this.draftsTransitionHelper.getEditMode(page.getSpaceKey()).equals("legacy")) {
            return false;
        }
        ContentId contentId = page.getLatestVersion().getContentId();
        ConfluenceUser currentUser = webInterfaceContext.getCurrentUser();
        Map<Long, ContributionStatus> contributionStatusByUser = this.contentEntityManager.getContributionStatusByUser(Collections.singleton(contentId), currentUser != null ? currentUser.getKey() : null);
        ContributionStatus contributionStatus = contributionStatusByUser.get(contentId.asLong());
        return contributionStatus != null && "unpublished".equals(contributionStatus.getStatus());
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public void setDraftsTransitionHelper(DraftsTransitionHelper draftsTransitionHelper) {
        this.draftsTransitionHelper = draftsTransitionHelper;
    }
}

