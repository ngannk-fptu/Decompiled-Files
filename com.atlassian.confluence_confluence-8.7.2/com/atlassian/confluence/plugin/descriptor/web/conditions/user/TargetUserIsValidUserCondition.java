/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.internal.user.UserAccessorInternal;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.user.ConfluenceUser;

public class TargetUserIsValidUserCondition
extends BaseConfluenceCondition {
    private UserAccessorInternal userAccessor;

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        ConfluenceUser targetedUser = context.getTargetedUser();
        if (targetedUser == null) {
            return false;
        }
        return this.userAccessor.isCrowdManaged(targetedUser);
    }

    public void setUserAccessor(UserAccessorInternal userAccessor) {
        this.userAccessor = userAccessor;
    }
}

