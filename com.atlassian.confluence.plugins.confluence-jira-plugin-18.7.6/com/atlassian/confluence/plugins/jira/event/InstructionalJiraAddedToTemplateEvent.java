/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.plugins.jira.event;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.template.instructional.create.jira")
public class InstructionalJiraAddedToTemplateEvent {
    private String instances;

    public InstructionalJiraAddedToTemplateEvent(String instances) {
        this.instances = instances;
    }

    public String getInstances() {
        return this.instances;
    }
}

