/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.internal.integration.jira;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.integration.jira.JiraService;
import com.atlassian.internal.integration.jira.IconRequest;
import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteItem;
import com.atlassian.internal.integration.jira.autocomplete.RestAutoCompleteContext;
import com.atlassian.internal.integration.jira.request.MyAssignedJiraIssuesRequest;
import java.net.URI;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;

public interface InternalJiraService
extends JiraService {
    @Nonnull
    public String createIssues(@Nonnull ApplicationId var1, @Nonnull String var2);

    @Nullable
    public URI findIssue(@Nonnull String var1, @Nullable String var2);

    @Nonnull
    public Collection<AutoCompleteItem> getAutoCompleteItems(@Nonnull RestAutoCompleteContext var1);

    @Nonnull
    public String getIssueTransitionsAsJson(@Nonnull String var1, @Nonnull ApplicationId var2);

    @Nonnull
    public String getIssueTypeMetaAsJson(@Nonnull ApplicationId var1, @Nonnull String var2, int var3);

    @Nonnull
    public String getIssueTypesAsJson(@Nonnull ApplicationId var1, @Nonnull String var2);

    @Nonnull
    public String getProjectsAsJson(@Nonnull ApplicationId var1);

    @Nonnull
    public String getServersAsJson();

    public void streamIcon(@Nonnull IconRequest var1, @Nonnull HttpServletResponse var2);

    @Nonnull
    public String transitionIssue(@Nonnull String var1, @Nullable Set<String> var2, @Nonnull ApplicationId var3, @Nonnull String var4);

    @Nonnull
    public String getMyAssignedIssues(@Nonnull MyAssignedJiraIssuesRequest var1);
}

