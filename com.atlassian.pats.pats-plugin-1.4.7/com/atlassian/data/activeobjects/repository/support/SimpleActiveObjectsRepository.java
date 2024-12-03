/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.google.common.collect.Lists
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Repository
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package com.atlassian.data.activeobjects.repository.support;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.data.activeobjects.repository.ActiveObjectsRepository;
import com.atlassian.data.activeobjects.repository.support.ActiveObjectsEntityInformation;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Repository
public class SimpleActiveObjectsRepository<T extends RawEntity<ID>, ID>
implements ActiveObjectsRepository<RawEntity<ID>, ID> {
    private static final Logger logger = LoggerFactory.getLogger(SimpleActiveObjectsRepository.class);
    private final ActiveObjectsEntityInformation<T, ID> entityInformation;
    private final ActiveObjects activeObjects;

    public SimpleActiveObjectsRepository(ActiveObjectsEntityInformation<T, ID> entityInformation, ActiveObjects activeObjects) {
        Assert.notNull(entityInformation, (String)"ActiveObjectsEntityInformation must not be null!");
        Assert.notNull((Object)activeObjects, (String)"EntityManager must not be null!");
        this.entityInformation = entityInformation;
        this.activeObjects = activeObjects;
    }

    @Override
    public Page<RawEntity<ID>> findAll(Pageable pageable) {
        if (pageable.isUnpaged()) {
            return new PageImpl<RawEntity<ID>>(this.findAll());
        }
        Query query = Query.select().offset((int)pageable.getOffset()).limit(pageable.getPageSize());
        if (pageable.getSort().isSorted()) {
            String orderByClause = StringUtils.collectionToCommaDelimitedString(SimpleActiveObjectsRepository.toOrders(pageable.getSort()));
            query.setOrderClause(orderByClause);
        }
        List<RawEntity<ID>> results = this.findByQuery(query);
        return new PageImpl<RawEntity<ID>>(results, pageable, this.count());
    }

    @Override
    public List<RawEntity<ID>> findByQuery(Query query) {
        return Arrays.asList(this.activeObjects.find(this.entityInformation.getJavaType(), query));
    }

    @Override
    public <S extends RawEntity<ID>> S save(S entity) {
        entity.save();
        return entity;
    }

    @Override
    public T save(Map<String, Object> params) {
        return (T)this.activeObjects.create(this.entityInformation.getJavaType(), params);
    }

    @Override
    public void saveAllEntities(List<Map<String, Object>> paramList) {
        for (Map<String, Object> params : paramList) {
            this.save(params);
        }
    }

    @Override
    public Optional<RawEntity<ID>> findById(ID id) {
        Class javaType = this.entityInformation.getJavaType();
        return Optional.ofNullable(this.getById(javaType, id));
    }

    private <T extends RawEntity<ID>, ID> T getById(Class<T> javaType, ID id) {
        return (T)this.activeObjects.get(javaType, id);
    }

    @Override
    public boolean existsById(ID id) {
        return this.findById(id).isPresent();
    }

    @Override
    public long count() {
        return this.activeObjects.count(this.entityInformation.getJavaType());
    }

    @Override
    public long count(Query query) {
        return this.activeObjects.count(this.entityInformation.getJavaType(), query);
    }

    @Override
    public void deleteById(ID id) {
        this.activeObjects.deleteWithSQL(this.entityInformation.getJavaType(), "ID = ?", new Object[]{id});
    }

    @Override
    public void delete(RawEntity<ID> entity) {
        this.activeObjects.delete(new RawEntity[]{entity});
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends RawEntity<ID>> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        int results = this.activeObjects.deleteWithSQL(this.entityInformation.getJavaType(), null, new Object[0]);
        logger.debug("Deleted num entities: [{}]", (Object)results);
    }

    @Override
    public List<RawEntity<ID>> findAll(Sort sort) {
        String orderByClause = StringUtils.collectionToCommaDelimitedString(SimpleActiveObjectsRepository.toOrders(sort));
        Query query = !orderByClause.isEmpty() ? Query.select().order(orderByClause) : Query.select();
        return this.findByQuery(query);
    }

    public static List<String> toOrders(Sort sort) {
        if (sort.isUnsorted()) {
            return Collections.emptyList();
        }
        ArrayList<String> orders = new ArrayList<String>();
        for (Sort.Order order : sort) {
            orders.add(String.format("%s %s", order.getProperty(), order.getDirection().toString()));
        }
        return orders;
    }

    @Override
    public List<RawEntity<ID>> findAllById(Iterable<ID> ids) {
        ArrayList newArrayList = Lists.newArrayList(ids);
        Query query = Query.select().from(this.entityInformation.getJavaType()).where(String.format("ID in (%s)", StringUtils.collectionToCommaDelimitedString((Collection)newArrayList)), new Object[0]);
        return Arrays.asList(this.activeObjects.find(this.entityInformation.getJavaType(), query));
    }

    @Override
    public <S extends RawEntity<ID>> List<S> saveAll(Iterable<S> entities) {
        entities.forEach(RawEntity::save);
        return Lists.newArrayList(entities);
    }

    @Override
    public void deleteInBatch(Iterable<RawEntity<ID>> entities) {
        this.deleteAll((Iterable<? extends RawEntity<ID>>)entities);
    }

    @Override
    public void flushAll() {
        this.activeObjects.flushAll();
    }

    @Override
    public List<RawEntity<ID>> findAll() {
        return Arrays.asList(this.activeObjects.find(this.entityInformation.getJavaType()));
    }
}

