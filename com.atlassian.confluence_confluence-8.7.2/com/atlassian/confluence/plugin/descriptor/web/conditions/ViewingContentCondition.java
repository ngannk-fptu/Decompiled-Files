/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

public class ViewingContentCondition
extends BaseConfluenceCondition {
    public static final String CONTEXT_KEY = "viewMode";

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        return context.hasParameter(CONTEXT_KEY);
    }
}

