/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.EntityManagerFactory
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.orm.jpa.support;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.util.Assert;

public class SharedEntityManagerBean
extends EntityManagerFactoryAccessor
implements FactoryBean<EntityManager>,
InitializingBean {
    @Nullable
    private Class<? extends EntityManager> entityManagerInterface;
    private boolean synchronizedWithTransaction = true;
    @Nullable
    private EntityManager shared;

    public void setEntityManagerInterface(Class<? extends EntityManager> entityManagerInterface) {
        Assert.notNull(entityManagerInterface, (String)"'entityManagerInterface' must not be null");
        this.entityManagerInterface = entityManagerInterface;
    }

    public void setSynchronizedWithTransaction(boolean synchronizedWithTransaction) {
        this.synchronizedWithTransaction = synchronizedWithTransaction;
    }

    public final void afterPropertiesSet() {
        EntityManagerFactory emf = this.getEntityManagerFactory();
        if (emf == null) {
            throw new IllegalArgumentException("'entityManagerFactory' or 'persistenceUnitName' is required");
        }
        if (emf instanceof EntityManagerFactoryInfo) {
            EntityManagerFactoryInfo emfInfo = (EntityManagerFactoryInfo)emf;
            if (this.entityManagerInterface == null) {
                this.entityManagerInterface = emfInfo.getEntityManagerInterface();
                if (this.entityManagerInterface == null) {
                    this.entityManagerInterface = EntityManager.class;
                }
            }
        } else if (this.entityManagerInterface == null) {
            this.entityManagerInterface = EntityManager.class;
        }
        this.shared = SharedEntityManagerCreator.createSharedEntityManager(emf, this.getJpaPropertyMap(), this.synchronizedWithTransaction, this.entityManagerInterface);
    }

    @Nullable
    public EntityManager getObject() {
        return this.shared;
    }

    public Class<? extends EntityManager> getObjectType() {
        return this.entityManagerInterface != null ? this.entityManagerInterface : EntityManager.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

