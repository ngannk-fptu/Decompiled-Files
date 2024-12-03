/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  com.google.common.collect.Lists
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.core.BatchPreparedStatementSetter
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.datasource.SingleConnectionDataSource
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.supporters.jirareminder;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.extra.calendar3.model.persistence.JiraReminderEventEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.DTO.JiraReminderEventDTO;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.google.common.collect.Lists;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class JiraReminderPersister {
    private static Logger logger = LoggerFactory.getLogger(JiraReminderPersister.class);
    private final ActiveObjects activeObjects;
    private final TransactionalExecutorFactory transactionalExecutorFactory;
    private String quote;

    public JiraReminderPersister(ActiveObjects activeObjects, TransactionalExecutorFactory transactionalExecutorFactory) {
        this.activeObjects = activeObjects;
        this.transactionalExecutorFactory = transactionalExecutorFactory;
    }

    protected JdbcTemplate getJdbcTemplate(Connection connection) {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource(connection, true);
        return new JdbcTemplate((DataSource)dataSource);
    }

    public void insertMultiRecordWithSingleStatement(final List<JiraReminderEventDTO> jiraReminderEventDTOs) {
        this.transactionalExecutorFactory.create().execute(connection -> {
            JdbcTemplate template = this.getJdbcTemplate(connection);
            if (!jiraReminderEventDTOs.isEmpty()) {
                this.deleteJiraReminderOldEvents(jiraReminderEventDTOs);
                String insertSql = "";
                try {
                    insertSql = this.getInsertSqlStatementForJiraReminder(template.getDataSource().getConnection());
                }
                catch (SQLException ex) {
                    logger.debug("Could not construct the INSERT statement for inserting JIRA reminder event", (Throwable)ex);
                    return null;
                }
                template.batchUpdate(insertSql, new BatchPreparedStatementSetter(){

                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, ((JiraReminderEventDTO)jiraReminderEventDTOs.get(i)).getKeyId());
                        ps.setString(2, ((JiraReminderEventDTO)jiraReminderEventDTOs.get(i)).getSubCalendarId());
                        ps.setString(3, ((JiraReminderEventDTO)jiraReminderEventDTOs.get(i)).getUserId());
                        ps.setString(4, ((JiraReminderEventDTO)jiraReminderEventDTOs.get(i)).getJql());
                        ps.setString(5, ((JiraReminderEventDTO)jiraReminderEventDTOs.get(i)).getTicketId());
                        ps.setString(6, ((JiraReminderEventDTO)jiraReminderEventDTOs.get(i)).getAssignee());
                        ps.setString(7, ((JiraReminderEventDTO)jiraReminderEventDTOs.get(i)).getStatus());
                        ps.setString(8, ((JiraReminderEventDTO)jiraReminderEventDTOs.get(i)).getSummary());
                        ps.setString(9, ((JiraReminderEventDTO)jiraReminderEventDTOs.get(i)).getDescription());
                        ps.setString(10, ((JiraReminderEventDTO)jiraReminderEventDTOs.get(i)).getEventType());
                        ps.setLong(11, ((JiraReminderEventDTO)jiraReminderEventDTOs.get(i)).getUtcStart());
                        ps.setLong(12, ((JiraReminderEventDTO)jiraReminderEventDTOs.get(i)).getUtcEnd());
                        ps.setBoolean(13, ((JiraReminderEventDTO)jiraReminderEventDTOs.get(i)).isAllDay());
                        ps.setString(14, ((JiraReminderEventDTO)jiraReminderEventDTOs.get(i)).getJiraIssueLink());
                        ps.setString(15, ((JiraReminderEventDTO)jiraReminderEventDTOs.get(i)).getJiraIssueIconUrl());
                    }

                    public int getBatchSize() {
                        return jiraReminderEventDTOs.size();
                    }
                });
            }
            return null;
        });
    }

    public void deleteJiraReminderOldEvents(List<JiraReminderEventDTO> jiraReminderEventDTOs) {
        ArrayList<String> jiraIssues = new ArrayList<String>();
        for (JiraReminderEventDTO jiraReminderEventDTO : jiraReminderEventDTOs) {
            jiraIssues.add(jiraReminderEventDTO.getTicketId());
        }
        String keyId = jiraReminderEventDTOs.get(0).getKeyId();
        String jql = jiraReminderEventDTOs.get(0).getJql();
        ArrayList querySubstitutions = Lists.newArrayList((Object[])new Object[]{keyId});
        querySubstitutions.addAll(jiraIssues);
        querySubstitutions.add(new DateTime(System.currentTimeMillis(), DateTimeZone.UTC).getMillis());
        querySubstitutions.add(jql);
        JiraReminderEventEntity[] jiraReminderEventEntities = (JiraReminderEventEntity[])this.activeObjects.find(JiraReminderEventEntity.class, Query.select().where(String.format("KEY_ID = ? AND (TICKET_ID IN (%s) OR UTC_START < ? OR JQL <> ?)", StringUtils.repeat("?", ", ", jiraIssues.size())), querySubstitutions.toArray()));
        this.activeObjects.delete((RawEntity[])jiraReminderEventEntities);
    }

    private String getInsertSqlStatementForJiraReminder(Connection connection) {
        this.loadQuoteString(connection);
        return String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", this.quote("AO_950DC3_TC_JIRA_REMI_EVENTS"), this.quote("KEY_ID"), this.quote("SUB_CALENDAR_ID"), this.quote("USER_ID"), this.quote("JQL"), this.quote("TICKET_ID"), this.quote("ASSIGNEE"), this.quote("STATUS"), this.quote("TITLE"), this.quote("DESCRIPTION"), this.quote("EVENT_TYPE"), this.quote("UTC_START"), this.quote("UTC_END"), this.quote("ALL_DAY"), this.quote("ISSUE_LINK"), this.quote("ISSUE_ICON_URL"));
    }

    protected void loadQuoteString(Connection conn) {
        if (this.quote != null) {
            return;
        }
        try {
            this.quote = conn.getMetaData().getIdentifierQuoteString().trim();
        }
        catch (SQLException e) {
            logger.debug("Could not get connection to load quote String", (Throwable)e);
        }
    }

    protected final String quote(String id) {
        return this.quote + id + this.quote;
    }
}

