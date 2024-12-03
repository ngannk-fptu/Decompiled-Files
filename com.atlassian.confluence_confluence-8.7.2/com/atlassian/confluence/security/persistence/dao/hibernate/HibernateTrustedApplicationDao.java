/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.SessionFactory
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.orm.hibernate5.HibernateTemplate
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.security.persistence.dao.hibernate;

import com.atlassian.confluence.security.persistence.dao.TrustedApplicationDao;
import com.atlassian.confluence.security.trust.ConfluenceTrustedApplication;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

public class HibernateTrustedApplicationDao
implements TrustedApplicationDao {
    private final HibernateTemplate hibernateTemplate;

    public HibernateTrustedApplicationDao(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public ConfluenceTrustedApplication findById(long id) {
        return (ConfluenceTrustedApplication)this.hibernateTemplate.get(ConfluenceTrustedApplication.class, (Serializable)Long.valueOf(id));
    }

    @Override
    public ConfluenceTrustedApplication findByKeyAlias(String keyAlias) {
        List results = this.hibernateTemplate.findByNamedQueryAndNamedParam("confluence.ta_getApplicationByKeyAlias", "keyAlias", (Object)keyAlias);
        return (ConfluenceTrustedApplication)DataAccessUtils.singleResult((Collection)results);
    }

    @Override
    public ConfluenceTrustedApplication findByName(String applicationName) {
        List results = this.hibernateTemplate.findByNamedQueryAndNamedParam("confluence.ta_getApplicationByName", "name", (Object)applicationName);
        return (ConfluenceTrustedApplication)DataAccessUtils.singleResult((Collection)results);
    }

    @Override
    public Collection<ConfluenceTrustedApplication> findAll() {
        return this.hibernateTemplate.find("from ConfluenceTrustedApplication", new Object[0]);
    }

    @Override
    @Transactional
    public void saveHibernateTrustedApplication(ConfluenceTrustedApplication trustedApplication) {
        this.hibernateTemplate.saveOrUpdate((Object)trustedApplication);
    }

    @Override
    public void deleteHibernateTrustedApplication(ConfluenceTrustedApplication trustedApplication) {
        this.hibernateTemplate.delete((Object)trustedApplication);
    }
}

