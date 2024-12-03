/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PersistenceException
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.springframework.orm.ObjectRetrievalFailureException
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.plugin.persistence.hibernate;

import com.atlassian.confluence.plugin.persistence.PluginData;
import com.atlassian.confluence.plugin.persistence.PluginDataDao;
import com.atlassian.confluence.plugin.persistence.PluginDataWithoutBinary;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.persistence.PersistenceException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class HibernatePluginDataDao
implements PluginDataDao {
    private final HibernateTemplate hibernateTemplate;

    public HibernatePluginDataDao(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    private SessionFactory getSessionFactory() {
        return Objects.requireNonNull(this.hibernateTemplate.getSessionFactory());
    }

    @Override
    public PluginData getPluginData(String key) throws ObjectRetrievalFailureException {
        List results = this.findPluginData(key);
        if (results.isEmpty()) {
            throw new ObjectRetrievalFailureException(PluginData.class, (Object)key);
        }
        return (PluginData)results.get(0);
    }

    @Override
    public PluginDataWithoutBinary getPluginDataWithoutBinary(String key) {
        List results = this.findPluginDataWithoutBinary(key);
        if (results.isEmpty()) {
            throw new ObjectRetrievalFailureException(PluginDataWithoutBinary.class, (Object)key);
        }
        return (PluginDataWithoutBinary)results.get(0);
    }

    private List findPluginData(String key) {
        return this.hibernateTemplate.findByNamedQueryAndNamedParam("confluence.pd_findPluginDataByKey", "key", (Object)key);
    }

    @Override
    public Iterator<PluginData> getAllPluginData() {
        Query namedQuery = this.getSessionFactory().getCurrentSession().getNamedQuery("confluence.pd_getAllPluginData");
        return this.hibernateTemplate.iterate(namedQuery.getQueryString(), new Object[0]);
    }

    @Override
    public Iterator<PluginDataWithoutBinary> getAllPluginDataWithoutBinary() {
        Query namedQuery = this.getSessionFactory().getCurrentSession().getNamedQuery("confluence.pd_getAllPluginDataWithoutBinary");
        return this.hibernateTemplate.iterate(namedQuery.getQueryString(), new Object[0]);
    }

    private List findPluginDataWithoutBinary(String key) {
        return this.hibernateTemplate.findByNamedQueryAndNamedParam("confluence.pd_findPluginDataByKeyWithoutBinary", "key", (Object)key);
    }

    @Override
    public void saveOrUpdate(PluginData pluginData) {
        try {
            Object o = this.getSessionFactory().getCurrentSession().get(PluginData.class, (Serializable)Long.valueOf(pluginData.getId()));
            if (o != null) {
                this.getSessionFactory().getCurrentSession().evict(o);
            }
        }
        catch (PersistenceException e) {
            throw new RuntimeException("There was a problem evicting or flushing a PluginData object", e);
        }
        pluginData.setLastModificationDate(new Date());
        this.hibernateTemplate.saveOrUpdate((Object)pluginData);
        try {
            String key = pluginData.getKey();
            Session session = this.getSessionFactory().getCurrentSession();
            session.flush();
            session.evict((Object)pluginData);
            session.evict((Object)this.getPluginDataWithoutBinary(key));
        }
        catch (HibernateException e) {
            throw new RuntimeException("There was a problem evicting or flushing a PluginData object", e);
        }
    }

    @Override
    public void remove(String key) throws ObjectRetrievalFailureException {
        this.hibernateTemplate.delete((Object)this.getPluginData(key));
    }

    @Override
    public boolean pluginDataExists(String key) {
        return !this.findPluginDataWithoutBinary(key).isEmpty();
    }
}

