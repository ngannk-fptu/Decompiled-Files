/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.config;

import com.atlassian.config.AbstractConfigurationPersister;
import com.atlassian.config.ConfigElement;
import com.atlassian.config.ConfigurationException;

public abstract class AbstractConfigElement<T, C>
implements ConfigElement<T, C> {
    private String propertyName;
    private AbstractConfigurationPersister config;

    public AbstractConfigElement(String name, C context, AbstractConfigurationPersister config) {
        this.setPropertyName(name);
        this.setContext(context);
        this.config = config;
    }

    @Override
    public final void save(T object) throws ConfigurationException {
        this.checkSaveObject(object);
        this.saveConfig(object);
    }

    @Override
    public final T load() throws ConfigurationException {
        return this.loadConfig();
    }

    protected void checkSaveObject(T object) throws ConfigurationException {
        if (object == null) {
            throw new ConfigurationException("Object to save cannot be null");
        }
        if (!this.getObjectClass().isAssignableFrom(object.getClass())) {
            throw new ConfigurationException("Object to save was not of expected type. Expected type was: " + this.getObjectClass() + ", actual type is: " + object.getClass().getName());
        }
    }

    public AbstractConfigurationPersister getConfiguration() {
        return this.config;
    }

    @Override
    public String getPropertyName() {
        return this.propertyName;
    }

    @Override
    public void setPropertyName(String name) {
        this.propertyName = name;
    }

    protected abstract T loadConfig() throws ConfigurationException;

    protected abstract void saveConfig(T var1) throws ConfigurationException;
}

