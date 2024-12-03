/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.MacroExecutionException
 */
package com.atlassian.confluence.extra.jira.api.services;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.extra.jira.JiraIssuesMacro;
import com.atlassian.confluence.extra.jira.columns.JiraColumnInfo;
import com.atlassian.confluence.macro.MacroExecutionException;
import java.util.Map;
import java.util.Set;

public interface JiraIssueSortingManager {
    public String getRequestDataForSorting(Map<String, String> var1, String var2, JiraIssuesMacro.Type var3, Set<JiraColumnInfo> var4, ConversionContext var5, ReadOnlyApplicationLink var6) throws MacroExecutionException;
}

