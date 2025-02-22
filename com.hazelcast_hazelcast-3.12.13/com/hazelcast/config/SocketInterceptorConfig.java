/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import java.util.Map;
import java.util.Properties;

public class SocketInterceptorConfig {
    private boolean enabled;
    private String className;
    private Object implementation;
    private Properties properties = new Properties();

    public SocketInterceptorConfig() {
    }

    public SocketInterceptorConfig(SocketInterceptorConfig socketInterceptorConfig) {
        this.enabled = socketInterceptorConfig.enabled;
        this.className = socketInterceptorConfig.className;
        this.implementation = socketInterceptorConfig.implementation;
        this.properties = new Properties();
        this.properties.putAll((Map<?, ?>)socketInterceptorConfig.properties);
    }

    public String getClassName() {
        return this.className;
    }

    public SocketInterceptorConfig setClassName(String className) {
        this.className = className;
        return this;
    }

    public SocketInterceptorConfig setImplementation(Object implementation) {
        this.implementation = implementation;
        return this;
    }

    public Object getImplementation() {
        return this.implementation;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public SocketInterceptorConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public SocketInterceptorConfig setProperty(String name, String value) {
        this.properties.put(name, value);
        return this;
    }

    public String getProperty(String name) {
        return this.properties.getProperty(name);
    }

    public Properties getProperties() {
        return this.properties;
    }

    public SocketInterceptorConfig setProperties(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("properties can't be null");
        }
        this.properties = properties;
        return this;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof SocketInterceptorConfig)) {
            return false;
        }
        SocketInterceptorConfig that = (SocketInterceptorConfig)o;
        if (this.enabled != that.enabled) {
            return false;
        }
        if (this.className != null ? !this.className.equals(that.className) : that.className != null) {
            return false;
        }
        if (this.implementation != null ? !this.implementation.equals(that.implementation) : that.implementation != null) {
            return false;
        }
        return this.properties != null ? this.properties.equals(that.properties) : that.properties == null;
    }

    public final int hashCode() {
        int result = this.enabled ? 1 : 0;
        result = 31 * result + (this.className != null ? this.className.hashCode() : 0);
        result = 31 * result + (this.implementation != null ? this.implementation.hashCode() : 0);
        result = 31 * result + (this.properties != null ? this.properties.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "SocketInterceptorConfig{className='" + this.className + '\'' + ", enabled=" + this.enabled + ", implementation=" + this.implementation + ", properties=" + this.properties + '}';
    }
}

