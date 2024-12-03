/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.config.internal;

import java.util.Collections;
import java.util.Map;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.jboss.logging.Logger;

public class ConfigurationServiceImpl
implements ConfigurationService,
ServiceRegistryAwareService {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)ConfigurationServiceImpl.class.getName());
    private final Map settings;
    private ServiceRegistryImplementor serviceRegistry;

    public ConfigurationServiceImpl(Map settings) {
        this.settings = Collections.unmodifiableMap(settings);
    }

    @Override
    public Map getSettings() {
        return this.settings;
    }

    @Override
    public void injectServices(ServiceRegistryImplementor serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public <T> T getSetting(String name, ConfigurationService.Converter<T> converter) {
        return this.getSetting(name, converter, null);
    }

    @Override
    public <T> T getSetting(String name, ConfigurationService.Converter<T> converter, T defaultValue) {
        Object value = this.settings.get(name);
        if (value == null) {
            return defaultValue;
        }
        return converter.convert(value);
    }

    @Override
    public <T> T getSetting(String name, Class<T> expected, T defaultValue) {
        Object value = this.settings.get(name);
        T target = this.cast(expected, value);
        return target != null ? target : defaultValue;
    }

    @Override
    public <T> T cast(Class<T> expected, Object candidate) {
        Class target;
        if (candidate == null) {
            return null;
        }
        if (expected.isInstance(candidate)) {
            return (T)candidate;
        }
        if (Class.class.isInstance(candidate)) {
            target = (Class)Class.class.cast(candidate);
        } else {
            try {
                target = this.serviceRegistry.getService(ClassLoaderService.class).classForName(candidate.toString());
            }
            catch (ClassLoadingException e) {
                LOG.debugf("Unable to locate %s implementation class %s", expected.getName(), candidate.toString());
                target = null;
            }
        }
        if (target != null) {
            try {
                return target.newInstance();
            }
            catch (Exception e) {
                LOG.debugf("Unable to instantiate %s class %s", expected.getName(), target.getName());
            }
        }
        return null;
    }
}

