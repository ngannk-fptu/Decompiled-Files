/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.stat.internal;

import org.hibernate.HibernateException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceInitiator;
import org.hibernate.service.spi.SessionFactoryServiceInitiatorContext;
import org.hibernate.stat.internal.StatisticsImpl;
import org.hibernate.stat.spi.StatisticsFactory;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.jboss.logging.Logger;

public class StatisticsInitiator
implements SessionFactoryServiceInitiator<StatisticsImplementor> {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)StatisticsInitiator.class.getName());
    public static final StatisticsInitiator INSTANCE = new StatisticsInitiator();
    public static final String STATS_BUILDER = "hibernate.stats.factory";

    @Override
    public Class<StatisticsImplementor> getServiceInitiated() {
        return StatisticsImplementor.class;
    }

    @Override
    public StatisticsImplementor initiateService(SessionFactoryServiceInitiatorContext context) {
        Object configValue = context.getServiceRegistry().getService(ConfigurationService.class).getSettings().get(STATS_BUILDER);
        return this.initiateServiceInternal(context.getSessionFactory(), configValue, context.getServiceRegistry());
    }

    @Override
    public StatisticsImplementor initiateService(SessionFactoryImplementor sessionFactory, SessionFactoryOptions sessionFactoryOptions, ServiceRegistryImplementor registry) {
        Object configValue = registry.getService(ConfigurationService.class).getSettings().get(STATS_BUILDER);
        return this.initiateServiceInternal(sessionFactory, configValue, registry);
    }

    private StatisticsImplementor initiateServiceInternal(SessionFactoryImplementor sessionFactory, Object configValue, ServiceRegistryImplementor registry) {
        StatisticsFactory statisticsFactory;
        if (configValue == null) {
            statisticsFactory = null;
        } else if (StatisticsFactory.class.isInstance(configValue)) {
            statisticsFactory = (StatisticsFactory)configValue;
        } else {
            ClassLoaderService classLoaderService = registry.getService(ClassLoaderService.class);
            try {
                statisticsFactory = (StatisticsFactory)classLoaderService.classForName(configValue.toString()).newInstance();
            }
            catch (HibernateException e) {
                throw e;
            }
            catch (Exception e) {
                throw new HibernateException("Unable to instantiate specified StatisticsFactory implementation [" + configValue.toString() + "]", e);
            }
        }
        StatisticsImplementor statistics = statisticsFactory == null ? new StatisticsImpl(sessionFactory) : statisticsFactory.buildStatistics(sessionFactory);
        boolean enabled = sessionFactory.getSettings().isStatisticsEnabled();
        statistics.setStatisticsEnabled(enabled);
        LOG.debugf("Statistics initialized [enabled=%s]", enabled);
        return statistics;
    }
}

