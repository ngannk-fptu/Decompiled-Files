/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter;

import java.util.Objects;

public class ForeignKey {
    private final String fromTable;
    private final String fromField;
    private final String toTable;
    private final String toField;

    public ForeignKey(String fromTable, String fromField, String toTable, String toField) {
        this.fromTable = Objects.requireNonNull(fromTable);
        this.fromField = Objects.requireNonNull(fromField);
        this.toTable = Objects.requireNonNull(toTable);
        this.toField = Objects.requireNonNull(toField);
    }

    public String getFromTable() {
        return this.fromTable;
    }

    public String getFromField() {
        return this.fromField;
    }

    public String getToTable() {
        return this.toTable;
    }

    public String getToField() {
        return this.toField;
    }
}

