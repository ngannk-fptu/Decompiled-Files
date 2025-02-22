/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import java.util.Properties;

public class ServiceConfig {
    private boolean enabled;
    private String name;
    private String className;
    private Object serviceImpl;
    private Properties properties = new Properties();
    private Object configObject;

    public boolean isEnabled() {
        return this.enabled;
    }

    public ServiceConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public ServiceConfig setName(String name) {
        this.name = name;
        return this;
    }

    public String getClassName() {
        return this.className;
    }

    public ServiceConfig setClassName(String className) {
        this.className = className;
        return this;
    }

    public Object getImplementation() {
        return this.serviceImpl;
    }

    public ServiceConfig setImplementation(Object serviceImpl) {
        this.serviceImpl = serviceImpl;
        return this;
    }

    @Deprecated
    public Object getServiceImpl() {
        return this.getImplementation();
    }

    @Deprecated
    public ServiceConfig setServiceImpl(Object serviceImpl) {
        return this.setImplementation(serviceImpl);
    }

    public Properties getProperties() {
        return this.properties;
    }

    public ServiceConfig setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public ServiceConfig addProperty(String propertyName, String value) {
        this.properties.setProperty(propertyName, value);
        return this;
    }

    public ServiceConfig setConfigObject(Object configObject) {
        this.configObject = configObject;
        return this;
    }

    public Object getConfigObject() {
        return this.configObject;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof ServiceConfig)) {
            return false;
        }
        ServiceConfig that = (ServiceConfig)o;
        if (this.enabled != that.enabled) {
            return false;
        }
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        if (this.className != null ? !this.className.equals(that.className) : that.className != null) {
            return false;
        }
        if (this.serviceImpl != null ? !this.serviceImpl.equals(that.serviceImpl) : that.serviceImpl != null) {
            return false;
        }
        if (this.properties != null ? !this.properties.equals(that.properties) : that.properties != null) {
            return false;
        }
        return this.configObject != null ? this.configObject.equals(that.configObject) : that.configObject == null;
    }

    public final int hashCode() {
        int result = this.enabled ? 1 : 0;
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        result = 31 * result + (this.className != null ? this.className.hashCode() : 0);
        result = 31 * result + (this.serviceImpl != null ? this.serviceImpl.hashCode() : 0);
        result = 31 * result + (this.properties != null ? this.properties.hashCode() : 0);
        result = 31 * result + (this.configObject != null ? this.configObject.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "ServiceConfig{enabled=" + this.enabled + ", name='" + this.name + '\'' + ", className='" + this.className + '\'' + ", implementation=" + this.serviceImpl + ", properties=" + this.properties + '}';
    }
}

