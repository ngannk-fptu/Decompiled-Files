/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.querydsl.core.group.Group;
import com.querydsl.core.group.GroupByMap;
import com.querydsl.core.types.Expression;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class GroupByProjection<K, V>
extends GroupByMap<K, V> {
    public GroupByProjection(Expression<K> key, Expression<?> ... expressions) {
        super(key, expressions);
    }

    @Override
    protected Map<K, V> transform(Map<K, Group> groups) {
        LinkedHashMap<K, V> results = new LinkedHashMap<K, V>((int)Math.ceil((double)groups.size() / 0.75), 0.75f);
        for (Map.Entry<K, Group> entry : groups.entrySet()) {
            results.put(entry.getKey(), this.transform(entry.getValue()));
        }
        return results;
    }

    protected abstract V transform(Group var1);
}

