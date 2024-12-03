/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.hibernate.boot.Metadata
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.integrator.spi.Integrator
 *  org.hibernate.service.spi.SessionFactoryServiceRegistry
 */
package com.atlassian.confluence.impl.core.persistence.hibernate;

import com.atlassian.confluence.impl.core.persistence.hibernate.HibernateMetadataSource;
import com.google.common.base.Preconditions;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public final class HibernateMetadataIntegrator
implements Integrator,
HibernateMetadataSource {
    private Metadata metadata;

    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        this.metadata = metadata;
    }

    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        this.metadata = null;
    }

    @Override
    public Metadata getMetadata() {
        Preconditions.checkState((this.metadata != null ? 1 : 0) != 0, (Object)"Hibernate Metadata has not yet been initialized");
        return this.metadata;
    }
}

