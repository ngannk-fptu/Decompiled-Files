/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.user.PersonalInformation;

public class ViewingOwnProfileCondition
extends BaseConfluenceCondition {
    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        PersonalInformation personalInformation = context.getPersonalInformation();
        if (personalInformation == null) {
            return false;
        }
        return personalInformation.belongsTo(context.getCurrentUser());
    }
}

