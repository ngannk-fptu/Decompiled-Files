/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.masterdetail.cqlmigrator;

import com.atlassian.confluence.extra.masterdetail.cqlmigrator.QueryExpression;

public class EmptyQueryExpression
implements QueryExpression {
    public static final EmptyQueryExpression EMPTY = new EmptyQueryExpression();

    private EmptyQueryExpression() {
    }

    @Override
    public String toQueryString() {
        return "";
    }
}

