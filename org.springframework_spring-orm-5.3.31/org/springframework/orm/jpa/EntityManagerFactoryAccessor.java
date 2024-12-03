/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.EntityManagerFactory
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.orm.jpa;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public abstract class EntityManagerFactoryAccessor
implements BeanFactoryAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private EntityManagerFactory entityManagerFactory;
    @Nullable
    private String persistenceUnitName;
    private final Map<String, Object> jpaPropertyMap = new HashMap<String, Object>();

    public void setEntityManagerFactory(@Nullable EntityManagerFactory emf) {
        this.entityManagerFactory = emf;
    }

    @Nullable
    public EntityManagerFactory getEntityManagerFactory() {
        return this.entityManagerFactory;
    }

    protected final EntityManagerFactory obtainEntityManagerFactory() {
        EntityManagerFactory emf = this.getEntityManagerFactory();
        Assert.state((emf != null ? 1 : 0) != 0, (String)"No EntityManagerFactory set");
        return emf;
    }

    public void setPersistenceUnitName(@Nullable String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    @Nullable
    public String getPersistenceUnitName() {
        return this.persistenceUnitName;
    }

    public void setJpaProperties(Properties jpaProperties) {
        CollectionUtils.mergePropertiesIntoMap((Properties)jpaProperties, this.jpaPropertyMap);
    }

    public void setJpaPropertyMap(@Nullable Map<String, Object> jpaProperties) {
        if (jpaProperties != null) {
            this.jpaPropertyMap.putAll(jpaProperties);
        }
    }

    public Map<String, Object> getJpaPropertyMap() {
        return this.jpaPropertyMap;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.getEntityManagerFactory() == null) {
            if (!(beanFactory instanceof ListableBeanFactory)) {
                throw new IllegalStateException("Cannot retrieve EntityManagerFactory by persistence unit name in a non-listable BeanFactory: " + beanFactory);
            }
            ListableBeanFactory lbf = (ListableBeanFactory)beanFactory;
            this.setEntityManagerFactory(EntityManagerFactoryUtils.findEntityManagerFactory(lbf, this.getPersistenceUnitName()));
        }
    }

    protected EntityManager createEntityManager() throws IllegalStateException {
        EntityManagerFactory emf = this.obtainEntityManagerFactory();
        Map<String, Object> properties = this.getJpaPropertyMap();
        return !CollectionUtils.isEmpty(properties) ? emf.createEntityManager(properties) : emf.createEntityManager();
    }

    @Nullable
    protected EntityManager getTransactionalEntityManager() throws IllegalStateException {
        EntityManagerFactory emf = this.obtainEntityManagerFactory();
        return EntityManagerFactoryUtils.getTransactionalEntityManager(emf, this.getJpaPropertyMap());
    }
}

