/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.hibernate.boot.Metadata
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.integrator.spi.Integrator
 *  org.hibernate.service.spi.SessionFactoryServiceRegistry
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.importexport.impl.BackupRestoreHibernateUtil;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

@Internal
final class BackupRestoreHibernateIntegrator
implements Integrator {
    BackupRestoreHibernateIntegrator() {
    }

    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        BackupRestoreHibernateUtil.prepareConfigurationForBackupOperation(metadata);
    }

    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
    }
}

