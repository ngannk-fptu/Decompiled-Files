/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.RelationalPath;
import java.util.LinkedHashMap;

public final class RelationalPathUtils {
    public static <T> FactoryExpression<T> createProjection(RelationalPath<T> path) {
        if (path.getType().equals(path.getClass())) {
            throw new IllegalArgumentException("RelationalPath based projection can only be used with generated Bean types");
        }
        try {
            path.getType().getConstructor(new Class[0]);
            return RelationalPathUtils.createBeanProjection(path);
        }
        catch (NoSuchMethodException e) {
            return RelationalPathUtils.createConstructorProjection(path);
        }
    }

    private static <T> FactoryExpression<T> createConstructorProjection(RelationalPath<T> path) {
        Expression[] exprs = path.getColumns().toArray(new Expression[path.getColumns().size()]);
        return Projections.constructor(path.getType(), exprs);
    }

    private static <T> FactoryExpression<T> createBeanProjection(RelationalPath<T> path) {
        LinkedHashMap bindings = new LinkedHashMap();
        for (Path<?> column : path.getColumns()) {
            bindings.put(column.getMetadata().getName(), column);
        }
        if (bindings.isEmpty()) {
            throw new IllegalArgumentException("No bindings could be derived from " + path);
        }
        return Projections.fields(path.getType(), bindings);
    }

    private RelationalPathUtils() {
    }
}

