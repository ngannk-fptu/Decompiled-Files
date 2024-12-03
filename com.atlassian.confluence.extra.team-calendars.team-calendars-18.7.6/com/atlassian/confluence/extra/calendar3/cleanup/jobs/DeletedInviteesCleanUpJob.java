/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.cleanup.jobs;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLMapper;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLSupplier;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.EventTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.InviteeTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.SubCalendarTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.internal.CwdUserTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.internal.UserMappingTable;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.google.common.collect.Iterables;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.sql.ProjectableSQLQuery;
import com.querydsl.sql.SQLQuery;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeletedInviteesCleanUpJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(DeletedInviteesCleanUpJob.class);
    private static final String JOB_KEY = "com.atlassian.confluence.extra.team-calendars." + DeletedInviteesCleanUpJob.class.getSimpleName();
    private static final String LAST_EVENT_PROPERTY = "lastEventId";
    private static final String CURRENT_BATCH_SIZE_PROPERTY = "batchSize";
    private static final String SUBCALENDAR_ID_NAME = "SUBCAL_ID";
    private static final int DELETION_BATCH_SIZE = 500;
    private final QueryDSLMapper queryDSLMapper;
    private final QueryDSLSupplier sqlQuerySupplier;
    private final CalendarManager calendarManager;
    private final PluginSettings pluginSettings;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public DeletedInviteesCleanUpJob(QueryDSLMapper queryDSLMapper, QueryDSLSupplier sqlQuerySupplier, @ComponentImport PluginSettingsFactory pluginSettingsFactory, CalendarManager calendarManager, @ComponentImport TransactionTemplate transactionTemplate) {
        this.queryDSLMapper = queryDSLMapper;
        this.sqlQuerySupplier = sqlQuerySupplier;
        this.calendarManager = calendarManager;
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
        this.transactionTemplate = transactionTemplate;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        return (JobRunnerResponse)this.transactionTemplate.execute(this::doCleanUp);
    }

    @VisibleForTesting
    protected JobRunnerResponse doCleanUp() {
        try {
            if (this.shouldRun()) {
                this.cleanUpDeletedInvitees();
            } else {
                this.saveJobProperties(0, 0L);
            }
        }
        catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.debug("Error executing job: " + e.getMessage(), (Throwable)e);
            } else {
                log.error("Error executing job: {}. Turn on debug logging to see full stacktrace", (Object)e.getMessage());
            }
            return JobRunnerResponse.failed((Throwable)e);
        }
        return JobRunnerResponse.success();
    }

    private boolean shouldRun() {
        long lastExaminedEventId = this.getLastEventId().orElse(0).intValue();
        return this.sqlQuerySupplier.executeSQLQuery(deletedUserContentQuery -> this.baseQueryForDeletedInvitees(deletedUserContentQuery, lastExaminedEventId).fetchFirst() != null);
    }

    private void saveJobProperties(int eventId, long batchSize) {
        Properties jobProperties = new Properties();
        jobProperties.setProperty(LAST_EVENT_PROPERTY, String.valueOf(eventId));
        jobProperties.setProperty(CURRENT_BATCH_SIZE_PROPERTY, String.valueOf(batchSize));
        this.pluginSettings.put(JOB_KEY, (Object)jobProperties);
    }

    private void cleanUpDeletedInvitees() throws SQLException {
        InviteeTable inviteeTable = this.queryDSLMapper.getInviteeTable();
        SubCalendarTable subCalendarTable = this.queryDSLMapper.getSubCalendarTable();
        EventTable eventsTable = this.queryDSLMapper.getEventsTable();
        int lastExaminedEventId = this.getLastEventId().orElse(0);
        long batchSize = lastExaminedEventId == 0 ? this.computeBatchSize(inviteeTable) : Math.max(this.getCurrentBatchSize().orElse(0L), this.computeBatchSize(inviteeTable));
        AtomicReference sqlExceptionIfAny = new AtomicReference();
        this.sqlQuerySupplier.executeSQLQuery(deletedUserContentQuery -> {
            this.baseQueryForDeletedInvitees(deletedUserContentQuery, lastExaminedEventId).limit(batchSize);
            ResultSet deletedUserContent = deletedUserContentQuery.getResults(inviteeTable.EVENT_ID, inviteeTable.INVITEE_ID, eventsTable.VEVENT_UID, subCalendarTable.ID.as(SUBCALENDAR_ID_NAME));
            HashSet<Integer> eventIds = new HashSet<Integer>();
            HashSet<String> inviteeIds = new HashSet<String>();
            HashMap<String, Set<String>> vEventUidsBySubCalendar = new HashMap<String, Set<String>>();
            int lastEventId = 0;
            try {
                while (deletedUserContent.next()) {
                    int eventId = deletedUserContent.getInt(inviteeTable.EVENT_ID.getMetadata().getName());
                    String inviteeId = deletedUserContent.getString(inviteeTable.INVITEE_ID.getMetadata().getName());
                    String vEventUid = deletedUserContent.getString(eventsTable.VEVENT_UID.getMetadata().getName());
                    String subCalId = deletedUserContent.getString(SUBCALENDAR_ID_NAME);
                    vEventUidsBySubCalendar.computeIfAbsent(subCalId, k -> new HashSet()).add(vEventUid);
                    eventIds.add(eventId);
                    inviteeIds.add(inviteeId);
                    lastEventId = eventId;
                }
            }
            catch (SQLException e) {
                sqlExceptionIfAny.set(e);
            }
            this.batchDeleteInviteeEntries(inviteeIds, eventIds, 500);
            this.updateExistingEvents(vEventUidsBySubCalendar);
            this.saveJobProperties(lastEventId, batchSize);
            return null;
        });
        if (sqlExceptionIfAny.get() != null) {
            throw (SQLException)sqlExceptionIfAny.get();
        }
    }

    private SQLQuery<Integer> baseQueryForDeletedInvitees(SQLQuery<Void> query, long lastExaminedEventId) {
        InviteeTable inviteeTable = this.queryDSLMapper.getInviteeTable();
        SubCalendarTable subCalendarTable = this.queryDSLMapper.getSubCalendarTable();
        EventTable eventsTable = this.queryDSLMapper.getEventsTable();
        CwdUserTable cwdUserTable = this.queryDSLMapper.getCwdUserTable();
        UserMappingTable userMappingTable = this.queryDSLMapper.getUserMappingTable();
        return (SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((ProjectableSQLQuery)((SQLQuery)query.from((Expression<?>)inviteeTable)).select(inviteeTable.ID)).join((EntityPath)eventsTable)).on((Predicate)eventsTable.ID.eq(inviteeTable.EVENT_ID))).join((EntityPath)subCalendarTable)).on((Predicate)subCalendarTable.ID.eq(eventsTable.SUB_CALENDAR_ID))).leftJoin((EntityPath)userMappingTable)).on((Predicate)userMappingTable.USER_KEY.eq(inviteeTable.INVITEE_ID))).leftJoin((EntityPath)cwdUserTable)).on((Predicate)cwdUserTable.USER_NAME.eq(userMappingTable.USERNAME))).where(this.userDeletedCondition().or(userMappingTable.USER_KEY.isNull()).and(inviteeTable.EVENT_ID.gt(lastExaminedEventId)))).orderBy((OrderSpecifier<?>)inviteeTable.EVENT_ID.asc());
    }

    private Optional<Integer> getLastEventId() {
        return Optional.ofNullable((Properties)this.pluginSettings.get(JOB_KEY)).map(properties -> properties.getProperty(LAST_EVENT_PROPERTY)).map(Integer::parseInt);
    }

    private Optional<Long> getCurrentBatchSize() {
        return Optional.ofNullable((Properties)this.pluginSettings.get(JOB_KEY)).map(properties -> properties.getProperty(CURRENT_BATCH_SIZE_PROPERTY)).map(Long::parseLong);
    }

    @VisibleForTesting
    protected long computeBatchSize(InviteeTable inviteeTable) {
        return this.sqlQuerySupplier.executeSQLQuery(query -> {
            long inviteeTableLength = ((SQLQuery)query.from((Expression<?>)inviteeTable)).fetchCount();
            return Math.floorDiv(inviteeTableLength, 28) + 1L;
        });
    }

    private BooleanExpression userDeletedCondition() {
        UserMappingTable userMappingTable = this.queryDSLMapper.getUserMappingTable();
        CwdUserTable cwdUserTable = this.queryDSLMapper.getCwdUserTable();
        return userMappingTable.USER_KEY.eq(userMappingTable.USERNAME).and(cwdUserTable.USER_NAME.isNull());
    }

    private void batchDeleteInviteeEntries(Collection<String> userInviteeIds, Collection<Integer> eventIds, int batchSize) {
        Iterables.partition(userInviteeIds, (int)batchSize).forEach(inviteeIdSubList -> Iterables.partition((Iterable)eventIds, (int)batchSize).forEach(eventIdSubList -> {
            long removedCount = this.deleteInvitees((Collection<String>)inviteeIdSubList, (Collection<Integer>)eventIdSubList);
            log.info("Removed {} invitee entries", (Object)removedCount);
        }));
    }

    private void updateExistingEvents(Map<String, Set<String>> vEventUidsBySubCalendar) {
        vEventUidsBySubCalendar.keySet().stream().forEach(subCalendarId -> {
            PersistedSubCalendar subCalendar = (PersistedSubCalendar)this.calendarManager.getPersistedSubCalendar((String)subCalendarId).orNull();
            if (subCalendar != null) {
                try {
                    this.calendarManager.updateOrRemoveInvalidExistingEvents(subCalendar, (Collection)vEventUidsBySubCalendar.get(subCalendarId));
                }
                catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Failed to update subCalendar with id [" + subCalendar.getName() + "]", (Throwable)e);
                    }
                    log.error("Failed to update subCalendar with id [{}]: {}. Turn on debug logging to see stacktrace", (Object)subCalendar.getName(), (Object)e.getMessage());
                }
            }
        });
    }

    private long deleteInvitees(Collection<String> inviteeIds, Collection<Integer> eventIds) {
        InviteeTable inviteeTable = this.queryDSLMapper.getInviteeTable();
        return this.sqlQuerySupplier.executeDeleteSQLClause(inviteeTable, sqlDeleteClause -> {
            sqlDeleteClause = sqlDeleteClause.where((Predicate)inviteeTable.INVITEE_ID.in(inviteeIds).and(inviteeTable.EVENT_ID.in(eventIds)));
            return sqlDeleteClause.execute();
        });
    }
}

