/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

public class HasPageCondition
extends BaseConfluenceCondition {
    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        return context.getPage() instanceof Page;
    }
}

