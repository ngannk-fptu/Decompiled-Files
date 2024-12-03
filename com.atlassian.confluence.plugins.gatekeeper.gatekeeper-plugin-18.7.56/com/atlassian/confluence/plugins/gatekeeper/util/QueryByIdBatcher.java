/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.PersistenceException
 *  javax.persistence.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryByIdBatcher {
    private static final Logger logger = LoggerFactory.getLogger(QueryByIdBatcher.class);
    private static final int DEFAULT_BATCH_SIZE = Integer.getInteger("gatekeeper.query.batch.size", 1000);
    private final int batchSize;
    private final EntityManager entityManager;
    private final String queryStart;
    private final String queryBatched;
    private Long startingId = null;
    private final HashMap<String, Object> extraParameters = new HashMap();

    public QueryByIdBatcher(EntityManager entityManager, String queryStart, String queryBatched) {
        this(entityManager, queryStart, queryBatched, DEFAULT_BATCH_SIZE);
    }

    public QueryByIdBatcher(EntityManager entityManager, String queryStart, String queryBatched, int batchSize) {
        this.entityManager = entityManager;
        this.queryStart = queryStart;
        this.queryBatched = queryBatched;
        this.batchSize = batchSize;
    }

    public List<Object[]> getBatch() {
        Query query;
        if (this.startingId != null) {
            query = this.entityManager.createQuery(this.queryBatched);
            query.setParameter("id", (Object)this.startingId);
        } else {
            query = this.entityManager.createQuery(this.queryStart);
        }
        if (!this.extraParameters.isEmpty()) {
            for (String fieldName : this.extraParameters.keySet()) {
                query.setParameter(fieldName, this.extraParameters.get(fieldName));
            }
        }
        query.setMaxResults(this.batchSize);
        List<Object[]> queryResults = new ArrayList();
        try {
            queryResults = query.getResultList();
        }
        catch (IllegalStateException | PersistenceException e) {
            logger.error("Loading from database failed.", e);
        }
        if (!queryResults.isEmpty()) {
            this.startingId = (Long)queryResults.get(queryResults.size() - 1)[0];
        }
        return queryResults;
    }

    public void addQueryParameter(String fieldName, Object fieldValue) {
        this.extraParameters.put(fieldName, fieldValue);
    }

    public long getStartingId() {
        return this.startingId;
    }

    public int getBatchSize() {
        return this.batchSize;
    }
}

