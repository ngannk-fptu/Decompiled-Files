/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.querydsl.sql.dml;

import com.google.common.collect.Maps;
import com.querydsl.core.QueryException;
import com.querydsl.core.types.Path;
import com.querydsl.core.util.ReflectionUtils;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.dml.AbstractMapper;
import com.querydsl.sql.types.Null;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultMapper
extends AbstractMapper<Object> {
    public static final DefaultMapper DEFAULT = new DefaultMapper(false);
    public static final DefaultMapper WITH_NULL_BINDINGS = new DefaultMapper(true);
    private final boolean withNullBindings;

    public DefaultMapper() {
        this(false);
    }

    public DefaultMapper(boolean withNullBindings) {
        this.withNullBindings = withNullBindings;
    }

    @Override
    public Map<Path<?>, Object> createMap(RelationalPath<?> entity, Object bean) {
        try {
            LinkedHashMap values = Maps.newLinkedHashMap();
            Class<?> beanClass = bean.getClass();
            Map<String, Path<?>> columns = this.getColumns(entity);
            for (Map.Entry<String, Path<?>> entry : columns.entrySet()) {
                Path<?> path = entry.getValue();
                Field beanField = ReflectionUtils.getFieldOrNull(beanClass, entry.getKey());
                if (beanField == null || Modifier.isStatic(beanField.getModifiers())) continue;
                beanField.setAccessible(true);
                Object propertyValue = beanField.get(bean);
                if (propertyValue != null) {
                    values.put(path, propertyValue);
                    continue;
                }
                if (!this.withNullBindings || this.isPrimaryKeyColumn(entity, path)) continue;
                values.put(path, Null.DEFAULT);
            }
            return values;
        }
        catch (IllegalAccessException e) {
            throw new QueryException(e);
        }
    }
}

