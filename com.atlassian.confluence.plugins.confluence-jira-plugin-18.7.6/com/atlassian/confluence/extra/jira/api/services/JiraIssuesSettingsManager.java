/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.api.services;

import java.util.Map;

public interface JiraIssuesSettingsManager {
    public Map<String, String> getColumnMap(String var1);

    public void setColumnMap(String var1, Map<String, String> var2);

    public Map<String, String> getIconMapping();

    public void setIconMapping(Map<String, String> var1);
}

