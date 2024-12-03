/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.jira.model;

import com.atlassian.confluence.extra.jira.JiraIssuesMacro;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class ClientId {
    private static final String SEPARATOR = "_";
    private static final int NO_JQL_OR_COLUMNS = 4;
    private static final int NO_COLUMNS = 5;
    private static final int INCL_JQL_AND_COLUMNS = 6;
    private String serverId;
    private String pageId;
    private String userId;
    private String jqlQuery;
    private JiraIssuesMacro.JiraIssuesType jiraIssuesType;
    private String columnNames;

    private ClientId(JiraIssuesMacro.JiraIssuesType jiraIssuesType, String serverId, String pageId, String userId, String jqlQuery, String columnNames) {
        this.serverId = serverId;
        this.pageId = pageId;
        this.userId = userId;
        this.jqlQuery = jqlQuery;
        this.jiraIssuesType = jiraIssuesType;
        this.columnNames = columnNames;
    }

    public static ClientId fromElement(JiraIssuesMacro.JiraIssuesType jiraIssuesType, String serverId, String pageId, String userId, String jqlQuery, String columnNames) {
        if (StringUtils.isEmpty((CharSequence)serverId) || StringUtils.isEmpty((CharSequence)pageId) || StringUtils.isEmpty((CharSequence)userId)) {
            throw new IllegalArgumentException("Wrong ClientId data");
        }
        return new ClientId(jiraIssuesType, serverId, pageId, userId, jqlQuery, columnNames);
    }

    public static ClientId fromElement(JiraIssuesMacro.JiraIssuesType jiraIssuesType, String serverId, String pageId, String userId) {
        return ClientId.fromElement(jiraIssuesType, serverId, pageId, userId, null, null);
    }

    public static ClientId fromClientId(String clientId) {
        String[] elements = clientId.split(SEPARATOR);
        if (elements.length == 4) {
            return new ClientId(JiraIssuesMacro.JiraIssuesType.valueOf(elements[0]), elements[1], elements[2], elements[3], null, null);
        }
        if (elements.length == 5) {
            return new ClientId(JiraIssuesMacro.JiraIssuesType.valueOf(elements[0]), elements[1], elements[2], elements[3], new String(Base64.decodeBase64((String)elements[4])), null);
        }
        if (elements.length == 6) {
            return new ClientId(JiraIssuesMacro.JiraIssuesType.valueOf(elements[0]), elements[1], elements[2], elements[3], new String(Base64.decodeBase64((String)elements[4])), new String(Base64.decodeBase64((String)elements[5])));
        }
        throw new IllegalArgumentException("Wrong clientId format=" + clientId);
    }

    public String getServerId() {
        return this.serverId;
    }

    public String getPageId() {
        return this.pageId;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getJqlQuery() {
        return this.jqlQuery;
    }

    public String getColumnNames() {
        return this.columnNames;
    }

    public JiraIssuesMacro.JiraIssuesType getJiraIssuesType() {
        return this.jiraIssuesType;
    }

    public String toString() {
        ArrayList params = Lists.newArrayList((Object[])new String[]{this.jiraIssuesType.toString(), this.serverId, this.pageId, this.userId});
        if (StringUtils.isNotEmpty((CharSequence)this.jqlQuery)) {
            params.add(new String(Base64.encodeBase64((byte[])this.jqlQuery.getBytes())));
        }
        if (StringUtils.isNotEmpty((CharSequence)this.columnNames)) {
            params.add(new String(Base64.encodeBase64((byte[])this.columnNames.getBytes())));
        }
        return StringUtils.join((Iterable)params, (String)SEPARATOR);
    }

    public int hashCode() {
        int result = this.pageId != null ? this.pageId.hashCode() : 0;
        result = 31 * result + (this.serverId != null ? this.serverId.hashCode() : 0);
        result = 31 * result + (this.userId != null ? this.userId.hashCode() : 0);
        result = 31 * result + (this.jqlQuery != null ? this.jqlQuery.hashCode() : 0);
        result = 31 * result + (this.jiraIssuesType != null ? this.jiraIssuesType.hashCode() : 0);
        result = 31 * result + (this.columnNames != null ? this.columnNames.hashCode() : 0);
        return result;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClientId that = (ClientId)o;
        return StringUtils.equals((CharSequence)this.serverId, (CharSequence)that.serverId) && StringUtils.equals((CharSequence)this.pageId, (CharSequence)that.pageId) && StringUtils.equals((CharSequence)this.userId, (CharSequence)that.userId) && StringUtils.equals((CharSequence)this.jqlQuery, (CharSequence)that.jqlQuery) && StringUtils.equals((CharSequence)this.columnNames, (CharSequence)that.columnNames) && this.jiraIssuesType == that.jiraIssuesType;
    }
}

