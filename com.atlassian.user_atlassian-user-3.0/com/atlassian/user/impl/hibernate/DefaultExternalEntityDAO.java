/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.hibernate.HibernateException
 *  net.sf.hibernate.Query
 *  net.sf.hibernate.Session
 *  net.sf.hibernate.SessionFactory
 *  org.springframework.orm.hibernate.HibernateCallback
 *  org.springframework.orm.hibernate.SessionFactoryUtils
 *  org.springframework.orm.hibernate.support.HibernateDaoSupport
 */
package com.atlassian.user.impl.hibernate;

import com.atlassian.user.ExternalEntity;
import com.atlassian.user.impl.hibernate.DefaultHibernateExternalEntity;
import com.atlassian.user.impl.hibernate.ExternalEntityDAO;
import java.util.Iterator;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

public class DefaultExternalEntityDAO
extends HibernateDaoSupport
implements ExternalEntityDAO {
    public static final String EXTERNAL_ENTITY_PREFIX = "ATLUSER_";
    public static final String EXTERNAL_ENTITY_TYPE = "EXT";

    public DefaultExternalEntityDAO(SessionFactory sessionFactory) {
        this.setSessionFactory(sessionFactory);
    }

    public ExternalEntity getExternalEntity(final String externalEntityName) {
        return (ExternalEntity)this.getHibernateTemplate().execute(new HibernateCallback(){

            public Object doInHibernate(Session session) throws HibernateException {
                Query queryObject = session.getNamedQuery("atluser.externalEntity_findExternalEntity");
                SessionFactoryUtils.applyTransactionTimeout((Query)queryObject, (SessionFactory)DefaultExternalEntityDAO.this.getSessionFactory());
                Iterator iterator = queryObject.setString("externalEntityName", externalEntityName).iterate();
                if (iterator.hasNext()) {
                    return iterator.next();
                }
                return null;
            }
        });
    }

    public void removeExternalEntity(String externalEntityName) {
        this.getHibernateTemplate().delete((Object)this.getExternalEntity(externalEntityName));
        this.getHibernateTemplate().flush();
    }

    public void saveExternalEntity(ExternalEntity externalEntity) {
        this.getHibernateTemplate().save((Object)externalEntity);
        this.getHibernateTemplate().flush();
    }

    public ExternalEntity createExternalEntity(String externalEntityName) {
        DefaultHibernateExternalEntity externalEntity = new DefaultHibernateExternalEntity();
        externalEntity.setName(externalEntityName);
        externalEntity.setType(EXTERNAL_ENTITY_TYPE);
        this.saveExternalEntity(externalEntity);
        return externalEntity;
    }
}

