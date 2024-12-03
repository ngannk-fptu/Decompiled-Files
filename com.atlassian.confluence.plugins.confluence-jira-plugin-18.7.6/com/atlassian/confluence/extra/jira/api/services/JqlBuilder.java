/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Joiner$MapJoiner
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.jira.api.services;

import com.google.common.base.Joiner;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class JqlBuilder {
    private static final String ISSUE_KEY_PARAM = "key";
    private static final String ISSUE_TYPE_PARAM = "type";
    private static final String ISSUE_STATUS_PARAM = "status";
    private static final String ISSUE_PROJECT_PARAM = "project";
    private static final String ISSUE_AFFECTED_VERSION_PARAM = "affectedVersion";
    private static final String ISSUE_FIXED_VERSION_PARAM = "fixVersion";
    private static final String ISSUE_COMPONENT_PARAM = "component";
    private static final String ISSUE_ASSIGNEE_PARAM = "assignee";
    private static final String ISSUE_REPORTER_PARAM = "reporter";
    private final Map<String, String> singleValueParamMap;
    private final Map<String, String[]> multiValueParamMap;

    public JqlBuilder() {
        this.singleValueParamMap = new LinkedHashMap<String, String>();
        this.multiValueParamMap = new LinkedHashMap<String, String[]>();
    }

    public JqlBuilder(Map<String, String> jqlMapPredefined) {
        this.singleValueParamMap = new LinkedHashMap<String, String>(jqlMapPredefined);
        this.multiValueParamMap = new LinkedHashMap<String, String[]>();
    }

    public JqlBuilder put(String key, String value) {
        this.singleValueParamMap.put(key, value);
        return this;
    }

    public JqlBuilder put(String key, String ... values) {
        this.multiValueParamMap.put(key, values);
        return this;
    }

    public JqlBuilder issueKeys(String ... issueKeyValues) {
        this.put(ISSUE_KEY_PARAM, issueKeyValues);
        return this;
    }

    public JqlBuilder issueTypes(String ... issueTypes) {
        this.put(ISSUE_TYPE_PARAM, issueTypes);
        return this;
    }

    public JqlBuilder projectKeys(String ... projectKeyValues) {
        this.put(ISSUE_PROJECT_PARAM, projectKeyValues);
        return this;
    }

    public JqlBuilder affectsVersions(String ... affectsVersions) {
        this.put(ISSUE_AFFECTED_VERSION_PARAM, affectsVersions);
        return this;
    }

    public JqlBuilder components(String ... components) {
        this.put(ISSUE_COMPONENT_PARAM, components);
        return this;
    }

    public JqlBuilder statuses(String ... statuses) {
        this.put(ISSUE_STATUS_PARAM, statuses);
        return this;
    }

    public JqlBuilder fixVersions(String ... fixedVersions) {
        this.put(ISSUE_FIXED_VERSION_PARAM, fixedVersions);
        return this;
    }

    public JqlBuilder assignees(String ... assignees) {
        this.put(ISSUE_ASSIGNEE_PARAM, assignees);
        return this;
    }

    public JqlBuilder reporters(String ... reporters) {
        this.put(ISSUE_REPORTER_PARAM, reporters);
        return this;
    }

    private String buildJqlParam() {
        if (this.singleValueParamMap.isEmpty() && this.multiValueParamMap.isEmpty()) {
            throw new IllegalArgumentException("Builder have no any parameter");
        }
        StringBuilder paramString = new StringBuilder();
        Joiner.MapJoiner joiner = Joiner.on((String)" AND ").withKeyValueSeparator("=");
        paramString.append(joiner.join(this.singleValueParamMap));
        if (!this.multiValueParamMap.isEmpty()) {
            if (!this.singleValueParamMap.isEmpty()) {
                paramString.append(" AND ");
            }
            Iterator<String> jqlSets = this.multiValueParamMap.keySet().iterator();
            while (jqlSets.hasNext()) {
                String key = jqlSets.next();
                String inData = StringUtils.join((Object[])this.multiValueParamMap.get(key), (String)",");
                paramString.append(key).append(" IN(");
                paramString.append(inData);
                paramString.append(")");
                if (!jqlSets.hasNext()) continue;
                paramString.append(" AND ");
            }
        }
        return paramString.toString();
    }

    public String build() {
        return "jql=" + this.buildJqlParam();
    }

    public String buildAndEncode() {
        return "jql=" + URLEncoder.encode(this.buildJqlParam(), StandardCharsets.UTF_8);
    }
}

