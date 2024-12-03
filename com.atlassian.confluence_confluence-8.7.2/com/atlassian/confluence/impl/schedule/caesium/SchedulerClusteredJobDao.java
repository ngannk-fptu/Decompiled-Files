/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.caesium.spi.ClusteredJob
 *  com.atlassian.scheduler.caesium.spi.ClusteredJobDao
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.query.Query
 *  org.hibernate.type.StringType
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.impl.schedule.caesium;

import com.atlassian.confluence.core.persistence.hibernate.ConfluenceHibernateObjectDao;
import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import com.atlassian.confluence.impl.schedule.caesium.SchedulerClusteredJob;
import com.atlassian.scheduler.caesium.spi.ClusteredJob;
import com.atlassian.scheduler.caesium.spi.ClusteredJobDao;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.query.Query;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation=Propagation.REQUIRES_NEW)
public class SchedulerClusteredJobDao
extends ConfluenceHibernateObjectDao<SchedulerClusteredJob>
implements ClusteredJobDao {
    private static final Logger log = LoggerFactory.getLogger(SchedulerClusteredJobDao.class);

    @Override
    public Class<SchedulerClusteredJob> getPersistentClass() {
        return SchedulerClusteredJob.class;
    }

    public @Nullable Date getNextRunTime(@NonNull JobId jobId) {
        return (Date)this.findOneFieldByJobId(jobId, "nextRunTime");
    }

    public @Nullable Long getVersion(@NonNull JobId jobId) {
        return (Long)this.findOneFieldByJobId(jobId, "version");
    }

    public @Nullable ClusteredJob find(@NonNull JobId jobId) {
        SchedulerClusteredJob record = this.findOneRecordByJobId(jobId);
        if (record == null) {
            return null;
        }
        try {
            return record.toClusteredJob();
        }
        catch (IOException e) {
            log.error("Could not read raw parameters", (Throwable)e);
            return null;
        }
    }

    public @NonNull Collection<ClusteredJob> findByJobRunnerKey(@NonNull JobRunnerKey jobRunnerKey) {
        List list = Objects.requireNonNull((List)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("FROM SchedulerClusteredJob t WHERE t.jobRunnerKey = :jobRunnerKey");
            query.setParameter("jobRunnerKey", (Object)jobRunnerKey.toString(), (Type)StringType.INSTANCE);
            return query.list();
        }));
        ArrayList<ClusteredJob> ret = new ArrayList<ClusteredJob>(list.size());
        for (Object obj : list) {
            SchedulerClusteredJob record = (SchedulerClusteredJob)obj;
            try {
                ret.add(record.toClusteredJob());
            }
            catch (IOException e) {
                log.error("Could not read raw parameters", (Throwable)e);
            }
        }
        return ret;
    }

    public @NonNull Map<JobId, Date> refresh() {
        List list = Objects.requireNonNull((List)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("SELECT t.jobId, t.nextRunTime FROM SchedulerClusteredJob t WHERE t.nextRunTime IS NOT NULL");
            return query.list();
        }));
        HashMap<JobId, Date> ret = new HashMap<JobId, Date>(list.size());
        for (Object obj : list) {
            Object[] jobId_nextRunTime = (Object[])obj;
            JobId jobId = JobId.of((String)((String)jobId_nextRunTime[0]));
            Date nextRunTime = (Date)jobId_nextRunTime[1];
            ret.put(jobId, nextRunTime);
        }
        return ret;
    }

    public @NonNull Set<JobRunnerKey> findAllJobRunnerKeys() {
        List list = Objects.requireNonNull((List)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("SELECT DISTINCT t.jobRunnerKey FROM SchedulerClusteredJob t");
            return query.list();
        }));
        HashSet<JobRunnerKey> ret = new HashSet<JobRunnerKey>(list.size());
        for (Object obj : list) {
            String jobRunnerKey = (String)obj;
            ret.add(JobRunnerKey.of((String)jobRunnerKey));
        }
        return ret;
    }

    public boolean create(@NonNull ClusteredJob clusteredJob) {
        SchedulerClusteredJob record = SchedulerClusteredJob.fromClusterJob(clusteredJob);
        this.save(record);
        this.getHibernateTemplate().flush();
        return true;
    }

    public boolean updateNextRunTime(@NonNull JobId jobId, @Nullable Date nextRunTime, long expectedVersion) {
        JdbcTemplate jdbc = DataAccessUtils.getJdbcTemplate(this.getSessionFactory().getCurrentSession());
        int numAffectedRows = jdbc.update("UPDATE scheduler_clustered_jobs SET next_run_time = ?, version = ? WHERE job_id = ? AND version = ?", new Object[]{nextRunTime, expectedVersion + 1L, jobId.toString(), expectedVersion});
        return numAffectedRows > 0;
    }

    public boolean delete(@NonNull JobId jobId) {
        JdbcTemplate jdbc = DataAccessUtils.getJdbcTemplate(this.getSessionFactory().getCurrentSession());
        int numAffectedRows = jdbc.update("DELETE FROM scheduler_clustered_jobs where job_id = ?", new Object[]{jobId.toString()});
        return numAffectedRows > 0;
    }

    private @Nullable Object findOneFieldByJobId(@NonNull JobId jobId, @NonNull String field) {
        List list = (List)this.getHibernateTemplate().execute(session -> session.createQuery("SELECT t." + field + " FROM SchedulerClusteredJob t WHERE t.jobId = :jobId").setParameter("jobId", (Object)jobId.toString(), (Type)StringType.INSTANCE).list());
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    private @Nullable SchedulerClusteredJob findOneRecordByJobId(@NonNull JobId jobId) {
        List list = (List)this.getHibernateTemplate().execute(session -> session.createQuery("FROM SchedulerClusteredJob t WHERE t.jobId = :jobId").setParameter("jobId", (Object)jobId.toString(), (Type)StringType.INSTANCE).list());
        return list == null || list.isEmpty() ? null : (SchedulerClusteredJob)list.get(0);
    }
}

