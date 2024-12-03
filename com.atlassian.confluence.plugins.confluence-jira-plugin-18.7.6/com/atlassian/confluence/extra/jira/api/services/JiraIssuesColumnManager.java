/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.extra.jira.api.services;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.extra.jira.columns.JiraColumnInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public interface JiraIssuesColumnManager {
    public static final Set<String> ALL_BUILTIN_COLUMN_NAMES = Set.of("description", "environment", "key", "summary", "type", "parent", "creator", "project", "priority", "status", "version", "resolution", "security", "assignee", "reporter", "created", "updated", "due", "component", "components", "votes", "comments", "attachments", "subtasks", "fixversion", "timeoriginalestimate", "timeestimate", "statuscategory");
    public static final Set<String> ALL_MULTIVALUE_BUILTIN_COLUMN_NAMES = Set.of("version", "component", "comments", "attachments", "fixversion", "fixVersion", "labels");
    public static final Set<String> SUPPORT_SORTABLE_COLUMN_NAMES = Set.of("issuekey", "summary", "issuetype", "created", "updated", "duedate", "assignee", "reporter", "priority", "status", "resolution", "version", "security", "watches", "components", "description", "environment", "fixVersion", "labels", "lastviewed", "timeoriginalestimate", "progress", "project", "timeestimate", "resolved", "subtasks", "timespent", "votes", "workratio", "resolutiondate");
    public static final Map<String, String> COLUMN_KEYS_MAPPING = new ImmutableMap.Builder().put((Object)"version", (Object)"affectedVersion").put((Object)"security", (Object)"level").put((Object)"watches", (Object)"watchers").put((Object)"type", (Object)"issuetype").build();
    public static final Map<String, String> XML_COLUMN_KEYS_MAPPING = Map.of("due", "duedate", "type", "issueType", "key", "issuekey");
    public static final Set<String> SINGLE_ISSUE_COLUMN_NAMES = new LinkedHashSet<String>(List.of("type", "summary", "status", "resolution", "statusCategory"));

    public Map<String, String> getColumnMap(String var1);

    public void setColumnMap(String var1, Map<String, String> var2);

    public boolean isColumnBuiltIn(String var1);

    public String getCanonicalFormOfBuiltInField(String var1);

    public boolean isBuiltInColumnMultivalue(String var1);

    public Set<JiraColumnInfo> getColumnsInfoFromJira(ReadOnlyApplicationLink var1) throws ExecutionException;

    public Set<JiraColumnInfo> getColumnInfo(Map<String, String> var1, Set<JiraColumnInfo> var2, ReadOnlyApplicationLink var3);

    public String getColumnMapping(String var1, Map<String, String> var2);

    public ImmutableMap<String, ImmutableSet<String>> getI18nColumnNames();

    public boolean columnsContainsEpicColumns(Set<JiraColumnInfo> var1);

    public boolean columnsContainsTeamColumns(Set<JiraColumnInfo> var1);
}

