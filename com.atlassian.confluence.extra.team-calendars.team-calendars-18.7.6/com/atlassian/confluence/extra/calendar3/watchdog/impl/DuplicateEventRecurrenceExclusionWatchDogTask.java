/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.watchdog.impl;

import com.atlassian.confluence.extra.calendar3.model.persistence.EventRecurrenceExclusionEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLMapper;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLSupplier;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.EventRecurrenceExclusionTable;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogStatusReporter;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogTask;
import com.atlassian.confluence.extra.calendar3.watchdog.impl.DuplicatedEventRecurrenceExclusion;
import com.atlassian.confluence.extra.calendar3.watchdog.impl.cleaner.EventDuplicatedDataCleaner;
import com.atlassian.confluence.extra.calendar3.watchdog.impl.cleaner.EventDuplicatedDataCleanerSpec;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.annotations.VisibleForTesting;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.ProjectableSQLQuery;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLDeleteClause;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DuplicateEventRecurrenceExclusionWatchDogTask
implements WatchDogTask,
EventDuplicatedDataCleanerSpec<DuplicatedEventRecurrenceExclusion> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateEventRecurrenceExclusionWatchDogTask.class);
    private static final int NUM_DUPLICATED_EVENT_PER_LOOP = 100;
    private static final int NUM_DELETE_ROW_PER_LOOP = 1000;
    private final TransactionalHostContextAccessor hostContextAccessor;
    private final QueryDSLMapper queryDSLMapper;
    private QueryDSLSupplier sqlQuerySupplier;

    @Autowired
    @VisibleForTesting
    public DuplicateEventRecurrenceExclusionWatchDogTask(@ComponentImport TransactionalHostContextAccessor hostContextAccessor, QueryDSLMapper queryDSLMapper, QueryDSLSupplier queryDSLSupplier) {
        this.hostContextAccessor = hostContextAccessor;
        this.queryDSLMapper = queryDSLMapper;
        this.sqlQuerySupplier = queryDSLSupplier;
    }

    @Override
    public boolean shouldRun() {
        EventRecurrenceExclusionTable exclusionTable = (EventRecurrenceExclusionTable)this.queryDSLMapper.getMapping(EventRecurrenceExclusionEntity.class);
        boolean hasDuplication = this.sqlQuerySupplier.executeSQLQuery(query -> ((SQLQuery)((SQLQuery)((SQLQuery)((ProjectableSQLQuery)query.select(exclusionTable.EVENT_ID)).from((Expression<?>)exclusionTable)).groupBy((Expression<?>[])new Expression[]{exclusionTable.EVENT_ID, exclusionTable.EXCLUSION, exclusionTable.ALL_DAY})).having(exclusionTable.EXCLUSION.count().gt(1))).fetchFirst() != null);
        if (hasDuplication) {
            LOGGER.info("Duplicate data has been detected in the event exclusion table. A clean up task will be run.");
        }
        return hasDuplication;
    }

    @Override
    public void run(WatchDogStatusReporter reporter) {
        EventDuplicatedDataCleaner<DuplicatedEventRecurrenceExclusion> duplicationCleaner = new EventDuplicatedDataCleaner<DuplicatedEventRecurrenceExclusion>(this.hostContextAccessor, 100, 1000, this, reporter);
        duplicationCleaner.cleanData();
    }

    @Override
    public Collection<DuplicatedEventRecurrenceExclusion> getDuplicatedDTOs(int duplicatedEventPage, int duplicateEventLassOffset) {
        EventRecurrenceExclusionTable exclusionTable = (EventRecurrenceExclusionTable)this.queryDSLMapper.getMapping(EventRecurrenceExclusionEntity.class);
        return this.sqlQuerySupplier.executeSQLQuery(query -> ((AbstractSQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)query.from((Expression<?>)exclusionTable)).groupBy((Expression<?>[])new Expression[]{exclusionTable.EVENT_ID, exclusionTable.EXCLUSION, exclusionTable.ALL_DAY})).having(exclusionTable.EXCLUSION.count().gt(1))).orderBy((OrderSpecifier<?>)exclusionTable.EXCLUSION.count().asc())).limit(duplicatedEventPage)).offset(duplicateEventLassOffset)).select(Projections.constructor(DuplicatedEventRecurrenceExclusion.class, exclusionTable.ID.min().as("MIN_ID"), exclusionTable.EVENT_ID, exclusionTable.EXCLUSION, exclusionTable.ALL_DAY))).fetch());
    }

    @Override
    public long getDuplicatedCountPerDTO(DuplicatedEventRecurrenceExclusion duplicatedDTO) {
        EventRecurrenceExclusionTable exclusionTable = (EventRecurrenceExclusionTable)this.queryDSLMapper.getMapping(EventRecurrenceExclusionEntity.class);
        return this.sqlQuerySupplier.executeSQLQuery(query -> {
            SQLQuery countDuplicatedPerEventQuery = (SQLQuery)((SQLQuery)((SQLQuery)((ProjectableSQLQuery)query.select(exclusionTable.ID)).from((Expression<?>)exclusionTable)).groupBy((Expression<?>[])new Expression[]{exclusionTable.EVENT_ID, exclusionTable.EXCLUSION, exclusionTable.ALL_DAY})).where(exclusionTable.ID.eq(duplicatedDTO.getId()).and(exclusionTable.EVENT_ID.eq(duplicatedDTO.getEventId())).and(exclusionTable.EXCLUSION.eq(duplicatedDTO.getExclusion())).and(exclusionTable.ALL_DAY.eq(duplicatedDTO.isAllDay())));
            return countDuplicatedPerEventQuery.fetchCount();
        });
    }

    @Override
    public long deleteDuplicatedRow(int limit, DuplicatedEventRecurrenceExclusion duplicatedDTO) {
        EventRecurrenceExclusionTable exclusionTable = (EventRecurrenceExclusionTable)this.queryDSLMapper.getMapping(EventRecurrenceExclusionEntity.class);
        return this.sqlQuerySupplier.executeDeleteSQLClause(exclusionTable, sqlDeleteClause -> {
            SQLDeleteClause deleteClause = sqlDeleteClause;
            deleteClause.where((Predicate)exclusionTable.ID.ne(duplicatedDTO.getId()).and(exclusionTable.EVENT_ID.eq(duplicatedDTO.getEventId())).and(exclusionTable.EXCLUSION.eq(duplicatedDTO.getExclusion())).and(exclusionTable.ALL_DAY.eq(duplicatedDTO.isAllDay())));
            if (limit > 1) {
                LOGGER.debug("Going to set the limit for the delete statement to [{}]", (Object)limit);
                deleteClause.limit(limit);
            }
            long deletedRow = deleteClause.execute();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Deleted [{}] rows in the {} table", (Object)deletedRow, (Object)exclusionTable);
            }
            return deletedRow;
        });
    }
}

