/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.core.spi.RunDetailsDao
 *  com.atlassian.scheduler.status.RunDetails
 *  com.atlassian.scheduler.status.RunOutcome
 *  org.hibernate.query.Query
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.impl.schedule.caesium;

import com.atlassian.confluence.core.persistence.hibernate.ConfluenceHibernateObjectDao;
import com.atlassian.confluence.impl.schedule.caesium.SchedulerRunDetails;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.core.spi.RunDetailsDao;
import com.atlassian.scheduler.status.RunDetails;
import com.atlassian.scheduler.status.RunOutcome;
import org.hibernate.query.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SchedulerRunDetailsDao
extends ConfluenceHibernateObjectDao<SchedulerRunDetails>
implements RunDetailsDao {
    @Override
    public Class<SchedulerRunDetails> getPersistentClass() {
        return SchedulerRunDetails.class;
    }

    public RunDetails getLastRunForJob(JobId jobId) {
        SchedulerRunDetails record = (SchedulerRunDetails)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("FROM SchedulerRunDetails t WHERE t.jobId = :jobId ORDER BY t.startTime DESC");
            query.setString("jobId", jobId.toString());
            query.setMaxResults(1);
            return query.uniqueResult();
        });
        return record == null ? null : record.toRunDetails();
    }

    public RunDetails getLastSuccessfulRunForJob(JobId jobId) {
        SchedulerRunDetails record = (SchedulerRunDetails)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("FROM SchedulerRunDetails t WHERE t.jobId = :jobId AND t.outcome = :outcome ORDER BY t.startTime DESC");
            query.setString("jobId", jobId.toString());
            query.setCharacter("outcome", SchedulerRunDetails.runOutcomeToChar(RunOutcome.SUCCESS));
            query.setMaxResults(1);
            return query.uniqueResult();
        });
        return record == null ? null : record.toRunDetails();
    }

    public void addRunDetails(JobId jobId, RunDetails runDetails) {
        SchedulerRunDetails record = SchedulerRunDetails.fromRunDetails(jobId, runDetails);
        this.save(record);
    }
}

