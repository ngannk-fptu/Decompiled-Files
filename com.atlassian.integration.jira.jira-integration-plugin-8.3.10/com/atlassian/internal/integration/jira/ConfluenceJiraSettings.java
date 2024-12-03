/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.internal.integration.jira;

import com.atlassian.internal.integration.jira.JiraSettings;

public class ConfluenceJiraSettings
implements JiraSettings {
    @Override
    public int getProperty(String key, int defaultValue, int minimumValue, int maximumValue) {
        int value = Integer.getInteger(key, defaultValue);
        if (value < minimumValue) {
            return minimumValue;
        }
        if (value > maximumValue) {
            return maximumValue;
        }
        return value;
    }

    @Override
    public boolean isBasicAuthenticationAllowed() {
        return true;
    }
}

