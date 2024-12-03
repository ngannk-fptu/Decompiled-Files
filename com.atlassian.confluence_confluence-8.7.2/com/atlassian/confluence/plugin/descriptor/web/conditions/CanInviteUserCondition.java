/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.admin.criteria.CanInviteUserCriteria;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

public class CanInviteUserCondition
extends BaseConfluenceCondition {
    private CanInviteUserCriteria canInviteUserCriteria;

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        return this.canInviteUserCriteria.isMet();
    }

    public void setCanInviteUserCriteria(CanInviteUserCriteria canInviteUserCriteria) {
        this.canInviteUserCriteria = canInviteUserCriteria;
    }
}

