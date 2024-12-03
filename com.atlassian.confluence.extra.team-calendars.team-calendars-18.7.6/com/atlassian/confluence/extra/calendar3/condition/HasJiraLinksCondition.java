/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 */
package com.atlassian.confluence.extra.calendar3.condition;

import com.atlassian.confluence.extra.calendar3.calendarstore.JiraAccessor;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

public class HasJiraLinksCondition
extends BaseConfluenceCondition {
    private final JiraAccessor jiraAccessor;

    public HasJiraLinksCondition(JiraAccessor jiraAccessor) {
        this.jiraAccessor = jiraAccessor;
    }

    protected boolean shouldDisplay(WebInterfaceContext webInterfaceContext) {
        return !this.jiraAccessor.getLinkedJiraApplications().isEmpty();
    }
}

