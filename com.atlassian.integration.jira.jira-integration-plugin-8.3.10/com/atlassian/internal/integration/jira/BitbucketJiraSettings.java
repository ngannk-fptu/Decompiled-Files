/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.server.ApplicationPropertiesService
 */
package com.atlassian.internal.integration.jira;

import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.internal.integration.jira.JiraSettings;

public class BitbucketJiraSettings
implements JiraSettings {
    private final ApplicationPropertiesService propertiesService;

    public BitbucketJiraSettings(ApplicationPropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    @Override
    public int getProperty(String key, int defaultValue, int minimumValue, int maximumValue) {
        int value = this.propertiesService.getPluginProperty(key, defaultValue);
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
        return false;
    }
}

