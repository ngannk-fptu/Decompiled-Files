/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade.ddl;

public enum NullChoice {
    NULLABLE("null"),
    NOT_NULL("not null");

    private String fragment;

    private NullChoice(String fragment) {
        this.fragment = fragment;
    }

    String toSqlFragment() {
        return this.fragment;
    }
}

