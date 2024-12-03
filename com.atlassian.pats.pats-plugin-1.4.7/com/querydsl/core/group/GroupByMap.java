/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

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
import java.util.LinkedHashMap;
import java.util.Map;

public class GroupByMap<K, V>
extends AbstractGroupByTransformer<K, Map<K, V>> {
    GroupByMap(Expression<K> key, Expression<?> ... expressions) {
        super(key, expressions);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<K, V> transform(FetchableQuery<?, ?> query) {
        LinkedHashMap<Object, GroupImpl> groups = new LinkedHashMap<Object, GroupImpl>();
        FactoryExpression<Tuple> expr = FactoryExpressionUtils.wrap(Projections.tuple(this.expressions));
        boolean hasGroups = false;
        for (Expression<?> e : expr.getArgs()) {
            hasGroups |= e instanceof GroupExpression;
        }
        if (hasGroups) {
            expr = GroupByMap.withoutGroupExpressions(expr);
        }
        CloseableIterator iter = query.select(expr).iterate();
        try {
            while (iter.hasNext()) {
                Object[] row = ((Tuple)iter.next()).toArray();
                Object groupId = row[0];
                GroupImpl group = (GroupImpl)groups.get(groupId);
                if (group == null) {
                    group = new GroupImpl(this.groupExpressions, this.maps);
                    groups.put(groupId, group);
                }
                group.add(row);
            }
        }
        finally {
            iter.close();
        }
        return this.transform(groups);
    }

    @Override
    protected Map<K, V> transform(Map<K, Group> groups) {
        return groups;
    }
}

