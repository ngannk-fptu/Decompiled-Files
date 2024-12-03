/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.Lists
 */
package com.querydsl.core.group;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.FetchableQuery;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.AbstractGroupByTransformer;
import com.querydsl.core.group.Group;
import com.querydsl.core.group.GroupExpression;
import com.querydsl.core.group.GroupImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionUtils;
import com.querydsl.core.types.Projections;
import java.util.ArrayList;
import java.util.List;

public class GroupByList<K, V>
extends AbstractGroupByTransformer<K, List<V>> {
    GroupByList(Expression<K> key, Expression<?> ... expressions) {
        super(key, expressions);
    }

    @Override
    public List<V> transform(FetchableQuery<?, ?> query) {
        FactoryExpression<Tuple> expr = FactoryExpressionUtils.wrap(Projections.tuple(this.expressions));
        boolean hasGroups = false;
        for (Expression<?> e : expr.getArgs()) {
            hasGroups |= e instanceof GroupExpression;
        }
        if (hasGroups) {
            expr = GroupByList.withoutGroupExpressions(expr);
        }
        CloseableIterator iter = query.select(expr).iterate();
        ArrayList list = Lists.newArrayList();
        GroupImpl group = null;
        Object groupId = null;
        while (iter.hasNext()) {
            Object[] row = ((Tuple)iter.next()).toArray();
            if (group == null) {
                group = new GroupImpl(this.groupExpressions, this.maps);
                groupId = row[0];
            } else if (!Objects.equal(groupId, (Object)row[0])) {
                list.add(this.transform(group));
                group = new GroupImpl(this.groupExpressions, this.maps);
                groupId = row[0];
            }
            group.add(row);
        }
        if (group != null) {
            list.add(this.transform(group));
        }
        iter.close();
        return list;
    }

    protected V transform(Group group) {
        return (V)group;
    }
}

