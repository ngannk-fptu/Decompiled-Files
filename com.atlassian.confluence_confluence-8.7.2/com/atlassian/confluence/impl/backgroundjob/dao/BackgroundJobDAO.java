/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.impl.backgroundjob.dao;

import com.atlassian.confluence.impl.backgroundjob.domain.ArchivedBackgroundJob;
import com.atlassian.confluence.impl.backgroundjob.domain.BackgroundJob;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class BackgroundJobDAO {
    private final HibernateTemplate hibernateTemplate;

    public BackgroundJobDAO(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public void save(BackgroundJob job) {
        this.hibernateTemplate.save((Object)job);
    }

    public void saveArchived(ArchivedBackgroundJob job) {
        this.hibernateTemplate.save((Object)job);
    }

    public void delete(BackgroundJob job) {
        this.hibernateTemplate.delete((Object)job);
    }

    public List<BackgroundJob> getAllJobsReadyToRunSortedById(Instant now) {
        return (List)this.hibernateTemplate.execute(session -> {
            String hql = "from BackgroundJob job where job.runAt <= :now order by id";
            Query query = session.createQuery("from BackgroundJob job where job.runAt <= :now order by id", BackgroundJob.class);
            query.setParameter("now", (Object)now);
            query.setCacheable(false);
            return query.list();
        });
    }

    public List<BackgroundJob> findActiveJobsByType(String type) {
        return (List)this.hibernateTemplate.execute(session -> {
            String hql = "from BackgroundJob job where job.type = :type";
            Query query = session.createQuery("from BackgroundJob job where job.type = :type", BackgroundJob.class);
            query.setParameter("type", (Object)type);
            query.setCacheable(false);
            return query.list();
        });
    }

    public void remove(BackgroundJob job) {
        this.hibernateTemplate.delete((Object)job);
    }

    public void removeArchivedJob(ArchivedBackgroundJob job) {
        this.hibernateTemplate.delete((Object)job);
    }

    public List<ArchivedBackgroundJob> getObsoleteArchivedJobs(Instant date, int limit) {
        return (List)this.hibernateTemplate.execute(session -> {
            String hql = "from ArchivedBackgroundJob job where job.completionTime <= :completionTime";
            Query query = session.createQuery("from ArchivedBackgroundJob job where job.completionTime <= :completionTime", ArchivedBackgroundJob.class);
            query.setParameter("completionTime", (Object)date);
            query.setCacheable(false);
            query.setMaxResults(limit);
            return query.list();
        });
    }

    public BackgroundJob getActiveJobById(long id) {
        return (BackgroundJob)this.hibernateTemplate.execute(session -> (BackgroundJob)session.get(BackgroundJob.class, (Serializable)Long.valueOf(id)));
    }

    public ArchivedBackgroundJob getArchivedJobById(long id) {
        return (ArchivedBackgroundJob)this.hibernateTemplate.execute(session -> (ArchivedBackgroundJob)session.get(ArchivedBackgroundJob.class, (Serializable)Long.valueOf(id)));
    }
}

