/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 */
package com.atlassian.confluence.tinymceplugin.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

@Deprecated
public class IsEditorPageRestrictedCondition
extends BaseConfluenceCondition {
    public boolean shouldDisplay(WebInterfaceContext context) {
        return context.isEditPageRestricted();
    }
}

