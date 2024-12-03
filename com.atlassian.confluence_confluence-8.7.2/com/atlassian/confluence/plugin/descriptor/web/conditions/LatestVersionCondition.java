/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

public class LatestVersionCondition
extends BaseConfluenceCondition {
    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        AbstractPage page = context.getPage();
        return page != null && page.isLatestVersion();
    }
}

