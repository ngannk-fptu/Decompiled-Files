/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import java.util.Map;
import java.util.Properties;

public final class SSLConfig {
    private boolean enabled;
    private String factoryClassName;
    private Object factoryImplementation;
    private Properties properties = new Properties();

    public SSLConfig() {
    }

    public SSLConfig(SSLConfig sslConfig) {
        this.enabled = sslConfig.enabled;
        this.factoryClassName = sslConfig.factoryClassName;
        this.factoryImplementation = sslConfig.factoryImplementation;
        this.properties = new Properties();
        this.properties.putAll((Map<?, ?>)sslConfig.properties);
    }

    public String getFactoryClassName() {
        return this.factoryClassName;
    }

    public SSLConfig setFactoryClassName(String factoryClassName) {
        this.factoryClassName = factoryClassName;
        return this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public SSLConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public SSLConfig setFactoryImplementation(Object factoryImplementation) {
        this.factoryImplementation = factoryImplementation;
        return this;
    }

    public Object getFactoryImplementation() {
        return this.factoryImplementation;
    }

    public SSLConfig setProperty(String name, String value) {
        this.properties.put(name, value);
        return this;
    }

    public String getProperty(String name) {
        return this.properties.getProperty(name);
    }

    public Properties getProperties() {
        return this.properties;
    }

    public SSLConfig setProperties(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("properties can't be null");
        }
        this.properties = properties;
        return this;
    }

    public String toString() {
        return "SSLConfig{className='" + this.factoryClassName + '\'' + ", enabled=" + this.enabled + ", implementation=" + this.factoryImplementation + ", properties=" + this.properties + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SSLConfig sslConfig = (SSLConfig)o;
        if (this.enabled != sslConfig.enabled) {
            return false;
        }
        if (this.factoryClassName != null ? !this.factoryClassName.equals(sslConfig.factoryClassName) : sslConfig.factoryClassName != null) {
            return false;
        }
        if (this.factoryImplementation != null ? !this.factoryImplementation.equals(sslConfig.factoryImplementation) : sslConfig.factoryImplementation != null) {
            return false;
        }
        return this.properties != null ? this.properties.equals(sslConfig.properties) : sslConfig.properties == null;
    }

    public int hashCode() {
        int result = this.enabled ? 1 : 0;
        result = 31 * result + (this.factoryClassName != null ? this.factoryClassName.hashCode() : 0);
        result = 31 * result + (this.factoryImplementation != null ? this.factoryImplementation.hashCode() : 0);
        result = 31 * result + (this.properties != null ? this.properties.hashCode() : 0);
        return result;
    }
}

