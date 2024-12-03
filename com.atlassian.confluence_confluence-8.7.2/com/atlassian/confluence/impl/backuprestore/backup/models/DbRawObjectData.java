/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.models;

import java.util.Map;

public class DbRawObjectData {
    private Map<String, Object> objectProperties;

    public DbRawObjectData(Map<String, Object> objectProperties) {
        this.objectProperties = objectProperties;
    }

    public Map<String, Object> getObjectProperties() {
        return this.objectProperties;
    }

    public Object getObjectProperty(String name) {
        return this.objectProperties.get(name);
    }

    public String toString() {
        return "DbRawObjectData{objectProperties=" + this.objectProperties + "}";
    }
}

