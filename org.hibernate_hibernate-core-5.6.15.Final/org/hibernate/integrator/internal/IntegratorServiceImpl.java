/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.integrator.internal;

import java.util.LinkedHashSet;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.cache.internal.CollectionCacheInvalidator;
import org.hibernate.cfg.beanvalidation.BeanValidationIntegrator;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.integrator.spi.IntegratorService;
import org.hibernate.secure.spi.JaccIntegrator;
import org.jboss.logging.Logger;

public class IntegratorServiceImpl
implements IntegratorService {
    private static final Logger LOG = Logger.getLogger((String)IntegratorServiceImpl.class.getName());
    private final LinkedHashSet<Integrator> integrators = new LinkedHashSet();

    public IntegratorServiceImpl(LinkedHashSet<Integrator> providedIntegrators, ClassLoaderService classLoaderService) {
        this.addIntegrator(new BeanValidationIntegrator());
        this.addIntegrator(new JaccIntegrator());
        this.addIntegrator(new CollectionCacheInvalidator());
        for (Integrator integrator : providedIntegrators) {
            this.addIntegrator(integrator);
        }
        for (Integrator integrator : classLoaderService.loadJavaServices(Integrator.class)) {
            this.addIntegrator(integrator);
        }
    }

    private void addIntegrator(Integrator integrator) {
        LOG.debugf("Adding Integrator [%s].", (Object)integrator.getClass().getName());
        this.integrators.add(integrator);
    }

    @Override
    public Iterable<Integrator> getIntegrators() {
        return this.integrators;
    }
}

