/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.jira.columns;

import com.atlassian.confluence.extra.jira.columns.JiraColumnInfo;
import com.atlassian.confluence.extra.jira.util.JiraUtil;
import com.atlassian.confluence.plugins.jira.beans.JiraServerBean;
import com.google.common.collect.Lists;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class JiraIssueSortableHelper {
    public static final String SPACE = " ";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String SINGLE_QUOTE = "'";
    private static final long SUPPORT_JIRA_BUILD_NUMBER = 6251L;
    private static final Set<String> DEFAULT_RSS_FIELDS = new LinkedHashSet<String>(Arrays.asList("type", "key", "summary", "assignee", "reporter", "priority", "status", "resolution", "created", "updated", "due"));
    private static final String ASC = "ASC";
    private static final String DESC = "DESC";
    private static final String COMMA = ",";

    private JiraIssueSortableHelper() {
    }

    public static String reoderColumns(String orderType, String clauseName, String orderQuery, Set<JiraColumnInfo> jiraColumns) {
        String[] orderColumns = StringUtils.split((String)orderQuery, (String)COMMA);
        ArrayList reOrderColumns = Lists.newArrayList();
        for (String col : orderColumns) {
            String columnName = col = StringUtils.remove((String)col.trim(), (String)DOUBLE_QUOTE);
            Object orderTypeColumn = "";
            if (StringUtils.endsWithIgnoreCase((CharSequence)col, (CharSequence)" ASC") || StringUtils.endsWithIgnoreCase((CharSequence)col, (CharSequence)" DESC")) {
                String[] columnPart = StringUtils.split((String)col, (String)SPACE);
                columnName = columnPart[0];
                orderTypeColumn = SPACE + columnPart[1];
            }
            if (JiraIssueSortableHelper.isSameColumn(columnName, clauseName, jiraColumns)) continue;
            reOrderColumns.add(columnName + (String)orderTypeColumn);
        }
        reOrderColumns.add(0, DOUBLE_QUOTE + JiraUtil.escapeDoubleQuote(clauseName) + "\" " + orderType);
        return StringUtils.join((Iterable)reOrderColumns, (String)COMMA);
    }

    public static Set<String> getColumnNames(String columnsParameter) {
        if (StringUtils.isBlank((CharSequence)columnsParameter)) {
            return DEFAULT_RSS_FIELDS;
        }
        LinkedHashSet<String> columnNames = new LinkedHashSet<String>(Arrays.asList(StringUtils.split((String)columnsParameter, (String)",;")));
        return columnNames.isEmpty() ? DEFAULT_RSS_FIELDS : (Set)columnNames.stream().map(name -> {
            try {
                return URLDecoder.decode(name, StandardCharsets.UTF_8);
            }
            catch (IllegalArgumentException e) {
                return name;
            }
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    static boolean isJiraSupportedOrder(JiraServerBean jiraServer) {
        return jiraServer != null && jiraServer.getBuildNumber() >= 6251L;
    }

    private static boolean isSameColumn(String column, String aliasRefColumn, Set<JiraColumnInfo> jiraColumns) {
        if (StringUtils.equalsIgnoreCase((CharSequence)column, (CharSequence)aliasRefColumn)) {
            return true;
        }
        for (JiraColumnInfo jiraColumnInfo : jiraColumns) {
            if (!jiraColumnInfo.getClauseNames().contains(aliasRefColumn)) continue;
            return jiraColumnInfo.getClauseNames().contains(column);
        }
        return false;
    }
}

