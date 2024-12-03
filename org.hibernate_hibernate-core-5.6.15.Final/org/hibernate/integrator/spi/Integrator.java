/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.integrator.spi;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public interface Integrator {
    public void integrate(Metadata var1, SessionFactoryImplementor var2, SessionFactoryServiceRegistry var3);

    public void disintegrate(SessionFactoryImplementor var1, SessionFactoryServiceRegistry var2);
}

