/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 */
package com.atlassian.confluence.extra.jira.api.services;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import java.util.Set;

public interface JiraCacheManager {
    public void clearJiraIssuesCache(String var1, Set<String> var2, ReadOnlyApplicationLink var3, boolean var4, boolean var5);

    public void initializeCache();
}

