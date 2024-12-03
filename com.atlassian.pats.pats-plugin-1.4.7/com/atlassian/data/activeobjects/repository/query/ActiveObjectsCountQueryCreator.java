/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQueryCreator;
import com.atlassian.data.activeobjects.repository.query.ParameterMetadataProvider;
import com.atlassian.data.activeobjects.repository.query.QueryDSLQuery;
import com.atlassian.data.activeobjects.repository.query.Queryable;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.data.repository.query.parser.PartTree;

public class ActiveObjectsCountQueryCreator
extends ActiveObjectsQueryCreator {
    public ActiveObjectsCountQueryCreator(PartTree tree, ReturnedType returnedType, SQLQueryFactory sqlQueryFactory, ParameterMetadataProvider provider, RelationalPathBase<?> qdslType) {
        super(tree, returnedType, sqlQueryFactory, provider, qdslType);
    }

    @Override
    protected Queryable complete(Predicate predicate, Sort sort, SQLQueryFactory sqlQueryFactory) {
        SQLQuery query = this.tree.isDistinct() ? (SQLQuery)sqlQueryFactory.select((Expression)Wildcard.countDistinct).from((Expression<?>)this.qdslType) : (SQLQuery)sqlQueryFactory.select((Expression)Wildcard.count).from((Expression<?>)this.qdslType);
        query = predicate == null ? query : (SQLQuery)query.where(predicate);
        return new QueryDSLQuery(query, this.qdslType);
    }
}

