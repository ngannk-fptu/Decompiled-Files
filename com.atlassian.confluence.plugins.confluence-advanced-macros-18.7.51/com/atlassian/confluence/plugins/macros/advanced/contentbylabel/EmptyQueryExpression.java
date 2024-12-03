/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.macros.advanced.contentbylabel;

import com.atlassian.confluence.plugins.macros.advanced.contentbylabel.QueryExpression;

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

