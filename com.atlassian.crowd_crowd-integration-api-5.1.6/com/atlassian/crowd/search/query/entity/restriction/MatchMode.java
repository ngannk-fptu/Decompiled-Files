/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.search.query.entity.restriction;

public enum MatchMode {
    EXACTLY_MATCHES(true),
    STARTS_WITH(false),
    ENDS_WITH(false),
    CONTAINS(false),
    LESS_THAN(false),
    LESS_THAN_OR_EQUAL(false),
    GREATER_THAN(false),
    GREATER_THAN_OR_EQUAL(false),
    NULL(true);

    private final boolean exact;

    private MatchMode(boolean exact) {
        this.exact = exact;
    }

    public boolean isExact() {
        return this.exact;
    }
}

