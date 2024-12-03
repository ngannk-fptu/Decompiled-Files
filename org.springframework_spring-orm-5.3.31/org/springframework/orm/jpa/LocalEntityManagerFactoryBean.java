/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.Persistence
 *  javax.persistence.PersistenceException
 *  javax.persistence.spi.PersistenceProvider
 */
package org.springframework.orm.jpa;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceProvider;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;

public class LocalEntityManagerFactoryBean
extends AbstractEntityManagerFactoryBean {
    @Override
    protected EntityManagerFactory createNativeEntityManagerFactory() throws PersistenceException {
        PersistenceProvider provider;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Building JPA EntityManagerFactory for persistence unit '" + this.getPersistenceUnitName() + "'"));
        }
        if ((provider = this.getPersistenceProvider()) != null) {
            EntityManagerFactory emf = provider.createEntityManagerFactory(this.getPersistenceUnitName(), this.getJpaPropertyMap());
            if (emf == null) {
                throw new IllegalStateException("PersistenceProvider [" + provider + "] did not return an EntityManagerFactory for name '" + this.getPersistenceUnitName() + "'");
            }
            return emf;
        }
        return Persistence.createEntityManagerFactory((String)this.getPersistenceUnitName(), this.getJpaPropertyMap());
    }
}

