/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.spaces.Space;

public class FavouriteSpaceCondition
extends BaseConfluenceCondition {
    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        Space space = context.getSpace();
        return space != null && space.getDescription().isFavourite(context.getCurrentUser());
    }
}

