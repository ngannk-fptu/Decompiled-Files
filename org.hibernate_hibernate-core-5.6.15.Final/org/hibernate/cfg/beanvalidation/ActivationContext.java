/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg.beanvalidation;

import java.util.Set;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.beanvalidation.ValidationMode;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public interface ActivationContext {
    public Set<ValidationMode> getValidationModes();

    public Metadata getMetadata();

    public SessionFactoryImplementor getSessionFactory();

    public SessionFactoryServiceRegistry getServiceRegistry();
}

