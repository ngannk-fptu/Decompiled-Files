/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.status.RunOutcome
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.internal.schedule.persistence.dao;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.persistence.hibernate.ConfluenceHibernateObjectDao;
import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import com.atlassian.confluence.impl.schedule.caesium.SchedulerRunDetails;
import com.atlassian.confluence.internal.schedule.persistence.dao.InternalRunDetailsDao;
import com.atlassian.confluence.schedule.managers.SchedulerRunDetailsPurgeMode;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.status.RunOutcome;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

@Internal
public class HibernateInternalRunDetailsDao
extends ConfluenceHibernateObjectDao<SchedulerRunDetails>
implements InternalRunDetailsDao {
    private static final Logger log = LoggerFactory.getLogger(HibernateInternalRunDetailsDao.class);
    private final HibernateDatabaseCapabilities databaseCapabilities;

    public HibernateInternalRunDetailsDao(HibernateDatabaseCapabilities databaseCapabilities) {
        this.databaseCapabilities = databaseCapabilities;
    }

    @Override
    public Class<SchedulerRunDetails> getPersistentClass() {
        return SchedulerRunDetails.class;
    }

    @Override
    public int purgeOldRunDetails(SchedulerRunDetailsPurgeMode purgeMode, int limit) {
        Date threshold = new Date(System.currentTimeMillis() - purgeMode.getTimeToLiveThreshold());
        JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate(this.getSessionFactory().getCurrentSession());
        StringBuilder whereClauseBuilder = new StringBuilder("start_time < ?");
        ArrayList parameters = Lists.newArrayList((Object[])new Object[]{threshold});
        if (purgeMode.equals((Object)SchedulerRunDetailsPurgeMode.UNSUCCESSFUL)) {
            String successOutcome = String.valueOf(SchedulerRunDetails.runOutcomeToChar(RunOutcome.SUCCESS));
            whereClauseBuilder.append(" AND outcome <> ?");
            parameters.add(successOutcome);
        }
        return jdbcTemplate.update(this.buildDeleteStatement(whereClauseBuilder.toString(), limit), parameters.toArray());
    }

    @Override
    public long count(Optional<JobId> jobId, long timeToLiveThreshold, RunOutcome runOutcome) {
        JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate(this.getSessionFactory().getCurrentSession());
        StringBuilder sqlBuilder = new StringBuilder("SELECT count(*) FROM scheduler_run_details");
        sqlBuilder.append(" WHERE start_time < ? ").append(" AND outcome = ? ");
        Date threshold = new Date(System.currentTimeMillis() - timeToLiveThreshold);
        ArrayList parameters = Lists.newArrayList((Object[])new Object[]{threshold});
        String runOutComeChar = String.valueOf(SchedulerRunDetails.runOutcomeToChar(runOutcome));
        parameters.add(runOutComeChar);
        if (jobId.isPresent()) {
            sqlBuilder.append(" AND job_id = ? ");
            parameters.add(jobId.get().toString());
        }
        return Objects.requireNonNull((Long)jdbcTemplate.queryForObject(sqlBuilder.toString(), parameters.toArray(), Long.class));
    }

    @Override
    public int purgeAll() {
        try {
            JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate(this.getSessionFactory().getCurrentSession());
            return jdbcTemplate.update("DELETE FROM scheduler_run_details");
        }
        catch (Exception e) {
            log.debug("", (Throwable)e);
            log.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public List<SchedulerRunDetails> getRecentRunDetails(JobId jobId, int limit) {
        return (List)this.getHibernateTemplate().execute(session -> {
            Query query = session.createNamedQuery("confluence.schedulerRunDetails_getRecentRunDetails", SchedulerRunDetails.class);
            query.setParameter("jobId", (Object)jobId.toString());
            query.setMaxResults(limit);
            return query.list();
        });
    }

    @Override
    public List<SchedulerRunDetails> getRecentRunDetails(JobId jobId) {
        return this.getRecentRunDetails(jobId, 100);
    }

    @VisibleForTesting
    String buildDeleteStatement(String whereClause, int limit) {
        StringBuilder deleteStatementBuilder = new StringBuilder();
        if (this.databaseCapabilities.isSqlServer()) {
            deleteStatementBuilder.append("DELETE TOP(").append(limit).append(") FROM ").append("scheduler_run_details");
            if (StringUtils.isNotBlank((CharSequence)whereClause)) {
                deleteStatementBuilder.append(" WHERE ").append(whereClause);
            }
        } else if (this.databaseCapabilities.isOracle()) {
            deleteStatementBuilder.append("DELETE FROM ").append("scheduler_run_details").append(" WHERE rownum < ").append(limit + 1);
            if (StringUtils.isNotBlank((CharSequence)whereClause)) {
                deleteStatementBuilder.append(" AND ").append(whereClause);
            }
        } else if (this.databaseCapabilities.isMySql() || this.databaseCapabilities.isH2()) {
            deleteStatementBuilder.append("DELETE FROM ").append("scheduler_run_details");
            if (StringUtils.isNotBlank((CharSequence)whereClause)) {
                deleteStatementBuilder.append(" WHERE ").append(whereClause);
            }
            deleteStatementBuilder.append(" LIMIT ").append(limit);
        } else if (this.databaseCapabilities.isHSQL()) {
            deleteStatementBuilder.append("DELETE FROM ").append("scheduler_run_details").append(" WHERE ").append(" rownum() < ").append(limit + 1);
            if (StringUtils.isNotBlank((CharSequence)whereClause)) {
                deleteStatementBuilder.append(" AND ").append(whereClause);
            }
        } else {
            deleteStatementBuilder.append("DELETE FROM ").append("scheduler_run_details").append(" WHERE id in (select id FROM ").append("scheduler_run_details");
            if (StringUtils.isNotBlank((CharSequence)whereClause)) {
                deleteStatementBuilder.append(" WHERE ").append(whereClause);
            }
            deleteStatementBuilder.append(" LIMIT ").append(limit).append(")");
        }
        log.debug("DELETE statement: {}", (Object)deleteStatementBuilder.toString());
        return deleteStatementBuilder.toString();
    }
}

