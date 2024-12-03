/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.querydsl.sql.dml;

import com.google.common.collect.Maps;
import com.querydsl.core.types.Path;
import com.querydsl.core.util.BeanMap;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.dml.AbstractMapper;
import com.querydsl.sql.types.Null;
import java.util.LinkedHashMap;
import java.util.Map;

public class BeanMapper
extends AbstractMapper<Object> {
    public static final BeanMapper DEFAULT = new BeanMapper(false);
    public static final BeanMapper WITH_NULL_BINDINGS = new BeanMapper(true);
    private final boolean withNullBindings;

    public BeanMapper() {
        this(false);
    }

    public BeanMapper(boolean withNullBindings) {
        this.withNullBindings = withNullBindings;
    }

    @Override
    public Map<Path<?>, Object> createMap(RelationalPath<?> entity, Object bean) {
        LinkedHashMap values = Maps.newLinkedHashMap();
        BeanMap map = new BeanMap(bean);
        Map<String, Path<?>> columns = this.getColumns(entity);
        for (Map.Entry<String, Path<?>> entry : columns.entrySet()) {
            Path<?> path = entry.getValue();
            if (!map.containsKey((Object)entry.getKey())) continue;
            Object value = map.get((Object)entry.getKey());
            if (value != null) {
                values.put(path, value);
                continue;
            }
            if (!this.withNullBindings || this.isPrimaryKeyColumn(entity, path)) continue;
            values.put(path, Null.DEFAULT);
        }
        return values;
    }
}

