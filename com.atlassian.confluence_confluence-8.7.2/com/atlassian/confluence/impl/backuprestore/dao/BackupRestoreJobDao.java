/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.api.model.backuprestore.JobState
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.springframework.orm.hibernate5.HibernateTemplate
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.impl.backuprestore.dao;

import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobState;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreJobsSearchFilter;
import com.atlassian.confluence.core.persistence.hibernate.SessionHelper;
import com.atlassian.confluence.impl.backuprestore.domain.BackupRestoreJobSettingsRecord;
import com.atlassian.confluence.impl.backuprestore.domain.BackupRestoreJobStatisticsRecord;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class BackupRestoreJobDao {
    public static final String NEW_STATE = "newState";
    public static final String EXISTING_EXPECTED_STATE = "existingExpectedState";
    public static final String CANCEL_TIME = "cancelTime";
    public static final String TERMINATOR = "terminator";
    public static final String ID = "id";
    public static final String FINISH_PROCESSING_TIME = "finishProcessingTime";
    public static final String START_PROCESSING_TIME = "startProcessingTime";
    public static final String STATISTICS = "statistics";
    private final SessionFactory sessionFactory;
    private final HibernateTemplate hibernateTemplate;

    public BackupRestoreJobDao(@Nonnull SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Transactional(readOnly=true)
    public BackupRestoreJob getById(Long jobId) {
        return (BackupRestoreJob)this.sessionFactory.getCurrentSession().get(BackupRestoreJob.class, (Serializable)jobId);
    }

    @Transactional
    public BackupRestoreJob save(BackupRestoreJob job) {
        this.hibernateTemplate.saveOrUpdate((Object)job);
        return job;
    }

    @Transactional
    public void update(BackupRestoreJob job) {
        this.sessionFactory.getCurrentSession().merge((Object)job);
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void updateInNewTransaction(BackupRestoreJob job) {
        this.sessionFactory.getCurrentSession().merge((Object)job);
    }

    @Transactional(readOnly=true)
    public List<BackupRestoreJob> findJobs(BackupRestoreJobsSearchFilter backupRestoreJobsSearchFilter) {
        Collection<JobState> jobStates = backupRestoreJobsSearchFilter.getJobStates();
        String spaceKey = backupRestoreJobsSearchFilter.getSpaceKey();
        String owner = backupRestoreJobsSearchFilter.getOwner();
        Instant dateFrom = backupRestoreJobsSearchFilter.getDateFrom();
        Instant dateTo = backupRestoreJobsSearchFilter.getDateTo();
        Integer limit = backupRestoreJobsSearchFilter.getLimit();
        JobScope jobScope = backupRestoreJobsSearchFilter.getJobScope();
        JobOperation jobOperation = backupRestoreJobsSearchFilter.getJobOperation();
        StringBuilder hql = new StringBuilder("from BackupRestoreJob job");
        ArrayList<String> hqlConditions = new ArrayList<String>();
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        if (!jobStates.isEmpty()) {
            hqlConditions.add("jobState in (:states)");
            parameters.put("states", jobStates);
        }
        if (!StringUtils.isEmpty((CharSequence)spaceKey)) {
            hqlConditions.add("singleSpaceKey = :singleSpaceKey");
            parameters.put("singleSpaceKey", spaceKey);
        }
        if (!StringUtils.isEmpty((CharSequence)owner)) {
            hqlConditions.add("owner = :owner");
            parameters.put("owner", owner);
        }
        if (dateFrom != null) {
            hqlConditions.add("createTime >= :dateFrom");
            parameters.put("dateFrom", dateFrom);
        }
        if (dateTo != null) {
            hqlConditions.add("createTime < :dateTo");
            parameters.put("dateTo", dateTo);
        }
        if (jobScope != null) {
            hqlConditions.add("scope = :scope");
            parameters.put("scope", jobScope.toString());
        }
        if (jobOperation != null) {
            hqlConditions.add("operation = :operation");
            parameters.put("operation", jobOperation.toString());
        }
        if (!hqlConditions.isEmpty()) {
            hql.append(" where ");
            hql.append(String.join((CharSequence)" AND ", hqlConditions));
        }
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql.toString(), BackupRestoreJob.class);
        parameters.forEach((arg_0, arg_1) -> ((Query)query).setParameter(arg_0, arg_1));
        query.setCacheable(false);
        if (limit != null) {
            query.setMaxResults(limit.intValue());
        }
        return query.list();
    }

    @Transactional
    public boolean cancelRunningJobWithOptimisticLock(long jobId, String terminator) {
        Query query = this.sessionFactory.getCurrentSession().createQuery("UPDATE BackupRestoreJob SET jobState = :newState, whoCancelledTheJob = :terminator, cancelTime = :cancelTime WHERE id = :id AND jobState IN (:existingExpectedState)");
        query.setParameter(ID, (Object)jobId);
        query.setParameter(NEW_STATE, (Object)JobState.CANCELLING);
        query.setParameter(TERMINATOR, (Object)terminator);
        query.setParameter(CANCEL_TIME, (Object)Instant.now().truncatedTo(ChronoUnit.SECONDS));
        query.setParameter(EXISTING_EXPECTED_STATE, Arrays.stream(JobState.values()).filter(JobState::isCancellable).collect(Collectors.toList()));
        int queryResult = query.executeUpdate();
        this.sessionFactory.getCurrentSession().evict((Object)this.getById(jobId));
        return queryResult > 0;
    }

    @Transactional
    public boolean cancelQueuedJobWithOptimisticLock(long jobId, String terminator) {
        Instant time = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Query query = this.sessionFactory.getCurrentSession().createQuery("UPDATE BackupRestoreJob SET jobState = :newState, whoCancelledTheJob = :terminator, cancelTime = :cancelTime, finishProcessingTime = :finishProcessingTime WHERE id = :id AND jobState = :existingExpectedState");
        query.setParameter(ID, (Object)jobId);
        query.setParameter(NEW_STATE, (Object)JobState.CANCELLED);
        query.setParameter(TERMINATOR, (Object)terminator);
        query.setParameter(CANCEL_TIME, (Object)time);
        query.setParameter(FINISH_PROCESSING_TIME, (Object)time);
        query.setParameter(EXISTING_EXPECTED_STATE, (Object)JobState.QUEUED);
        int queryResult = query.executeUpdate();
        this.sessionFactory.getCurrentSession().evict((Object)this.getById(jobId));
        return queryResult > 0;
    }

    @Transactional
    public boolean startProcessingJobWithOptimisticLock(long jobId) {
        Query query = this.sessionFactory.getCurrentSession().createQuery("UPDATE BackupRestoreJob SET jobState = :newState, startProcessingTime = :startProcessingTime WHERE id = :id AND jobState = :existingExpectedState");
        query.setParameter(ID, (Object)jobId);
        query.setParameter(NEW_STATE, (Object)JobState.PROCESSING);
        query.setParameter(START_PROCESSING_TIME, (Object)Instant.now().truncatedTo(ChronoUnit.SECONDS));
        query.setParameter(EXISTING_EXPECTED_STATE, (Object)JobState.QUEUED);
        int queryResult = query.executeUpdate();
        this.sessionFactory.getCurrentSession().evict((Object)this.getById(jobId));
        return queryResult > 0;
    }

    @Transactional(readOnly=true)
    public BackupRestoreJob getNextJobForProcessing() {
        Query query = this.sessionFactory.getCurrentSession().createQuery("from BackupRestoreJob WHERE state = 'QUEUED' order by id", BackupRestoreJob.class);
        query.setMaxResults(1);
        return (BackupRestoreJob)query.uniqueResult();
    }

    @Transactional(readOnly=true)
    public BackupRestoreJob getNextActiveJob() {
        Query query = this.sessionFactory.getCurrentSession().createQuery("from BackupRestoreJob WHERE jobState IN (:existingExpectedState) order by id", BackupRestoreJob.class);
        query.setParameter(EXISTING_EXPECTED_STATE, List.of(JobState.PROCESSING, JobState.CANCELLING, JobState.COMPLETING));
        query.setMaxResults(1);
        return (BackupRestoreJob)query.uniqueResult();
    }

    @Transactional(readOnly=true)
    public BackupRestoreJobSettingsRecord getSettingsById(long id) throws IllegalStateException {
        BackupRestoreJobSettingsRecord settingsRecord = (BackupRestoreJobSettingsRecord)this.sessionFactory.getCurrentSession().get(BackupRestoreJobSettingsRecord.class, (Serializable)Long.valueOf(id));
        if (settingsRecord == null) {
            throw new IllegalStateException("Unable to find settings for the job with id " + id);
        }
        return settingsRecord;
    }

    @Transactional(readOnly=true)
    public BackupRestoreJobStatisticsRecord getStatisticsById(long id) {
        return (BackupRestoreJobStatisticsRecord)this.sessionFactory.getCurrentSession().get(BackupRestoreJobStatisticsRecord.class, (Serializable)Long.valueOf(id));
    }

    @Transactional
    public Long save(BackupRestoreJobSettingsRecord backupRestoreJobSettingsRecord) {
        return (Long)this.hibernateTemplate.save((Object)backupRestoreJobSettingsRecord);
    }

    @Transactional
    public void save(BackupRestoreJobStatisticsRecord statisticsRecord) {
        this.hibernateTemplate.save((Object)statisticsRecord);
    }

    @Transactional
    public int updateStatistics(long jobId, String statistics) {
        Query query = this.sessionFactory.getCurrentSession().createQuery("UPDATE BackupRestoreJobStatisticsRecord SET statistics = :statistics WHERE id = :id");
        query.setParameter(STATISTICS, (Object)statistics);
        query.setParameter(ID, (Object)jobId);
        int result = query.executeUpdate();
        this.sessionFactory.getCurrentSession().evict((Object)this.getStatisticsById(jobId));
        return result;
    }

    @Transactional
    public BackupRestoreJob saveAndKeepId(BackupRestoreJob job) {
        SessionHelper.save(this.sessionFactory.getCurrentSession(), job, job.getId());
        return job;
    }

    @Transactional(readOnly=true)
    public List<BackupRestoreJob> findJobsWithExpiredZips() {
        Query query = this.sessionFactory.getCurrentSession().createQuery("from BackupRestoreJob WHERE fileDeleteTime is not null AND fileExists is true AND fileDeleteTime <= :currtime", BackupRestoreJob.class);
        query.setParameter("currtime", (Object)Instant.now());
        query.setCacheable(false);
        return query.list();
    }

    @Transactional
    public void delete(long jobId) {
        BackupRestoreJobStatisticsRecord statisticsToDelete;
        BackupRestoreJobSettingsRecord settingsToDelete;
        Session session = this.sessionFactory.getCurrentSession();
        BackupRestoreJob detailsToDelete = (BackupRestoreJob)session.get(BackupRestoreJob.class, (Serializable)Long.valueOf(jobId));
        if (detailsToDelete != null) {
            session.remove((Object)detailsToDelete);
        }
        if ((settingsToDelete = (BackupRestoreJobSettingsRecord)session.get(BackupRestoreJobSettingsRecord.class, (Serializable)Long.valueOf(jobId))) != null) {
            session.remove((Object)settingsToDelete);
        }
        if ((statisticsToDelete = (BackupRestoreJobStatisticsRecord)session.get(BackupRestoreJobStatisticsRecord.class, (Serializable)Long.valueOf(jobId))) != null) {
            session.remove((Object)statisticsToDelete);
        }
    }
}

