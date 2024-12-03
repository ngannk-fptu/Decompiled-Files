/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PersistenceUnitUtil
 *  javax.persistence.spi.LoadState
 *  org.jboss.logging.Logger
 */
package org.hibernate.jpa.internal;

import java.io.Serializable;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.spi.LoadState;
import org.hibernate.Hibernate;
import org.hibernate.MappingException;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.internal.util.PersistenceUtilHelper;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.logging.Logger;

public class PersistenceUnitUtilImpl
implements PersistenceUnitUtil,
Serializable {
    private static final Logger log = Logger.getLogger(PersistenceUnitUtilImpl.class);
    private final SessionFactoryImplementor sessionFactory;
    private final transient PersistenceUtilHelper.MetadataCache cache = new PersistenceUtilHelper.MetadataCache();

    public PersistenceUnitUtilImpl(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public boolean isLoaded(Object entity, String attributeName) {
        log.debug((Object)"PersistenceUnitUtil#isLoaded is not always accurate; consider using EntityManager#contains instead");
        LoadState state = PersistenceUtilHelper.isLoadedWithoutReference(entity, attributeName, this.cache);
        if (state == LoadState.LOADED) {
            return true;
        }
        if (state == LoadState.NOT_LOADED) {
            return false;
        }
        return PersistenceUtilHelper.isLoadedWithReference(entity, attributeName, this.cache) != LoadState.NOT_LOADED;
    }

    public boolean isLoaded(Object entity) {
        log.debug((Object)"PersistenceUnitUtil#isLoaded is not always accurate; consider using EntityManager#contains instead");
        return PersistenceUtilHelper.isLoaded(entity) != LoadState.NOT_LOADED;
    }

    public Object getIdentifier(Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Passed entity cannot be null");
        }
        if (entity instanceof HibernateProxy) {
            return ((HibernateProxy)entity).getHibernateLazyInitializer().getInternalIdentifier();
        }
        if (ManagedTypeHelper.isManagedEntity(entity)) {
            EntityEntry entityEntry = ManagedTypeHelper.asManagedEntity(entity).$$_hibernate_getEntityEntry();
            if (entityEntry != null) {
                return entityEntry.getId();
            }
            log.debug((Object)"javax.persistence.PersistenceUnitUtil.getIdentifier may not be able to read identifier of a detached entity");
            return this.getIdentifierFromPersister(entity);
        }
        log.debugf("javax.persistence.PersistenceUnitUtil.getIdentifier is only intended to work with enhanced entities (although Hibernate also adapts this support to its proxies); however the passed entity was not enhanced (nor a proxy).. may not be able to read identifier", new Object[0]);
        return this.getIdentifierFromPersister(entity);
    }

    private Object getIdentifierFromPersister(Object entity) {
        EntityPersister persister;
        Class entityClass = Hibernate.getClass(entity);
        try {
            persister = this.sessionFactory.getMetamodel().entityPersister(entityClass);
            if (persister == null) {
                throw new IllegalArgumentException(entityClass.getName() + " is not an entity");
            }
        }
        catch (MappingException ex) {
            throw new IllegalArgumentException(entityClass.getName() + " is not an entity", (Throwable)((Object)ex));
        }
        return persister.getIdentifier(entity, null);
    }
}

