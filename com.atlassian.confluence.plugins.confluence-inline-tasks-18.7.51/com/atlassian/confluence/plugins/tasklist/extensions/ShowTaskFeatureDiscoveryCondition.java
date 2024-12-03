/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.confluence.xwork.FlashScope
 */
package com.atlassian.confluence.plugins.tasklist.extensions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.xwork.FlashScope;

public class ShowTaskFeatureDiscoveryCondition
extends BaseConfluenceCondition {
    protected boolean shouldDisplay(WebInterfaceContext webInterfaceContext) {
        return FlashScope.has((String)"create-task");
    }
}

