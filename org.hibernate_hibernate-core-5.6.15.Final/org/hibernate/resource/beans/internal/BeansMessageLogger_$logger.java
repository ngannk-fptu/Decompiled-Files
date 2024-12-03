/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 */
package org.hibernate.resource.beans.internal;

import java.io.Serializable;
import java.util.Locale;
import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.internal.BeansMessageLogger;
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;
import org.jboss.logging.Logger;

public class BeansMessageLogger_$logger
implements BeansMessageLogger,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FQCN = BeansMessageLogger_$logger.class.getName();
    protected final Logger log;
    private static final Locale LOCALE = Locale.ROOT;

    public BeansMessageLogger_$logger(Logger log) {
        this.log = log;
    }

    protected Locale getLoggingLocale() {
        return LOCALE;
    }

    @Override
    public final void beanManagerButCdiNotAvailable(Object cdiBeanManagerReference) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.beanManagerButCdiNotAvailable$str(), cdiBeanManagerReference);
    }

    protected String beanManagerButCdiNotAvailable$str() {
        return "HHH10005001: An explicit CDI BeanManager reference [%s] was passed to Hibernate, but CDI is not available on the Hibernate ClassLoader.  This is likely going to lead to exceptions later on in bootstrap";
    }

    @Override
    public final void noBeanManagerButCdiAvailable() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.noBeanManagerButCdiAvailable$str(), new Object[0]);
    }

    protected String noBeanManagerButCdiAvailable$str() {
        return "HHH10005002: No explicit CDI BeanManager reference was passed to Hibernate, but CDI is available on the Hibernate ClassLoader.";
    }

    @Override
    public final void stoppingManagedBeanRegistry(ManagedBeanRegistry registry) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.stoppingManagedBeanRegistry$str(), (Object)registry);
    }

    protected String stoppingManagedBeanRegistry$str() {
        return "HHH10005003: Stopping ManagedBeanRegistry : %s";
    }

    @Override
    public final void stoppingBeanContainer(BeanContainer beanContainer) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.stoppingBeanContainer$str(), (Object)beanContainer);
    }

    protected String stoppingBeanContainer$str() {
        return "HHH10005004: Stopping BeanContainer : %s";
    }
}

