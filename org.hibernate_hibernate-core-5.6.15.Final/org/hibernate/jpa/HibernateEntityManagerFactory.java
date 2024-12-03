/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityGraph
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.metamodel.EntityType
 */
package org.hibernate.jpa;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import org.hibernate.Metamodel;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.NullnessHelper;

@Deprecated
public interface HibernateEntityManagerFactory
extends EntityManagerFactory,
Serializable {
    @Deprecated
    default public SessionFactoryImplementor getSessionFactory() {
        return (SessionFactoryImplementor)this;
    }

    public <T> List<EntityGraph<? super T>> findEntityGraphsByType(Class<T> var1);

    public Metamodel getMetamodel();

    @Deprecated
    default public String getEntityManagerFactoryName() {
        return (String)NullnessHelper.coalesceSuppliedValues(() -> (String)this.getProperties().get("hibernate.entitymanager_factory_name"), () -> {
            String oldSetting = (String)this.getProperties().get("hibernate.ejb.entitymanager_factory_name");
            if (oldSetting != null) {
                DeprecationLogger.DEPRECATION_LOGGER.deprecatedSetting("hibernate.ejb.entitymanager_factory_name", "hibernate.entitymanager_factory_name");
            }
            return oldSetting;
        });
    }

    @Deprecated
    default public EntityType getEntityTypeByName(String entityName) {
        EntityType entityType = this.getMetamodel().getEntityTypeByName(entityName);
        if (entityType == null) {
            throw new IllegalArgumentException("[" + entityName + "] did not refer to EntityType");
        }
        return entityType;
    }
}

