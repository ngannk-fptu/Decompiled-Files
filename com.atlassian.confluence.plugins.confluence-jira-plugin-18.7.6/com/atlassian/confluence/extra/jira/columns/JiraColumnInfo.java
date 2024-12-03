/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package com.atlassian.confluence.extra.jira.columns;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Optional;

public class JiraColumnInfo {
    private static final List<String> NO_WRAPPED_TEXT_FIELDS = List.of("key", "type", "priority", "status", "created", "updated", "due");
    private static final String CLASS_NO_WRAP = "columns nowrap";
    private static final String CLASS_WRAP = "columns";
    private static final String URL_CUSTOM_FIELD_TYPE = "com.atlassian.jira.plugin.system.customfieldtypes:url";
    @SerializedName(value="name")
    private String title;
    @SerializedName(value="id")
    private String rssKey;
    @SerializedName(value="clauseNames")
    private List<String> clauseNames;
    private boolean sortable;
    @SerializedName(value="custom")
    private boolean custom;
    @SerializedName(value="navigable")
    private boolean navigable;
    @SerializedName(value="schema")
    private JsonSchema schema;

    public JiraColumnInfo() {
    }

    public JiraColumnInfo(String rssKey) {
        this(rssKey, rssKey);
    }

    public JiraColumnInfo(String rssKey, String title) {
        this.rssKey = rssKey;
        this.title = title;
    }

    public JiraColumnInfo(String rssKey, String title, List<String> clauseNames) {
        this(rssKey, title);
        this.clauseNames = clauseNames;
    }

    public JiraColumnInfo(String rssKey, String title, boolean sortable) {
        this(rssKey, title);
        this.sortable = sortable;
    }

    public JiraColumnInfo(String rssKey, String title, List<String> clauseNames, boolean sortable) {
        this(rssKey, title, clauseNames);
        this.sortable = sortable;
    }

    public JiraColumnInfo(String rssKey, String title, List<String> clauseNames, boolean sortable, JsonSchema schema) {
        this(rssKey, title, clauseNames);
        this.sortable = sortable;
        this.schema = schema;
    }

    public String getRssKey() {
        return this.rssKey;
    }

    public void setRssKey(String rssKey) {
        this.rssKey = rssKey;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return this.rssKey;
    }

    public JsonSchema getSchema() {
        return this.schema;
    }

    public String getSchemaString() {
        if (this.schema == null) {
            return null;
        }
        return Optional.ofNullable(this.schema.system).orElse(this.schema.custom);
    }

    public boolean eqSchemaOrKey(String schema) {
        return schema.equals(this.getSchemaString()) || schema.equals(this.getKey());
    }

    public String getHtmlClassName() {
        return this.shouldWrap() ? CLASS_WRAP : CLASS_NO_WRAP;
    }

    public boolean shouldWrap() {
        return !NO_WRAPPED_TEXT_FIELDS.contains(this.getKey().toLowerCase());
    }

    public String toString() {
        return this.getKey();
    }

    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return this.rssKey.equalsIgnoreCase((String)obj);
        }
        if (obj instanceof JiraColumnInfo) {
            JiraColumnInfo otherJiraColumnInfo = (JiraColumnInfo)obj;
            return this.rssKey.equalsIgnoreCase(otherJiraColumnInfo.rssKey);
        }
        return false;
    }

    public int hashCode() {
        return this.rssKey.hashCode();
    }

    public List<String> getClauseNames() {
        return this.clauseNames;
    }

    public void setClauseName(List<String> clauseNames) {
        this.clauseNames = clauseNames;
    }

    public boolean isSortable() {
        return this.sortable;
    }

    public String getPrimaryClauseName() {
        return this.clauseNames != null && !this.clauseNames.isEmpty() ? this.clauseNames.get(0) : "";
    }

    public boolean isCustom() {
        return this.custom;
    }

    public boolean isNavigable() {
        return this.navigable;
    }

    public boolean isUrlColumn() {
        return this.schema != null && URL_CUSTOM_FIELD_TYPE.equals(this.schema.custom);
    }

    public static class JsonSchema {
        @SerializedName(value="system")
        public String system;
        @SerializedName(value="custom")
        public String custom;
        @SerializedName(value="type")
        public String type;
        @SerializedName(value="customeId")
        public int customeId;

        public JsonSchema() {
        }

        public JsonSchema(String type, String custom, int customeId, String system) {
            this.system = system;
            this.custom = custom;
            this.type = type;
            this.customeId = customeId;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            JsonSchema other = (JsonSchema)obj;
            if (this.system == null ? other.system != null : !this.system.equals(other.system)) {
                return false;
            }
            if (this.custom == null ? other.custom != null : !this.custom.equals(other.custom)) {
                return false;
            }
            return this.customeId == other.customeId;
        }
    }
}

