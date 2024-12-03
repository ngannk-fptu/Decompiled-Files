/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.cache.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;
import org.hibernate.boot.registry.selector.spi.StrategyCreator;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.service.spi.ServiceException;
import org.jboss.logging.Logger;

public class StrategyCreatorRegionFactoryImpl
implements StrategyCreator<RegionFactory> {
    private static final Logger log = Logger.getLogger(StrategyCreatorRegionFactoryImpl.class);
    private final Properties properties;

    public StrategyCreatorRegionFactoryImpl(Properties properties) {
        this.properties = properties;
    }

    @Override
    public RegionFactory create(Class<? extends RegionFactory> strategyClass) {
        assert (RegionFactory.class.isAssignableFrom(strategyClass));
        try {
            Constructor<? extends RegionFactory> ctor = strategyClass.getConstructor(Properties.class);
            return ctor.newInstance(this.properties);
        }
        catch (NoSuchMethodException e) {
            log.debugf("RegionFactory impl [%s] did not provide constructor accepting Properties", (Object)strategyClass.getName());
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new ServiceException("Unable to call constructor of RegionFactory impl [" + strategyClass.getName() + "]", e);
        }
        try {
            Constructor<? extends RegionFactory> ctor = strategyClass.getConstructor(Map.class);
            return ctor.newInstance(this.properties);
        }
        catch (NoSuchMethodException e) {
            log.debugf("RegionFactory impl [%s] did not provide constructor accepting Properties", (Object)strategyClass.getName());
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new ServiceException("Unable to call constructor of RegionFactory impl [" + strategyClass.getName() + "]", e);
        }
        try {
            return strategyClass.newInstance();
        }
        catch (IllegalAccessException | InstantiationException e) {
            throw new ServiceException("Unable to call constructor of RegionFactory impl [" + strategyClass.getName() + "]", e);
        }
    }
}

