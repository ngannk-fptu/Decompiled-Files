/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import java.util.Properties;

public final class MCMutualAuthConfig {
    private boolean enabled;
    private String factoryClassName;
    private Object factoryImplementation;
    private Properties properties = new Properties();

    public String getFactoryClassName() {
        return this.factoryClassName;
    }

    public MCMutualAuthConfig setFactoryClassName(String factoryClassName) {
        this.factoryClassName = factoryClassName;
        return this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public MCMutualAuthConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public MCMutualAuthConfig setFactoryImplementation(Object factoryImplementation) {
        this.factoryImplementation = factoryImplementation;
        return this;
    }

    public Object getFactoryImplementation() {
        return this.factoryImplementation;
    }

    public MCMutualAuthConfig setProperty(String name, String value) {
        this.properties.put(name, value);
        return this;
    }

    public String getProperty(String name) {
        return this.properties.getProperty(name);
    }

    public Properties getProperties() {
        return this.properties;
    }

    public MCMutualAuthConfig setProperties(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("properties can't be null");
        }
        this.properties = properties;
        return this;
    }

    public String toString() {
        return "MCMutualAuthConfig{className='" + this.factoryClassName + '\'' + ", enabled=" + this.enabled + ", implementation=" + this.factoryImplementation + ", properties=" + this.properties + '}';
    }
}

