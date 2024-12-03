/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.watchdog.impl;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLMapper;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLSupplier;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.SubCalendarTable;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogStatusReporter;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogTask;
import com.atlassian.confluence.extra.calendar3.watchdog.impl.cleaner.callables.CallableBuilder;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.SQLQuery;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IncorrectInternalSubscriptionSubCalendarWatchDogTask
implements WatchDogTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(IncorrectInternalSubscriptionSubCalendarWatchDogTask.class);
    private static final int BATCH_SIZE = 1000;
    private final TransactionalHostContextAccessor hostContextAccessor;
    private final QueryDSLMapper queryDSLMapper;
    private final int runtimeBatchSize;
    private final QueryDSLSupplier sqlQuerySupplier;
    private long totalItemNeedToCleanup;
    private SQLQuery<Void> invalidSubscriptionQuery;

    @Autowired
    public IncorrectInternalSubscriptionSubCalendarWatchDogTask(@ComponentImport TransactionalHostContextAccessor hostContextAccessor, QueryDSLMapper queryDSLMapper, QueryDSLSupplier queryDSLSupplier) {
        this(hostContextAccessor, queryDSLMapper, queryDSLSupplier, 1000);
    }

    @VisibleForTesting
    public IncorrectInternalSubscriptionSubCalendarWatchDogTask(TransactionalHostContextAccessor hostContextAccessor, QueryDSLMapper queryDSLMapper, QueryDSLSupplier queryDSLSupplier, int batchSize) {
        this.hostContextAccessor = hostContextAccessor;
        this.queryDSLMapper = queryDSLMapper;
        this.sqlQuerySupplier = queryDSLSupplier;
        this.totalItemNeedToCleanup = 0L;
        this.runtimeBatchSize = batchSize;
    }

    @Override
    public boolean shouldRun() {
        SubCalendarTable childSubCalendarTable = (SubCalendarTable)this.queryDSLMapper.getMapping(SubCalendarEntity.class);
        SubCalendarTable parentSubCalendarTable = (SubCalendarTable)this.queryDSLMapper.getMapping(SubCalendarEntity.class, "parentSubCalendars");
        this.sqlQuerySupplier.executeSQLQuery(query -> {
            this.invalidSubscriptionQuery = (SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)query.from((Expression<?>)childSubCalendarTable)).innerJoin((EntityPath)parentSubCalendarTable)).on((Predicate)childSubCalendarTable.PARENT_ID.eq(parentSubCalendarTable.ID))).where(childSubCalendarTable.CREATOR.notEqualsIgnoreCase(parentSubCalendarTable.CREATOR).and(childSubCalendarTable.STORE_KEY.eq("com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore")));
            this.totalItemNeedToCleanup = this.invalidSubscriptionQuery.fetchCount();
            return null;
        });
        LOGGER.debug("Number of invalid Internal Subscription items have been detected: " + this.totalItemNeedToCleanup);
        return this.totalItemNeedToCleanup > 0L;
    }

    @Override
    public void run(WatchDogStatusReporter reporter) {
        LOGGER.debug("Cleaning up invalid Internal Subscription item");
        long totalDeletedItem = CallableBuilder.builder().withAction(() -> {
            SQLQuery queryWithLimit = (SQLQuery)((SQLQuery)this.invalidSubscriptionQuery.clone()).limit(this.runtimeBatchSize);
            SubCalendarTable subCalendarTable = (SubCalendarTable)this.queryDSLMapper.getMapping(SubCalendarEntity.class);
            return this.sqlQuerySupplier.executeDeleteSQLClause(subCalendarTable, deleteClause -> {
                List batchIdToDelete = ((AbstractSQLQuery)queryWithLimit.select((Expression)subCalendarTable.ID)).fetch();
                return deleteClause.where((Predicate)subCalendarTable.ID.in(batchIdToDelete)).execute();
            });
        }).withTransaction(this.hostContextAccessor).withBatching(this.runtimeBatchSize, this.totalItemNeedToCleanup).getBatchCallable().apply(null).stream().reduce(Long::sum).orElse(0L);
        LOGGER.debug("Number of invalid Internal Subscription items have been cleared: " + totalDeletedItem);
    }
}

