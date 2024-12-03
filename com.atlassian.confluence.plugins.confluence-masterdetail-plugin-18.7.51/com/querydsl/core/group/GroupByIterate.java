/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.querydsl.core.group;

import com.google.common.base.Objects;
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
import java.util.NoSuchElementException;

public class GroupByIterate<K, V>
extends AbstractGroupByTransformer<K, CloseableIterator<V>> {
    GroupByIterate(Expression<K> key, Expression<?> ... expressions) {
        super(key, expressions);
    }

    @Override
    public CloseableIterator<V> transform(FetchableQuery<?, ?> query) {
        FactoryExpression<Tuple> expr = FactoryExpressionUtils.wrap(Projections.tuple(this.expressions));
        boolean hasGroups = false;
        for (Expression<?> e : expr.getArgs()) {
            hasGroups |= e instanceof GroupExpression;
        }
        if (hasGroups) {
            expr = GroupByIterate.withoutGroupExpressions(expr);
        }
        final CloseableIterator iter = query.select(expr).iterate();
        return new CloseableIterator<V>(){
            private GroupImpl group;
            private K groupId;

            @Override
            public boolean hasNext() {
                return this.group != null || iter.hasNext();
            }

            @Override
            public V next() {
                if (!iter.hasNext()) {
                    if (this.group != null) {
                        GroupImpl current = this.group;
                        this.group = null;
                        return GroupByIterate.this.transform(current);
                    }
                    throw new NoSuchElementException();
                }
                while (iter.hasNext()) {
                    Object[] row = ((Tuple)iter.next()).toArray();
                    if (this.group == null) {
                        this.group = new GroupImpl(GroupByIterate.this.groupExpressions, GroupByIterate.this.maps);
                        this.groupId = row[0];
                        this.group.add(row);
                        continue;
                    }
                    if (Objects.equal(this.groupId, (Object)row[0])) {
                        this.group.add(row);
                        continue;
                    }
                    GroupImpl current = this.group;
                    this.group = new GroupImpl(GroupByIterate.this.groupExpressions, GroupByIterate.this.maps);
                    this.groupId = row[0];
                    this.group.add(row);
                    return GroupByIterate.this.transform(current);
                }
                GroupImpl current = this.group;
                this.group = null;
                return GroupByIterate.this.transform(current);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void close() {
                iter.close();
            }
        };
    }

    protected V transform(Group group) {
        return (V)group;
    }
}

