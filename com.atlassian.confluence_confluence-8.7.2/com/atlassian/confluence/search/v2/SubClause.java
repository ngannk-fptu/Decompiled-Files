/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.ClauseType;

public class SubClause<T> {
    private final T clause;
    private final ClauseType clauseType;

    public SubClause(T query, ClauseType clauseType) {
        this.clause = query;
        this.clauseType = clauseType;
    }

    public T getClause() {
        return this.clause;
    }

    public ClauseType getClauseType() {
        return this.clauseType;
    }
}

