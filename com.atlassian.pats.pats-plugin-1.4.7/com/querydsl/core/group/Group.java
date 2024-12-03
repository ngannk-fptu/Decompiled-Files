/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.querydsl.core.group.GroupExpression;
import com.querydsl.core.types.Expression;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public interface Group {
    public Object[] toArray();

    public <T, R> R getGroup(GroupExpression<T, R> var1);

    public <T> T getOne(Expression<T> var1);

    public <T> Set<T> getSet(Expression<T> var1);

    public <T> SortedSet<T> getSortedSet(Expression<T> var1);

    public <T> List<T> getList(Expression<T> var1);

    public <K, V> Map<K, V> getMap(Expression<K> var1, Expression<V> var2);

    public <K, V> SortedMap<K, V> getSortedMap(Expression<K> var1, Expression<V> var2);
}

