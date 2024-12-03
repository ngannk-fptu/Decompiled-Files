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

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.model.persistence.InviteeEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLMapper;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLSupplier;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.InviteeTable;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogStatusReporter;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogTask;
import com.atlassian.confluence.extra.calendar3.watchdog.impl.DuplicatedInvitee;
import com.atlassian.confluence.extra.calendar3.watchdog.impl.cleaner.EventDuplicatedDataCleaner;
import com.atlassian.confluence.extra.calendar3.watchdog.impl.cleaner.EventDuplicatedDataCleanerSpec;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.annotations.VisibleForTesting;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.ProjectableSQLQuery;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLDeleteClause;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DuplicateInviteeWatchDogTask
implements WatchDogTask,
EventDuplicatedDataCleanerSpec<DuplicatedInvitee> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateInviteeWatchDogTask.class);
    private static final int NUM_DUPLICATED_EVENT_PER_LOOP = 100;
    private static final int NUM_DELETE_ROW_PER_LOOP = 1000;
    private final TransactionalHostContextAccessor hostContextAccessor;
    private final ActiveObjectsServiceWrapper activeObjectsServiceWrapper;
    private final QueryDSLMapper queryDSLMapper;
    private final int numDuplicatedEventPerLoop;
    private final int numDeleteRowPerLoop;
    private QueryDSLSupplier sqlQuerySupplier;

    @Autowired
    public DuplicateInviteeWatchDogTask(@ComponentImport TransactionalHostContextAccessor hostContextAccessor, ActiveObjectsServiceWrapper activeObjectsServiceWrapper, QueryDSLMapper queryDSLMapper, QueryDSLSupplier queryDSLSupplier) {
        this(hostContextAccessor, activeObjectsServiceWrapper, queryDSLMapper, queryDSLSupplier, 100, 1000);
    }

    @VisibleForTesting
    public DuplicateInviteeWatchDogTask(TransactionalHostContextAccessor hostContextAccessor, ActiveObjectsServiceWrapper activeObjectsServiceWrapper, QueryDSLMapper queryDSLMapper, QueryDSLSupplier queryDSLSupplier, int numDuplicatedEventPerLoop, int numDeleteRowPerLoop) {
        this.hostContextAccessor = hostContextAccessor;
        this.activeObjectsServiceWrapper = activeObjectsServiceWrapper;
        this.queryDSLMapper = queryDSLMapper;
        this.sqlQuerySupplier = queryDSLSupplier;
        this.numDuplicatedEventPerLoop = numDuplicatedEventPerLoop;
        this.numDeleteRowPerLoop = numDeleteRowPerLoop;
    }

    @Override
    public boolean shouldRun() {
        InviteeTable inviteeTable = (InviteeTable)this.queryDSLMapper.getMapping(InviteeEntity.class);
        boolean hasDuplication = this.sqlQuerySupplier.executeSQLQuery(query -> ((SQLQuery)((SQLQuery)((QueryBase)((Object)((SQLQuery)query.from((Expression<?>)inviteeTable)).select(inviteeTable.EVENT_ID))).groupBy(inviteeTable.EVENT_ID, inviteeTable.INVITEE_ID)).having(inviteeTable.INVITEE_ID.count().gt(1))).fetchFirst() != null);
        if (hasDuplication) {
            LOGGER.info("Duplicate data has been detected in the invitee table. A clean up task will be run.");
        }
        return hasDuplication;
    }

    @Override
    public void run(WatchDogStatusReporter reporter) {
        EventDuplicatedDataCleaner<DuplicatedInvitee> duplicationCleaner = new EventDuplicatedDataCleaner<DuplicatedInvitee>(this.hostContextAccessor, this.numDuplicatedEventPerLoop, this.numDeleteRowPerLoop, this, reporter);
        duplicationCleaner.cleanData();
    }

    @Override
    public Collection<DuplicatedInvitee> getDuplicatedDTOs(int duplicatedEventPage, int duplicateEventLassOffset) {
        InviteeTable inviteeTable = (InviteeTable)this.queryDSLMapper.getMapping(InviteeEntity.class);
        return this.sqlQuerySupplier.executeSQLQuery(query -> ((AbstractSQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)query.from((Expression<?>)inviteeTable)).groupBy((Expression<?>[])new Expression[]{inviteeTable.EVENT_ID, inviteeTable.INVITEE_ID})).having(inviteeTable.INVITEE_ID.count().gt(1))).orderBy((OrderSpecifier<?>)inviteeTable.INVITEE_ID.count().asc())).limit(duplicatedEventPage)).offset(duplicateEventLassOffset)).select(Projections.constructor(DuplicatedInvitee.class, inviteeTable.ID.min().as("MIN_ID"), inviteeTable.EVENT_ID, inviteeTable.INVITEE_ID))).fetch());
    }

    @Override
    public long getDuplicatedCountPerDTO(DuplicatedInvitee duplicatedDTO) {
        InviteeTable inviteeTable = (InviteeTable)this.queryDSLMapper.getMapping(InviteeEntity.class);
        return this.sqlQuerySupplier.executeSQLQuery(query -> {
            SQLQuery countDuplicatedPerEventQuery = (SQLQuery)((SQLQuery)((SQLQuery)((ProjectableSQLQuery)query.select(inviteeTable.ID)).from((Expression<?>)inviteeTable)).groupBy((Expression<?>[])new Expression[]{inviteeTable.EVENT_ID, inviteeTable.INVITEE_ID})).where(inviteeTable.ID.ne(duplicatedDTO.getId()).and(inviteeTable.EVENT_ID.eq(duplicatedDTO.getEventId())).and(inviteeTable.INVITEE_ID.eq(duplicatedDTO.getInviteeId())));
            long numberOfDuplicationPerEvent = countDuplicatedPerEventQuery.fetchCount();
            LOGGER.debug("Found {} duplicate(s) on event id {} and invitee id {}", new Object[]{numberOfDuplicationPerEvent, duplicatedDTO.getEventId(), duplicatedDTO.getInviteeId()});
            return numberOfDuplicationPerEvent;
        });
    }

    @Override
    public long deleteDuplicatedRow(int limit, DuplicatedInvitee duplicatedInvitee) {
        InviteeTable inviteeTable = (InviteeTable)this.queryDSLMapper.getMapping(InviteeEntity.class);
        BooleanExpression whereClause = inviteeTable.ID.ne(duplicatedInvitee.getId()).and(inviteeTable.EVENT_ID.eq(duplicatedInvitee.getEventId())).and(inviteeTable.INVITEE_ID.eq(duplicatedInvitee.getInviteeId()));
        return this.sqlQuerySupplier.executeDeleteSQLClause(inviteeTable, sqlDeleteClause -> {
            SQLDeleteClause deleteClause;
            if (limit < 0) {
                deleteClause = sqlDeleteClause;
                deleteClause.where((Predicate)whereClause);
            } else {
                List batchIdToDelete = this.sqlQuerySupplier.executeSQLQuery(query -> ((AbstractSQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)query.from((Expression<?>)inviteeTable)).where(whereClause)).limit(limit)).select(inviteeTable.ID)).fetch());
                deleteClause = sqlDeleteClause.where((Predicate)inviteeTable.ID.in(batchIdToDelete));
            }
            long deletedRow = deleteClause.execute();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Deleted [{}] duplicate rows in the {} table", (Object)deletedRow, (Object)inviteeTable);
            }
            return deletedRow;
        });
    }
}

