/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.integrator.spi;

import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.Service;

public interface IntegratorService
extends Service {
    public Iterable<Integrator> getIntegrators();
}

