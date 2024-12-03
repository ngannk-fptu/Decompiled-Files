/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.internal;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.internal.NoCachingRegionFactory;
import org.hibernate.cache.internal.StrategyCreatorRegionFactoryImpl;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class RegionFactoryInitiator
implements StandardServiceInitiator<RegionFactory> {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(RegionFactoryInitiator.class);
    public static final RegionFactoryInitiator INSTANCE = new RegionFactoryInitiator();

    @Override
    public Class<RegionFactory> getServiceInitiated() {
        return RegionFactory.class;
    }

    @Override
    public RegionFactory initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        RegionFactory regionFactory = this.resolveRegionFactory(configurationValues, registry);
        LOG.debugf("Cache region factory : %s", regionFactory.getClass().getName());
        return regionFactory;
    }

    protected RegionFactory resolveRegionFactory(Map configurationValues, ServiceRegistryImplementor registry) {
        Properties p = new Properties();
        p.putAll((Map<?, ?>)configurationValues);
        Boolean useSecondLevelCache = ConfigurationHelper.getBooleanWrapper("hibernate.cache.use_second_level_cache", configurationValues, null);
        Boolean useQueryCache = ConfigurationHelper.getBooleanWrapper("hibernate.cache.use_query_cache", configurationValues, null);
        if (useSecondLevelCache != null && useSecondLevelCache == Boolean.FALSE && (useQueryCache == null || useQueryCache == Boolean.FALSE)) {
            return NoCachingRegionFactory.INSTANCE;
        }
        Object setting = configurationValues.get("hibernate.cache.region.factory_class");
        StrategySelector selector = registry.getService(StrategySelector.class);
        Collection<Class<RegionFactory>> implementors = selector.getRegisteredStrategyImplementors(RegionFactory.class);
        if (setting == null && implementors.size() != 1 && (useSecondLevelCache != null && useSecondLevelCache == Boolean.TRUE || useQueryCache != null && useQueryCache == Boolean.TRUE)) {
            throw new CacheException("Caching was explicitly requested, but no RegionFactory was defined and there is not a single registered RegionFactory");
        }
        RegionFactory regionFactory = registry.getService(StrategySelector.class).resolveStrategy(RegionFactory.class, setting, (RegionFactory)null, new StrategyCreatorRegionFactoryImpl(p));
        if (regionFactory != null) {
            return regionFactory;
        }
        RegionFactory fallback = this.getFallback(configurationValues, registry);
        if (fallback != null) {
            return fallback;
        }
        if (implementors.size() == 1) {
            RegionFactory registeredFactory = selector.resolveStrategy(RegionFactory.class, implementors.iterator().next());
            configurationValues.put("hibernate.cache.region.factory_class", registeredFactory);
            configurationValues.put("hibernate.cache.use_second_level_cache", "true");
            return registeredFactory;
        }
        LOG.debugf("Cannot default RegionFactory based on registered strategies as `%s` RegionFactory strategies were registered", implementors);
        return NoCachingRegionFactory.INSTANCE;
    }

    protected RegionFactory getFallback(Map configurationValues, ServiceRegistryImplementor registry) {
        return null;
    }
}

