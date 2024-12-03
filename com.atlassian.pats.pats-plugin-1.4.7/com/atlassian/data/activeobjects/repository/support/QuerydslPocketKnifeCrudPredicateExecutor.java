/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 */
package com.atlassian.data.activeobjects.repository.support;

import com.atlassian.data.activeobjects.repository.support.PocketKnifeCrudQuerydslPredicateExecutor;
import com.atlassian.data.activeobjects.repository.support.PocketKnifeQuerydslPredicateExecutor;
import com.atlassian.data.activeobjects.repository.support.QuerydslPocketKnifeReadOnlyPredicateExecutor;
import com.atlassian.data.activeobjects.repository.support.QuerydslUtils;
import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import com.atlassian.pocketknife.spi.querydsl.EnhancedRelationalPathBase;
import com.querydsl.core.dml.StoreClause;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.DefaultMapper;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class QuerydslPocketKnifeCrudPredicateExecutor<T, ID>
extends QuerydslPocketKnifeReadOnlyPredicateExecutor<T>
implements PocketKnifeCrudQuerydslPredicateExecutor<T, ID> {
    private static final Logger logger = LoggerFactory.getLogger(QuerydslPocketKnifeCrudPredicateExecutor.class);
    protected final Path<ID> primaryKeyPath;
    protected final SimplePath<ID> primaryKeySimplePath;

    public QuerydslPocketKnifeCrudPredicateExecutor(EnhancedRelationalPathBase<T> entityPath, DatabaseAccessor databaseAccessor, PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType) {
        super(entityPath, databaseAccessor, transactionType);
        this.primaryKeyPath = QuerydslUtils.getPrimaryKeyForEntity(entityPath);
        this.primaryKeySimplePath = QuerydslUtils.getSimplePathForPrimaryKey(entityPath, this.primaryKeyPath);
    }

    @Override
    public <S extends T> S save(S entity, @Nullable PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType, @Nullable OnRollback rollback) {
        Map<Path<?>, Object> beanMap = DefaultMapper.DEFAULT.createMap((RelationalPath<?>)this.entityPath, (Object)entity);
        Object entityId = beanMap.get(this.primaryKeyPath);
        return !this.isIdDefined(entityId) ? this.insert(entity, beanMap, transactionType, rollback) : this.update(entity, entityId, beanMap, transactionType, rollback);
    }

    private boolean isIdDefined(ID entityId) {
        if (Objects.nonNull(entityId)) {
            if (entityId instanceof Number && ((Number)entityId).intValue() == 0) {
                return false;
            }
            String idValue = String.valueOf(entityId);
            return !idValue.isEmpty();
        }
        return false;
    }

    private <S extends T> S insert(S entity, Map<Path<?>, Object> beanMap, PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType, OnRollback rollback) {
        return (S)this.executeQuery(transactionType, (DatabaseConnection t) -> {
            SQLInsertClause insert = t.insert(this.entityPath);
            this.populate(beanMap, insert);
            ID newId = insert.executeWithKey(this.primaryKeyPath);
            return Objects.nonNull(newId) ? this.applyNewId(entity, newId) : entity;
        }, rollback);
    }

    private <S extends T> S applyNewId(S entity, Object executeWithKey) {
        Field field = ReflectionUtils.findField(entity.getClass(), (String)QuerydslUtils.toDotPath(this.primaryKeyPath), this.primaryKeyPath.getType());
        ReflectionUtils.makeAccessible((Field)field);
        ReflectionUtils.setField((Field)field, entity, (Object)executeWithKey);
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) {
        return this.findOne(QuerydslPocketKnifeCrudPredicateExecutor.createPredicateForPkEqId(id, this.primaryKeySimplePath));
    }

    @Override
    public List<T> findAll(Sort sort) {
        return this.findAll((Predicate)Expressions.asBoolean(true).isTrue(), sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return this.findAll((Predicate)Expressions.asBoolean(true).isTrue(), pageable);
    }

    private <S extends T> S update(S entity, ID entityId, Map<Path<?>, Object> beanMap, PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType, OnRollback rollback) {
        return (S)this.executeQuery(transactionType, (DatabaseConnection t) -> {
            Assert.notNull((Object)entityId, () -> String.format("ID for entity: [%s] can't be null!", entity));
            SQLUpdateClause sqlUpdateClause = t.update(this.entityPath).where((Predicate)this.primaryKeySimplePath.eq(entityId));
            this.populate(beanMap, sqlUpdateClause);
            long execute = sqlUpdateClause.execute();
            logger.debug("Updated num entities: [{}]", (Object)execute);
            return entity;
        }, rollback);
    }

    private StoreClause populate(Map<Path<?>, Object> createMap, StoreClause storeClause) {
        boolean isInsert = storeClause instanceof SQLInsertClause;
        for (Map.Entry<Path<?>, Object> entry : createMap.entrySet()) {
            if (!isInsert && this.primaryKeyPath.equals(entry.getKey())) continue;
            storeClause.set(entry.getKey(), entry.getValue());
        }
        return storeClause;
    }

    @Override
    public long count() {
        return this.executeQuery(t -> ((SQLQuery)t.select(Wildcard.count).from((Expression<?>)this.entityPath)).fetchCount());
    }

    @Override
    public long deleteAll(@Nullable PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType, @Nullable OnRollback rollback) {
        return this.executeQuery(t -> t.delete(this.entityPath).execute());
    }

    @Override
    public <S extends T> List<S> saveAll(@Nullable PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType, @Nullable OnRollback rollback, S ... entities) {
        Assert.notEmpty((Object[])entities, () -> "Supplied entities can't be null/empty!");
        return Stream.of(entities).map(t -> this.save(t, transactionType, rollback)).collect(Collectors.toList());
    }

    @Override
    public boolean existsById(ID id) {
        return this.exists(QuerydslPocketKnifeCrudPredicateExecutor.createPredicateForPkEqId(id, this.primaryKeySimplePath));
    }

    private static BooleanExpression createPredicateForPkEqId(Object id, SimplePath<?> primaryKeySimplePath) {
        Assert.notNull((Object)id, () -> "ID can't be null!");
        return primaryKeySimplePath.eq(Expressions.constant(id));
    }

    @Override
    public long deleteById(ID id, @Nullable PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType, @Nullable OnRollback rollback) {
        return this.delete(QuerydslPocketKnifeCrudPredicateExecutor.createPredicateForPkEqId(id, this.primaryKeySimplePath), transactionType, rollback);
    }

    @Override
    public long delete(Predicate predicate, @Nullable PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType, @Nullable OnRollback rollback) {
        return this.executeQuery(transactionType, (DatabaseConnection t) -> t.delete(this.entityPath).where(predicate).execute(), rollback);
    }

    @Override
    public long delete(T entity, @Nullable PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType, @Nullable OnRollback rollback) {
        return this.delete(QuerydslPocketKnifeCrudPredicateExecutor.createPkIdPredicate(entity, this.entityPath, this.primaryKeyPath, this.primaryKeySimplePath), transactionType, rollback);
    }

    public static BooleanExpression createPkIdPredicate(Object entity, RelationalPathBase<?> entityPath, Path<?> primaryKeyPath, SimplePath<?> primaryKeySimplePath) {
        Map<Path<?>, Object> beanMap = DefaultMapper.DEFAULT.createMap((RelationalPath<?>)entityPath, entity);
        Object idValue = beanMap.get(primaryKeyPath);
        return QuerydslPocketKnifeCrudPredicateExecutor.createPredicateForPkEqId(idValue, primaryKeySimplePath);
    }

    @Override
    public long deleteAll(@Nullable PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType, @Nullable OnRollback rollback, T ... entities) {
        Assert.notEmpty((Object[])entities, () -> "Supplied entities can't be null/empty!");
        return Stream.of(entities).map(t -> this.delete(t, transactionType, rollback)).collect(Collectors.counting());
    }

    @Override
    public <S extends T> S save(S entity) {
        return this.save(entity, this.defaultTransactionType, DEFAULT_ROLLBACK);
    }

    @Override
    public <S extends T> List<S> saveAll(S ... entities) {
        return this.saveAll(this.defaultTransactionType, DEFAULT_ROLLBACK, entities);
    }

    @Override
    public long delete(Predicate predicate) {
        return this.delete(predicate, this.defaultTransactionType, DEFAULT_ROLLBACK);
    }

    @Override
    public long deleteById(ID id) {
        return this.deleteById(id, this.defaultTransactionType, DEFAULT_ROLLBACK);
    }

    @Override
    public long delete(T entity) {
        return this.delete(entity, this.defaultTransactionType, DEFAULT_ROLLBACK);
    }

    @Override
    public long deleteAll(T ... entities) {
        return this.deleteAll(this.defaultTransactionType, DEFAULT_ROLLBACK, entities);
    }

    @Override
    public long deleteAll() {
        return this.deleteAll(this.defaultTransactionType, DEFAULT_ROLLBACK);
    }
}

