/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.api.services;

public interface JiraIssuesUrlManager {
    public String getRequestUrl(String var1);

    public String getJiraXmlUrlFromFlexigridRequest(String var1, String var2, String var3, String var4);

    public String getJiraXmlUrlFromFlexigridRequest(String var1, String var2, String var3, String var4, String var5);
}

