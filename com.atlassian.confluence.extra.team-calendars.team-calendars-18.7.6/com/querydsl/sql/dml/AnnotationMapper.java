/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.dml;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.Path;
import com.querydsl.core.util.ReflectionUtils;
import com.querydsl.sql.Column;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.dml.Mapper;
import com.querydsl.sql.types.Null;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class AnnotationMapper
implements Mapper<Object> {
    public static final AnnotationMapper DEFAULT = new AnnotationMapper(false);
    public static final AnnotationMapper WITH_NULL_BINDINGS = new AnnotationMapper(true);
    private final boolean withNullBindings;

    public AnnotationMapper() {
        this(false);
    }

    public AnnotationMapper(boolean withNullBindings) {
        this.withNullBindings = withNullBindings;
    }

    @Override
    public Map<Path<?>, Object> createMap(RelationalPath<?> path, Object object) {
        try {
            HashMap columnToPath = new HashMap();
            for (Path<?> column : path.getColumns()) {
                columnToPath.put(ColumnMetadata.getName(column), column);
            }
            HashMap values = new HashMap();
            for (Field field : ReflectionUtils.getFields(object.getClass())) {
                Column ann = field.getAnnotation(Column.class);
                if (ann == null) continue;
                field.setAccessible(true);
                Object propertyValue = field.get(object);
                if (propertyValue != null) {
                    if (!columnToPath.containsKey(ann.value())) continue;
                    values.put((Path<?>)columnToPath.get(ann.value()), propertyValue);
                    continue;
                }
                if (!this.withNullBindings) continue;
                values.put((Path<?>)columnToPath.get(ann.value()), Null.DEFAULT);
            }
            return values;
        }
        catch (IllegalAccessException e) {
            throw new QueryException(e);
        }
    }
}

