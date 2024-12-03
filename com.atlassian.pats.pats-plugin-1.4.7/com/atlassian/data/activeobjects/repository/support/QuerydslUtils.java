/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 */
package com.atlassian.data.activeobjects.repository.support;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathType;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.querydsl.QSort;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public final class QuerydslUtils {
    private static final Logger log = LoggerFactory.getLogger(QuerydslUtils.class);
    public static final boolean QUERY_DSL_PRESENT = ClassUtils.isPresent((String)"com.querydsl.core.types.Predicate", (ClassLoader)QuerydslUtils.class.getClassLoader());

    public static String toDotPath(Path<?> path) {
        return QuerydslUtils.toDotPath(path, "");
    }

    private static String toDotPath(@Nullable Path<?> path, String tail) {
        if (path == null) {
            return tail;
        }
        PathMetadata metadata = path.getMetadata();
        Path<?> parent = metadata.getParent();
        if (parent == null) {
            return tail;
        }
        if (metadata.getPathType().equals(PathType.DELEGATE)) {
            return QuerydslUtils.toDotPath(parent, tail);
        }
        Object element = metadata.getElement();
        if (element == null || !StringUtils.hasText((String)element.toString())) {
            return QuerydslUtils.toDotPath(parent, tail);
        }
        return QuerydslUtils.toDotPath(parent, StringUtils.hasText((String)tail) ? String.format("%s.%s", element, tail) : element.toString());
    }

    public static <T> SQLQuery<T> applySorting(Sort sort, SQLQuery<T> query, RelationalPathBase<?> path, PathBuilder<?> builder) {
        Assert.notNull((Object)sort, (String)"Sort must not be null!");
        Assert.notNull(query, (String)"Query must not be null!");
        if (sort.isUnsorted()) {
            return query;
        }
        if (sort instanceof QSort) {
            return QuerydslUtils.addOrderByFrom((QSort)sort, query);
        }
        return QuerydslUtils.addOrderByFrom(sort, query, path, builder);
    }

    private static <T> SQLQuery<T> addOrderByFrom(Sort sort, SQLQuery<T> query, RelationalPathBase<?> path, PathBuilder<?> builder) {
        Assert.notNull((Object)sort, (String)"Sort must not be null!");
        Assert.notNull(query, (String)"Query must not be null!");
        for (Sort.Order order : sort) {
            query.orderBy((OrderSpecifier<?>)QuerydslUtils.toOrderSpecifier(order, path, builder));
        }
        return query;
    }

    private static <T> SQLQuery<T> addOrderByFrom(QSort qsort, SQLQuery<T> query) {
        List<OrderSpecifier<?>> orderSpecifiers = qsort.getOrderSpecifiers();
        return (SQLQuery)query.orderBy((OrderSpecifier<?>[])orderSpecifiers.toArray(new OrderSpecifier[0]));
    }

    public static OrderSpecifier toOrderSpecifier(Sort.Order order, RelationalPathBase<?> path, PathBuilder<?> builder) {
        return new OrderSpecifier(order.isAscending() ? Order.ASC : Order.DESC, QuerydslUtils.buildOrderPropertyPathFrom(order, path, builder), QuerydslUtils.toQueryDslNullHandling(order.getNullHandling()));
    }

    private static OrderSpecifier.NullHandling toQueryDslNullHandling(Sort.NullHandling nullHandling) {
        Assert.notNull((Object)((Object)nullHandling), (String)"NullHandling must not be null!");
        switch (nullHandling) {
            case NULLS_FIRST: {
                return OrderSpecifier.NullHandling.NullsFirst;
            }
            case NULLS_LAST: {
                return OrderSpecifier.NullHandling.NullsLast;
            }
        }
        return OrderSpecifier.NullHandling.Default;
    }

    public static Expression<?> buildOrderPropertyPathFrom(Sort.Order order, RelationalPathBase<?> path, PathBuilder<?> builder) {
        Assert.notNull((Object)order, (String)"Order must not be null!");
        return QuerydslUtils.getExpressionForProperty(order.getProperty(), path, builder);
    }

    public static <T> SimplePath<T> getExpressionForProperty(String propertyName, RelationalPathBase<?> path, PathBuilder<?> builder) {
        PropertyPath propertyPath = PropertyPath.from(propertyName, builder.getType());
        log.debug("Got propertyPath: [{}]", (Object)propertyPath);
        return Expressions.path(propertyPath.getType(), path, propertyName);
    }

    public static <ID> Path<ID> getPrimaryKeyForEntity(RelationalPathBase<?> entityPath) {
        PrimaryKey<?> primaryKey = entityPath.getPrimaryKey();
        Assert.isTrue((Objects.nonNull(primaryKey) && 1 == primaryKey.getLocalColumns().size() ? 1 : 0) != 0, () -> "PK doesn't exist or is composite primary key - not supported in AO!");
        Path<?> pkPath = primaryKey.getLocalColumns().get(0);
        log.debug("Found PK: [{}]: [{}] for entity: [{}]", new Object[]{pkPath, pkPath.getType(), entityPath});
        return pkPath;
    }

    public static <ID> SimplePath<ID> getSimplePathForPrimaryKey(RelationalPathBase<?> root, Path<ID> pkPath) {
        return Expressions.path(pkPath.getType(), root, QuerydslUtils.toDotPath(pkPath));
    }

    private QuerydslUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

