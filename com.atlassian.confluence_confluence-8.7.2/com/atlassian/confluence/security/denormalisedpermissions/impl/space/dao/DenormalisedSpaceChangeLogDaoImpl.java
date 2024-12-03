/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.CriteriaDelete
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.space.dao;

import com.atlassian.confluence.security.denormalisedpermissions.impl.space.dao.DenormalisedSpaceChangeLogDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain.DenormalisedSpaceChangeLog;
import java.util.List;
import javax.persistence.criteria.CriteriaDelete;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class DenormalisedSpaceChangeLogDaoImpl
implements DenormalisedSpaceChangeLogDao {
    private static final Logger log = LoggerFactory.getLogger(DenormalisedSpaceChangeLogDaoImpl.class);
    private final HibernateTemplate hibernateTemplate;

    public DenormalisedSpaceChangeLogDaoImpl(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public List<DenormalisedSpaceChangeLog> findSpaceChangeLogRecords(int limit) {
        return (List)this.hibernateTemplate.execute(session -> {
            String hqlQuery = "from DenormalisedSpaceChangeLog order by id";
            Query query = session.createQuery(hqlQuery, DenormalisedSpaceChangeLog.class);
            query.setCacheable(false);
            query.setMaxResults(limit);
            return query.list();
        });
    }

    @Override
    public void removeSpaceChangeLogRecords(List<DenormalisedSpaceChangeLog> processedRecords) {
        this.hibernateTemplate.execute(session -> {
            processedRecords.forEach(arg_0 -> ((HibernateTemplate)this.hibernateTemplate).delete(arg_0));
            log.debug("Removed {} space change log records", (Object)processedRecords.size());
            return null;
        });
    }

    @Override
    public void saveRecord(DenormalisedSpaceChangeLog log) {
        this.hibernateTemplate.execute(session -> {
            this.hibernateTemplate.save((Object)log);
            return null;
        });
    }

    @Override
    public List<Long> getAllChangedSpaceIds() {
        return (List)this.hibernateTemplate.execute(session -> {
            String hqlQuery = "select distinct log.spaceId from DenormalisedSpaceChangeLog log";
            Query query = session.createQuery(hqlQuery, Long.class);
            query.setCacheable(false);
            return query.list();
        });
    }

    @Override
    public void removeAllSpaceChangeLogRecords() {
        this.hibernateTemplate.execute(session -> {
            CriteriaDelete criteriaDelete = session.getCriteriaBuilder().createCriteriaDelete(DenormalisedSpaceChangeLog.class);
            criteriaDelete.from(DenormalisedSpaceChangeLog.class);
            session.createQuery(criteriaDelete).executeUpdate();
            return null;
        });
    }
}

