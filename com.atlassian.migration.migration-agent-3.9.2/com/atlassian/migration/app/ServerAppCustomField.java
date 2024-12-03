/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

import java.util.Objects;

public class ServerAppCustomField {
    private final String fieldName;
    private final String fieldTypeKey;

    public ServerAppCustomField(String fieldName, String fieldTypeKey) {
        this.fieldName = fieldName;
        this.fieldTypeKey = fieldTypeKey;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getFieldTypeKey() {
        return this.fieldTypeKey;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ServerAppCustomField that = (ServerAppCustomField)o;
        return this.fieldName.equals(that.fieldName) && this.fieldTypeKey.equals(that.fieldTypeKey);
    }

    public int hashCode() {
        return Objects.hash(this.fieldName, this.fieldTypeKey);
    }
}

