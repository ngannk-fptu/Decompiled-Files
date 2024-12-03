/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.querydsl.core.support;

import com.google.common.collect.Lists;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.JoinType;
import com.querydsl.core.types.Expression;
import java.util.List;

public class OrderedQueryMetadata
extends DefaultQueryMetadata {
    private static final long serialVersionUID = 6326236143414219377L;
    private List<JoinExpression> joins;

    public OrderedQueryMetadata() {
        this.noValidate();
    }

    @Override
    public void addJoin(JoinType joinType, Expression<?> expr) {
        this.joins = null;
        super.addJoin(joinType, expr);
    }

    @Override
    public List<JoinExpression> getJoins() {
        if (this.joins == null) {
            this.joins = Lists.newArrayList();
            int separator = 0;
            for (JoinExpression j : super.getJoins()) {
                if (j.getType() == JoinType.DEFAULT) {
                    this.joins.add(separator++, j);
                    continue;
                }
                this.joins.add(j);
            }
        }
        return this.joins;
    }
}

