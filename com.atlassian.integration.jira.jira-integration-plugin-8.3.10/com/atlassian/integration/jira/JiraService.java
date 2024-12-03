/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 */
package com.atlassian.integration.jira;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.integration.jira.JiraFeature;
import com.atlassian.integration.jira.JiraIssueUrlsRequest;
import com.atlassian.integration.jira.JiraIssuesRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public interface JiraService {
    @Nonnull
    public String createIssue(@Nonnull ApplicationId var1, @Nonnull String var2);

    @Nonnull
    public Set<String> findValidIssues(@Nonnull Set<String> var1, @Nonnull ApplicationId var2);

    @Nonnull
    public String getIssuesAsJson(@Nonnull JiraIssuesRequest var1);

    @Nonnull
    public Map<String, String> getIssueUrls(@Nonnull JiraIssueUrlsRequest var1);

    @Nonnull
    public List<ApplicationLink> getJiraLinksForEntity(@Nonnull String var1);

    @Nonnull
    public Set<JiraFeature> getSupportedFeatures(@Nonnull ApplicationId var1);

    public boolean isLinked();
}

