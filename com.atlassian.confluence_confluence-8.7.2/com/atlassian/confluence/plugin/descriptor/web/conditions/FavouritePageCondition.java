/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.user.ConfluenceUser;

public class FavouritePageCondition
extends BaseConfluenceCondition {
    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        AbstractPage page = context.getPage();
        ConfluenceUser currentUser = context.getCurrentUser();
        if (page == null || currentUser == null) {
            return false;
        }
        return page.isFavourite(currentUser);
    }
}

