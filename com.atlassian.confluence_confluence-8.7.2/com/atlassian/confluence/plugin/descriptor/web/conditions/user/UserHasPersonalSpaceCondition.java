/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.spaces.SpaceManager;

public class UserHasPersonalSpaceCondition
extends BaseConfluenceCondition {
    private SpaceManager spaceManager;

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        return this.spaceManager.getPersonalSpace(context.getCurrentUser()) != null;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }
}

