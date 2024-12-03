/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.helper;

import java.util.Map;

public class SearchIssue {
    private String key;
    private Map<String, Object> fields;

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, Object> getFields() {
        return this.fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }
}

