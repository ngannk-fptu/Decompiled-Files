/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.ApplicationType
 */
package com.atlassian.crowd.model;

import com.atlassian.crowd.model.application.ApplicationType;

public enum ApplicationSubtype {
    JIRA_CORE("jira-core", "Jira Core"),
    JIRA_SERVICE_DESK("jira-servicedesk", "Jira Service Desk"),
    JIRA_SOFTWARE("jira-software", "Jira Software"),
    CONFLUENCE("confluence", "Confluence"),
    BITBUCKET("bitbucket", "Bitbucket");

    private String value;
    private String displayName;

    private ApplicationSubtype(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getValue() {
        return this.value;
    }

    public static ApplicationSubtype toEnum(String value) {
        for (ApplicationSubtype v : ApplicationSubtype.values()) {
            if (!v.getValue().equalsIgnoreCase(value)) continue;
            return v;
        }
        throw new IllegalArgumentException(value);
    }

    public static ApplicationSubtype getOnlySubtype(ApplicationType applicationType) {
        switch (applicationType) {
            case CONFLUENCE: {
                return CONFLUENCE;
            }
            case STASH: {
                return BITBUCKET;
            }
        }
        throw new IllegalArgumentException("No default subtype for: " + applicationType);
    }
}

