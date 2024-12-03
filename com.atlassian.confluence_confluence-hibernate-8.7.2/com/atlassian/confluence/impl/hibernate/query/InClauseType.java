/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.hibernate.query;

public enum InClauseType {
    IN("in", " or "),
    NOT_IN("not in", " and ");

    private final String clauseType;
    private final String joinString;

    private InClauseType(String clauseType, String joinString) {
        this.clauseType = clauseType;
        this.joinString = joinString;
    }

    public String getClauseType() {
        return this.clauseType;
    }

    public String getJoinString() {
        return this.joinString;
    }
}

