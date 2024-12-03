/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

public class UserLoggedInCondition
extends BaseConfluenceCondition {
    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        return context.getCurrentUser() != null;
    }
}

