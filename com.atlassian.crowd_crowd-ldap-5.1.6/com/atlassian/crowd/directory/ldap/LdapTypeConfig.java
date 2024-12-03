/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.ldap;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class LdapTypeConfig {
    private final String key;
    private final String displayName;
    private final Properties defaultValues;
    private final Set<String> hiddenFields = new HashSet<String>();

    public LdapTypeConfig(String key, String displayName, Properties defaultValues) {
        this.key = key;
        this.displayName = displayName;
        this.defaultValues = defaultValues;
    }

    public String getKey() {
        return this.key;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setHiddenField(String fieldName) {
        this.hiddenFields.add(fieldName);
    }

    public String getLdapTypeAsJson() {
        String fieldWithDash;
        String comma = "";
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"key\": \"").append(this.key).append("\", ");
        sb.append("\"defaults\": {");
        for (Map.Entry<Object, Object> entry : this.defaultValues.entrySet()) {
            fieldWithDash = entry.getKey().toString().replace('.', '-');
            sb.append(comma);
            sb.append("\"").append(fieldWithDash).append("\":");
            sb.append("\"").append(entry.getValue()).append("\"");
            comma = ",";
        }
        sb.append("},");
        sb.append("\"hidden\": [");
        comma = "";
        for (String field : this.hiddenFields) {
            fieldWithDash = field.replace('.', '-');
            sb.append(comma);
            sb.append("\"").append(fieldWithDash).append("\"");
            comma = ",";
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }
}

