/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.macros.advanced.contentbylabel;

import com.atlassian.confluence.plugins.macros.advanced.contentbylabel.EmptyQueryExpression;
import com.atlassian.confluence.plugins.macros.advanced.contentbylabel.QueryExpression;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;

public class CompositeQueryExpression
implements QueryExpression {
    private final BooleanOperator operator;
    private final List<QueryExpression> expressions;

    private CompositeQueryExpression(BooleanOperator operator, List<? extends QueryExpression> expressions) {
        this.operator = operator;
        this.expressions = ImmutableList.copyOf(expressions);
    }

    @Override
    public String toQueryString() {
        if (this.expressions.isEmpty()) {
            return "";
        }
        if (this.expressions.size() > 1) {
            return Joiner.on((String)this.operator.toString()).join((Iterable)Lists.transform(this.expressions, (Function)new Function<QueryExpression, String>(){

                public String apply(QueryExpression input) {
                    return input.toQueryString();
                }
            }));
        }
        return this.expressions.get(0).toQueryString();
    }

    public boolean isEmpty() {
        return this.expressions.isEmpty();
    }

    public BooleanOperator getOperator() {
        return this.operator;
    }

    public QueryExpression get(int i) {
        return this.expressions.get(i);
    }

    public int size() {
        return this.expressions.size();
    }

    public static Builder builder(BooleanOperator operator) {
        return new Builder(operator);
    }

    public static class Builder {
        private BooleanOperator operator;
        private List<QueryExpression> expressions = Lists.newArrayList();

        private Builder(BooleanOperator operator) {
            this.operator = operator;
        }

        public Builder add(QueryExpression expression) {
            if (expression != null && !(expression instanceof EmptyQueryExpression)) {
                this.expressions.add(expression);
            }
            return this;
        }

        public QueryExpression build() {
            if (this.expressions.isEmpty()) {
                return EmptyQueryExpression.EMPTY;
            }
            if (this.expressions.size() == 1) {
                return this.expressions.get(0);
            }
            return new CompositeQueryExpression(this.operator, this.expressions);
        }
    }

    public static enum BooleanOperator {
        AND(" and "),
        OR(" or ");

        private final String str;

        private BooleanOperator(String str) {
            this.str = str;
        }

        public String toString() {
            return this.str;
        }
    }
}

