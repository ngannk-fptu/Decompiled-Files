/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.boot.spi;

import java.util.List;
import org.hibernate.integrator.spi.Integrator;

public interface IntegratorProvider {
    public List<Integrator> getIntegrators();
}

