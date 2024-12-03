/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 */
package com.atlassian.confluence.extra.jira.cache;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.extra.jira.JiraIssuesManager;
import com.atlassian.confluence.extra.jira.api.services.ConfluenceJiraPluginSettingManager;
import com.atlassian.confluence.extra.jira.api.services.JiraCacheManager;
import java.util.Optional;

public class CacheSettingsAction
extends ConfluenceActionSupport {
    private static final Integer DEFAULT_JIM_CACHE_TIMEOUT = Integer.parseInt(System.getProperty("confluence.jim.cache.time", "5"));
    private ConfluenceJiraPluginSettingManager confluenceJiraPluginSettingManager;
    private JiraIssuesManager jiraIssuesManager;
    private JiraCacheManager jiraCacheManager;
    private String cacheTimeoutInMinutes;
    private boolean settingsUpdated;

    public String setCacheSettings() {
        Integer newCacheTimeout;
        Optional<Integer> cacheTimeOutInMinutesConfiguration = this.confluenceJiraPluginSettingManager.getCacheTimeoutInMinutes();
        Integer currentCacheTimeout = null;
        if (cacheTimeOutInMinutesConfiguration.isPresent()) {
            currentCacheTimeout = cacheTimeOutInMinutesConfiguration.get();
        }
        try {
            newCacheTimeout = Integer.parseInt(this.cacheTimeoutInMinutes);
        }
        catch (NumberFormatException nfe) {
            newCacheTimeout = DEFAULT_JIM_CACHE_TIMEOUT;
        }
        if (!newCacheTimeout.equals(currentCacheTimeout)) {
            this.confluenceJiraPluginSettingManager.setCacheTimeoutInMinutes(Optional.of(newCacheTimeout));
            this.jiraIssuesManager.initializeCache();
            this.jiraCacheManager.initializeCache();
        }
        return "success";
    }

    public void setConfluenceJiraPluginSettingManager(ConfluenceJiraPluginSettingManager confluenceJiraPluginSettingManager) {
        this.confluenceJiraPluginSettingManager = confluenceJiraPluginSettingManager;
    }

    public String getCacheTimeoutInMinutes() {
        Optional<Integer> cacheTimeOutInMinutesConfiguration = this.confluenceJiraPluginSettingManager.getCacheTimeoutInMinutes();
        Integer timeoutInMinutes = null;
        if (cacheTimeOutInMinutesConfiguration.isPresent()) {
            timeoutInMinutes = cacheTimeOutInMinutesConfiguration.get();
        }
        if (timeoutInMinutes == null) {
            return String.valueOf(DEFAULT_JIM_CACHE_TIMEOUT);
        }
        return String.valueOf(timeoutInMinutes);
    }

    public void setCacheTimeoutInMinutes(String cacheTimeoutInMinutes) {
        this.cacheTimeoutInMinutes = cacheTimeoutInMinutes;
    }

    public boolean isSettingsUpdated() {
        return this.settingsUpdated;
    }

    public void setSettingsUpdated(boolean settingsUpdated) {
        this.settingsUpdated = settingsUpdated;
    }

    public void setJiraIssuesManager(JiraIssuesManager jiraIssuesManager) {
        this.jiraIssuesManager = jiraIssuesManager;
    }

    public void setJiraCacheManager(JiraCacheManager jiraCacheManager) {
        this.jiraCacheManager = jiraCacheManager;
    }
}

