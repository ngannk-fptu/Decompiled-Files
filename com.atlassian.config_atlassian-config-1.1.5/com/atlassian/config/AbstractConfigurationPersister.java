/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 */
package com.atlassian.config;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.config.ConfigElement;
import com.atlassian.config.ConfigurationException;
import com.atlassian.config.ConfigurationPersister;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractConfigurationPersister
implements ConfigurationPersister {
    @TenantAware(value=TenancyScope.TENANTLESS, comment="A map of <Class, Class> can't contain tenant information")
    private Map<Class<?>, Class<?>> configMappings = new HashMap();

    static ConfigElement instantiateClass(Class clazz, Object ... constructorArgs) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException {
        if (clazz.getConstructors().length == 1) {
            return (ConfigElement)clazz.getConstructors()[0].newInstance(constructorArgs);
        }
        throw new IllegalArgumentException("Unable to construct " + clazz.getName() + " as a config entry. It must have exactly one constructor.");
    }

    @Override
    public void addConfigMapping(Class<?> propertyType, Class<?> configType) {
        this.configMappings.put(propertyType, configType);
    }

    public abstract Object getRootContext();

    @Override
    public void addConfigElement(Object item, String propertyName) throws ConfigurationException {
        this.addConfigElement(item, propertyName, this.getRootContext());
    }

    @Override
    public Object getConfigElement(Class propertyType, String propertyName) throws ConfigurationException {
        return this.getConfigElement(propertyType, propertyName, this.getRootContext());
    }

    @Override
    public String getStringConfigElement(String propertyName) throws ConfigurationException {
        return (String)this.getConfigElement(String.class, propertyName);
    }

    public void addConfigElement(Object item, String propertyName, Object context) throws ConfigurationException {
        Class<?> clazz = null;
        if (item == null) {
            return;
        }
        for (Map.Entry<Class<?>, Class<?>> entry : this.configMappings.entrySet()) {
            Class<?> c = entry.getKey();
            if (!c.isAssignableFrom(item.getClass())) continue;
            clazz = entry.getValue();
            break;
        }
        if (clazz != null) {
            try {
                ConfigElement config = AbstractConfigurationPersister.instantiateClass(clazz, propertyName, context, this);
                config.save(item);
            }
            catch (Exception e) {
                throw new ConfigurationException("Failed to create config element: " + clazz.getName(), e);
            }
        } else {
            throw new ConfigurationException("Failed to find config element for " + item.getClass().getName());
        }
    }

    public Object getConfigElement(Class propertyType, String propertyName, Object context) throws ConfigurationException {
        Class<?> clazz = this.configMappings.get(propertyType);
        if (clazz != null) {
            try {
                ConfigElement config = AbstractConfigurationPersister.instantiateClass(clazz, propertyName, context, this);
                return config.load();
            }
            catch (Exception e) {
                throw new ConfigurationException("Failed to create config element: " + clazz.getName(), e);
            }
        }
        throw new ConfigurationException("Failed to find config element for " + propertyType.getName());
    }
}

