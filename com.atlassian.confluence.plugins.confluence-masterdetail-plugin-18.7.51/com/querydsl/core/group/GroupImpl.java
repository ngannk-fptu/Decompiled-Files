/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.querydsl.core.group.Group;
import com.querydsl.core.group.GroupCollector;
import com.querydsl.core.group.GroupExpression;
import com.querydsl.core.group.QPair;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

class GroupImpl
implements Group {
    private final Map<Expression<?>, GroupCollector<?, ?>> groupCollectorMap = new LinkedHashMap();
    private final List<GroupExpression<?, ?>> groupExpressions;
    private final List<GroupCollector<?, ?>> groupCollectors = new ArrayList();
    private final List<QPair<?, ?>> maps;

    public GroupImpl(List<GroupExpression<?, ?>> columnDefinitions, List<QPair<?, ?>> maps) {
        this.groupExpressions = columnDefinitions;
        this.maps = maps;
        for (int i = 0; i < columnDefinitions.size(); ++i) {
            GroupExpression<?, ?> coldef = columnDefinitions.get(i);
            GroupCollector<?, ?> collector = this.groupCollectorMap.get(coldef.getExpression());
            if (collector == null) {
                collector = coldef.createGroupCollector();
                Expression<?> coldefExpr = coldef.getExpression();
                this.groupCollectorMap.put(coldefExpr, collector);
                if (coldefExpr instanceof Operation && ((Operation)coldefExpr).getOperator() == Ops.ALIAS) {
                    this.groupCollectorMap.put(((Operation)coldefExpr).getArg(1), collector);
                }
            }
            this.groupCollectors.add(collector);
        }
    }

    void add(Object[] row) {
        int i = 0;
        for (GroupCollector<?, ?> groupCollector : this.groupCollectors) {
            groupCollector.add(row[i]);
            ++i;
        }
    }

    private <T, R> R get(Expression<T> expr) {
        GroupCollector<?, ?> col = this.groupCollectorMap.get(expr);
        if (col != null) {
            return (R)col.get();
        }
        throw new NoSuchElementException(expr.toString());
    }

    @Override
    public <T, R> R getGroup(GroupExpression<T, R> definition) {
        for (GroupExpression<?, ?> def : this.groupExpressions) {
            if (!def.equals(definition)) continue;
            return (R)this.groupCollectorMap.get(def.getExpression()).get();
        }
        throw new NoSuchElementException(definition.toString());
    }

    @Override
    public <T> List<T> getList(Expression<T> expr) {
        return (List)this.get(expr);
    }

    @Override
    public <K, V> Map<K, V> getMap(Expression<K> key, Expression<V> value) {
        for (QPair<K, K> qPair : this.maps) {
            if (!qPair.equals(key, value)) continue;
            return (Map)this.groupCollectorMap.get(qPair).get();
        }
        throw new NoSuchElementException("GMap(" + key + ", " + value + ")");
    }

    @Override
    public <K, V> SortedMap<K, V> getSortedMap(Expression<K> key, Expression<V> value) {
        for (QPair<K, K> qPair : this.maps) {
            if (!qPair.equals(key, value)) continue;
            return (SortedMap)this.groupCollectorMap.get(qPair).get();
        }
        throw new NoSuchElementException("GMap(" + key + ", " + value + ")");
    }

    @Override
    public <T> T getOne(Expression<T> expr) {
        return (T)this.get(expr);
    }

    @Override
    public <T> Set<T> getSet(Expression<T> expr) {
        return (Set)this.get(expr);
    }

    @Override
    public <T> SortedSet<T> getSortedSet(Expression<T> expr) {
        return (SortedSet)this.get(expr);
    }

    @Override
    public Object[] toArray() {
        ArrayList arr = new ArrayList(this.groupCollectors.size());
        for (GroupCollector<?, ?> col : this.groupCollectors) {
            arr.add(col.get());
        }
        return arr.toArray();
    }
}

