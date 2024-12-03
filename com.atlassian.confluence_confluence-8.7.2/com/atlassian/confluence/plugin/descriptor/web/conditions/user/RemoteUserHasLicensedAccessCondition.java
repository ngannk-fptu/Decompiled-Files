/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.ConfluenceUser;

public final class RemoteUserHasLicensedAccessCondition
extends BaseConfluenceCondition {
    private ConfluenceAccessManager confluenceAccessManager;

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        ConfluenceUser currentUser = context.getCurrentUser();
        return currentUser != null && this.confluenceAccessManager.getUserAccessStatus(currentUser).hasLicensedAccess();
    }

    public void setConfluenceAccessManager(ConfluenceAccessManager confluenceAccessManager) {
        this.confluenceAccessManager = confluenceAccessManager;
    }
}

