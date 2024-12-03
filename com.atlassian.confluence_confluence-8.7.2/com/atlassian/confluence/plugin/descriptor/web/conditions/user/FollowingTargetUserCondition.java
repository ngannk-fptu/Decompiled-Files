/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.follow.FollowManager;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

public class FollowingTargetUserCondition
extends BaseConfluenceCondition {
    private FollowManager followManager;

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        return this.followManager.isUserFollowing(context.getCurrentUser(), context.getTargetedUser());
    }

    public void setFollowManager(FollowManager followManager) {
        this.followManager = followManager;
    }
}

