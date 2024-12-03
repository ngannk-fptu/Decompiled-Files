/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.IncorrectResultSizeDataAccessException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package com.atlassian.data.activeobjects.repository.support;

import com.atlassian.data.activeobjects.repository.support.PocketKnifeQuerydslPredicateExecutor;
import com.atlassian.data.activeobjects.repository.support.QuerydslUtils;
import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import com.atlassian.pocketknife.spi.querydsl.EnhancedRelationalPathBase;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.sql.SQLQuery;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class QuerydslPocketKnifeReadOnlyPredicateExecutor<T>
implements PocketKnifeQuerydslPredicateExecutor<T> {
    private static final Logger logger = LoggerFactory.getLogger(QuerydslPocketKnifeReadOnlyPredicateExecutor.class);
    protected static final OnRollback DEFAULT_ROLLBACK = OnRollback.NOOP;
    protected final EnhancedRelationalPathBase<T> entityPath;
    protected final DatabaseAccessor databaseAccessor;
    protected final PocketKnifeQuerydslPredicateExecutor.TransactionType defaultTransactionType;
    protected final PathBuilder<T> builder;

    public QuerydslPocketKnifeReadOnlyPredicateExecutor(EnhancedRelationalPathBase<T> entityPath, DatabaseAccessor databaseAccessor, PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType) {
        Assert.notNull(entityPath, () -> "entityPath can't be null!");
        Assert.notNull((Object)databaseAccessor, () -> "DatabaseAccessor can't be null!");
        Assert.notNull((Object)((Object)transactionType), () -> "TransactionType can't be null!");
        this.databaseAccessor = databaseAccessor;
        this.entityPath = entityPath;
        this.defaultTransactionType = transactionType;
        this.builder = new PathBuilder(entityPath.getType(), entityPath.getMetadata());
        logger.debug("builder: [{}]", this.builder);
        logger.debug("builder type: [{}]", entityPath.getType());
        logger.debug("builder metadata: [{}]", (Object)entityPath.getMetadata());
    }

    @Override
    public Optional<T> findOne(Predicate predicate) {
        Assert.notNull((Object)predicate, (String)"Predicate must not be null!");
        try {
            Object fetchOne = this.executeQuery(t -> ((SQLQuery)((SQLQuery)t.select(this.entityPath).from((Expression<?>)this.entityPath)).where(predicate)).fetchOne());
            return Optional.ofNullable(fetchOne);
        }
        catch (NonUniqueResultException ex) {
            throw new IncorrectResultSizeDataAccessException(ex.getMessage(), 1, (Throwable)ex);
        }
    }

    protected <T> T executeQuery(Function<DatabaseConnection, T> callback) {
        return this.executeQuery(this.defaultTransactionType, callback, DEFAULT_ROLLBACK);
    }

    @Override
    public <T> T executeQuery(@Nullable PocketKnifeQuerydslPredicateExecutor.TransactionType type, Function<DatabaseConnection, T> callback, @Nullable OnRollback rollback) {
        Assert.notNull(callback, (String)"Callback must not be null!");
        return this.getTransactionType(type).equals((Object)PocketKnifeQuerydslPredicateExecutor.TransactionType.IN_TRANSACTION) ? this.databaseAccessor.runInTransaction(callback, this.getRollback(rollback)) : this.databaseAccessor.runInNewTransaction(callback, this.getRollback(rollback));
    }

    private PocketKnifeQuerydslPredicateExecutor.TransactionType getTransactionType(PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType) {
        return Objects.nonNull((Object)transactionType) ? transactionType : this.defaultTransactionType;
    }

    private OnRollback getRollback(OnRollback rollback) {
        return Objects.nonNull(rollback) ? rollback : DEFAULT_ROLLBACK;
    }

    @Override
    public List<T> findAll(Predicate predicate) {
        Assert.notNull((Object)predicate, (String)"Predicate must not be null!");
        return this.executeQuery(t -> ((SQLQuery)((SQLQuery)t.select(this.entityPath).from((Expression<?>)this.entityPath)).where(predicate)).fetch());
    }

    @Override
    public List<T> findAll(Predicate predicate, OrderSpecifier<?> ... orders) {
        Assert.notNull((Object)predicate, (String)"Predicate must not be null!");
        Assert.notNull(orders, (String)"Order specifiers must not be null!");
        return this.executeQuery(t -> {
            SQLQuery query = (SQLQuery)t.select(this.entityPath).from((Expression<?>)this.entityPath);
            return this.executeSorted(query, orders);
        });
    }

    @Override
    public List<T> findAll(Predicate predicate, Sort sort) {
        Assert.notNull((Object)predicate, (String)"Predicate must not be null!");
        Assert.notNull((Object)sort, (String)"Sort must not be null!");
        return this.executeQuery(t -> {
            SQLQuery query = (SQLQuery)t.select(this.entityPath).from((Expression<?>)this.entityPath);
            return this.executeSorted(query, sort);
        });
    }

    @Override
    public List<T> findAll(OrderSpecifier<?> ... orders) {
        Assert.notNull(orders, (String)"Order specifiers must not be null!");
        return this.executeQuery(t -> {
            SQLQuery query = (SQLQuery)t.select(this.entityPath).from((Expression<?>)this.entityPath);
            return this.executeSorted(query, orders);
        });
    }

    @Override
    public Page<T> findAll(Predicate predicate, Pageable pageable) {
        Assert.notNull((Object)predicate, (String)"Predicate must not be null!");
        Assert.notNull((Object)pageable, (String)"Pageable must not be null!");
        long countQueryResult = this.createCountQuery(predicate);
        List createQueryResult = this.executeQuery(t -> {
            SQLQuery sqlQuery = (SQLQuery)((SQLQuery)t.select(this.entityPath).from((Expression<?>)this.entityPath)).where(predicate);
            this.applyPagination(pageable, sqlQuery);
            return sqlQuery.fetch();
        });
        return PageableExecutionUtils.getPage(createQueryResult, pageable, () -> countQueryResult);
    }

    private SQLQuery<T> applyPagination(Pageable pageable, SQLQuery<T> query) {
        Assert.notNull((Object)pageable, (String)"Pageable must not be null!");
        Assert.notNull(query, (String)"SQLQuery must not be null!");
        if (pageable.isUnpaged()) {
            return query;
        }
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        return QuerydslUtils.applySorting(pageable.getSort(), query, this.entityPath, this.builder);
    }

    @Override
    public long count(Predicate predicate) {
        return this.executeQuery(t -> ((SQLQuery)((SQLQuery)t.select(Wildcard.count).from((Expression<?>)this.entityPath)).where(predicate)).fetchCount());
    }

    @Override
    public boolean exists(Predicate predicate) {
        return this.count(predicate) > 0L;
    }

    protected Long createCountQuery(Predicate ... predicate) {
        return this.executeQuery(t -> ((SQLQuery)((SQLQuery)t.select(this.entityPath).from((Expression<?>)this.entityPath)).where(predicate)).fetchCount());
    }

    private List<T> executeSorted(SQLQuery<T> query, OrderSpecifier<?> ... orders) {
        return this.executeSorted(query, new QSort(orders));
    }

    private List<T> executeSorted(SQLQuery<T> query, Sort sort) {
        return QuerydslUtils.applySorting(sort, query, this.entityPath, this.builder).fetch();
    }
}

