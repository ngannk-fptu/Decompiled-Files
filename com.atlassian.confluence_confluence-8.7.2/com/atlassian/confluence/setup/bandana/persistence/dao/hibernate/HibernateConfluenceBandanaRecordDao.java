/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.hibernate.type.StringType
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeanUtils
 *  org.springframework.dao.IncorrectResultSizeDataAccessException
 *  org.springframework.dao.TypeMismatchDataAccessException
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.setup.bandana.persistence.dao.hibernate;

import com.atlassian.confluence.core.persistence.hibernate.SessionHelper;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.ConfluenceLockerOnSiteRestore;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaRecord;
import com.atlassian.confluence.setup.bandana.persistence.dao.ConfluenceBandanaRecordDao;
import java.util.Collection;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class HibernateConfluenceBandanaRecordDao
implements ConfluenceBandanaRecordDao {
    private static final Logger log = LoggerFactory.getLogger(HibernateConfluenceBandanaRecordDao.class);
    private final HibernateTemplate hibernateTemplate;

    public HibernateConfluenceBandanaRecordDao(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public void saveOrUpdate(ConfluenceBandanaRecord record) {
        this.assertDatabaseIsNotLocked();
        ConfluenceBandanaRecord persistent = this.getPersistent(record);
        if (persistent == null) {
            if (record.getId() != 0L) {
                log.error("Attempted to save bandana record with ID already set. CONF-8785!");
                throw new IllegalStateException("Attempted to save a Bandana record that already has an ID. CONF-8785");
            }
            this.hibernateTemplate.save((Object)record);
        } else {
            BeanUtils.copyProperties((Object)record, (Object)persistent, (String[])new String[]{"id"});
            this.hibernateTemplate.update((Object)persistent);
        }
    }

    private void assertDatabaseIsNotLocked() {
        ConfluenceLockerOnSiteRestore.assertDatabaseIsNotLocked();
    }

    @Override
    public ConfluenceBandanaRecord getRecord(String context, String key) {
        return (ConfluenceBandanaRecord)this.hibernateTemplate.execute(session -> {
            Query query = session.getNamedQuery("confluence.bandanarecord_findByContextAndKey");
            query.setParameter("context", (Object)context);
            query.setParameter("key", (Object)key);
            List results = query.list();
            if (results.isEmpty()) {
                return null;
            }
            return results.get(0);
        });
    }

    @Override
    public void remove(ConfluenceBandanaRecord record) {
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from ConfluenceBandanaRecord cbr where cbr.context = :context and cbr.key = :key", new Object[]{record.getContext(), record.getKey()}, new Type[]{StringType.INSTANCE, StringType.INSTANCE}));
    }

    private ConfluenceBandanaRecord getPersistent(ConfluenceBandanaRecord record) {
        return this.getRecord(record.getContext(), record.getKey());
    }

    @Override
    public void removeAllInContext(String context) {
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from ConfluenceBandanaRecord cbr where cbr.context = :context", new Object[]{context}, new Type[]{StringType.INSTANCE}));
    }

    @Override
    public Collection findForContext(String context) {
        return (Collection)this.hibernateTemplate.execute(session -> {
            Query query = session.getNamedQuery("confluence.bandanarecord_findByContext");
            query.setParameter("context", (Object)context);
            return query.list();
        });
    }

    @Override
    public Iterable<String> findKeysForContext(String context) {
        return (Iterable)this.hibernateTemplate.execute(session -> {
            Query query = session.createQuery("select cbr.key from ConfluenceBandanaRecord cbr where cbr.context = :context");
            query.setParameter("context", (Object)context);
            return query.list();
        });
    }

    @Override
    public long countWithKey(String key) {
        try {
            List results = (List)this.hibernateTemplate.execute(session -> {
                Query query = session.createQuery("select count(*) from ConfluenceBandanaRecord cbr where cbr.key = :key");
                query.setParameter("key", (Object)key);
                return query.list();
            });
            return DataAccessUtils.longResult((Collection)results);
        }
        catch (IncorrectResultSizeDataAccessException | TypeMismatchDataAccessException ex) {
            log.error("Could not count record in Bandana table with key " + key, ex);
            return 0L;
        }
    }

    @Override
    public Iterable<ConfluenceBandanaRecord> findAllWithKey(String key) {
        return (Iterable)this.hibernateTemplate.execute(session -> {
            Query query = session.createQuery("from ConfluenceBandanaRecord cbr where cbr.key = :key");
            query.setParameter("key", (Object)key);
            return query.list();
        });
    }
}

