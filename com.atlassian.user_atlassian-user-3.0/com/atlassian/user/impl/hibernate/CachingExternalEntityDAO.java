/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  net.sf.hibernate.SessionFactory
 */
package com.atlassian.user.impl.hibernate;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.user.ExternalEntity;
import com.atlassian.user.impl.hibernate.DefaultExternalEntityDAO;
import com.atlassian.user.impl.hibernate.DefaultHibernateExternalEntity;
import java.io.Serializable;
import net.sf.hibernate.SessionFactory;

public class CachingExternalEntityDAO
extends DefaultExternalEntityDAO {
    private CacheFactory cacheFactory;

    public CachingExternalEntityDAO(SessionFactory sessionFactory, CacheFactory cacheFactory) {
        super(sessionFactory);
        this.cacheFactory = cacheFactory;
    }

    public ExternalEntity getExternalEntity(String externalEntityName) {
        Long id = (Long)this.getCache().get((Object)externalEntityName);
        if (id != null) {
            return (ExternalEntity)this.getHibernateTemplate().get(DefaultHibernateExternalEntity.class, (Serializable)id);
        }
        ExternalEntity externalEntity = super.getExternalEntity(externalEntityName);
        if (externalEntity != null) {
            this.cacheEntity(externalEntityName, externalEntity);
        }
        return externalEntity;
    }

    public void removeExternalEntity(String externalEntityName) {
        this.getCache().remove((Object)externalEntityName);
        super.removeExternalEntity(externalEntityName);
    }

    public ExternalEntity createExternalEntity(String externalEntityName) {
        ExternalEntity entity = super.createExternalEntity(externalEntityName);
        this.cacheEntity(externalEntityName, entity);
        return entity;
    }

    private Cache getCache() {
        return this.cacheFactory.getCache(this.getClass().getName() + ".externalEntityName");
    }

    private void cacheEntity(String externalEntityName, ExternalEntity entity) {
        this.getCache().put((Object)externalEntityName, (Object)new Long(entity.getId()));
    }
}

