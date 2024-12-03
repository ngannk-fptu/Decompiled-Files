/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.persistence.criteria.CriteriaDelete
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.content.dao;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.DenormalisedContentChangeLog;
import java.util.List;
import javax.persistence.criteria.CriteriaDelete;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class DenormalisedContentChangeLogDao {
    private static final Logger log = LoggerFactory.getLogger(DenormalisedContentChangeLogDao.class);
    private final HibernateTemplate hibernateTemplate;

    public DenormalisedContentChangeLogDao(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public List<DenormalisedContentChangeLog> findContentChangeLogRecords(int limit) {
        return (List)this.hibernateTemplate.execute(session -> {
            String hqlQuery = "from DenormalisedContentChangeLog order by id";
            Query query = session.createQuery(hqlQuery, DenormalisedContentChangeLog.class);
            query.setCacheable(false);
            query.setMaxResults(limit);
            return query.list();
        });
    }

    public void removeContentChangeLogRecords(List<DenormalisedContentChangeLog> processedRecords) {
        this.hibernateTemplate.execute(session -> {
            processedRecords.forEach(arg_0 -> ((HibernateTemplate)this.hibernateTemplate).delete(arg_0));
            log.debug("Removed {} content change log records", (Object)processedRecords.size());
            return null;
        });
    }

    public void removeAllContentChangeLogRecords() {
        this.hibernateTemplate.execute(session -> {
            CriteriaDelete criteriaDelete = session.getCriteriaBuilder().createCriteriaDelete(DenormalisedContentChangeLog.class);
            criteriaDelete.from(DenormalisedContentChangeLog.class);
            session.createQuery(criteriaDelete).executeUpdate();
            return null;
        });
    }

    @VisibleForTesting
    public void saveRecord(DenormalisedContentChangeLog log) {
        this.hibernateTemplate.execute(session -> {
            this.hibernateTemplate.save((Object)log);
            return null;
        });
    }
}

