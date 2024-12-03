/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.manage.dao;

import com.atlassian.confluence.security.denormalisedpermissions.StateChangeInformation;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedServiceStateChangeLog;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class DenormalisedChangeLogDao {
    private final HibernateTemplate hibernateTemplate;

    public DenormalisedChangeLogDao(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public List<DenormalisedServiceStateChangeLog> getLastRecords(int limit) {
        return (List)this.hibernateTemplate.execute(session -> {
            String hqlQuery = "from DenormalisedServiceStateChangeLog order by id desc";
            Query query = session.createQuery(hqlQuery, DenormalisedServiceStateChangeLog.class);
            query.setMaxResults(limit);
            query.setCacheable(false);
            return query.list();
        });
    }

    public Object clearHistory() {
        return this.hibernateTemplate.execute(session -> {
            session.createQuery("delete from DenormalisedServiceStateChangeLog").executeUpdate();
            return null;
        });
    }

    public void addMessage(StateChangeInformation.MessageLevel level, String message) {
        DenormalisedServiceStateChangeLog log = new DenormalisedServiceStateChangeLog();
        log.setMessage(message);
        log.setMessageLevel(level.name());
        log.setTimestamp(System.currentTimeMillis());
        this.hibernateTemplate.save((Object)log);
    }
}

