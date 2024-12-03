/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.integrator.spi;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.integrator.spi.Integrator;

@Deprecated
public interface ServiceContributingIntegrator
extends Integrator {
    public void prepareServices(StandardServiceRegistryBuilder var1);
}

