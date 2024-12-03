/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.config;

import org.apache.lucene.queryparser.flexible.core.config.AbstractQueryConfig;

public class FieldConfig
extends AbstractQueryConfig {
    private String fieldName;

    public FieldConfig(String fieldName) {
        if (fieldName == null) {
            throw new IllegalArgumentException("field name should not be null!");
        }
        this.fieldName = fieldName;
    }

    public String getField() {
        return this.fieldName;
    }

    public String toString() {
        return "<fieldconfig name=\"" + this.fieldName + "\" configurations=\"" + super.toString() + "\"/>";
    }
}

