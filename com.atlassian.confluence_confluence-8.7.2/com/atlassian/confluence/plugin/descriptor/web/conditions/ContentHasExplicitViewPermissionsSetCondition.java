/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

public class ContentHasExplicitViewPermissionsSetCondition
extends BaseConfluenceCondition {
    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        ContentEntityObject content = context.getPage() != null ? context.getPage() : context.getDraft();
        return content != null && content.hasPermissions("View");
    }
}

