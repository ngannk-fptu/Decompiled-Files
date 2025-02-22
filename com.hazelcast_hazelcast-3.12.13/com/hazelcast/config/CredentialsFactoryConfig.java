/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.security.ICredentialsFactory;
import java.util.Map;
import java.util.Properties;

public class CredentialsFactoryConfig {
    private String className;
    private ICredentialsFactory implementation;
    private Properties properties = new Properties();

    public CredentialsFactoryConfig() {
    }

    public CredentialsFactoryConfig(String className) {
        this.className = className;
    }

    public CredentialsFactoryConfig(CredentialsFactoryConfig credentialsFactoryConfig) {
        this.className = credentialsFactoryConfig.className;
        this.implementation = credentialsFactoryConfig.implementation;
        this.properties = new Properties();
        this.properties.putAll((Map<?, ?>)credentialsFactoryConfig.properties);
    }

    public String getClassName() {
        return this.className;
    }

    public CredentialsFactoryConfig setClassName(String className) {
        this.className = className;
        return this;
    }

    public ICredentialsFactory getImplementation() {
        return this.implementation;
    }

    public CredentialsFactoryConfig setImplementation(ICredentialsFactory factoryImpl) {
        this.implementation = factoryImpl;
        return this;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public CredentialsFactoryConfig setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public String toString() {
        return "CredentialsFactoryConfig{className='" + this.className + '\'' + ", implementation=" + this.implementation + ", properties=" + this.properties + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CredentialsFactoryConfig that = (CredentialsFactoryConfig)o;
        if (this.className != null ? !this.className.equals(that.className) : that.className != null) {
            return false;
        }
        if (this.implementation != null ? !this.implementation.equals(that.implementation) : that.implementation != null) {
            return false;
        }
        return this.properties != null ? this.properties.equals(that.properties) : that.properties == null;
    }

    public int hashCode() {
        int result = this.className != null ? this.className.hashCode() : 0;
        result = 31 * result + (this.implementation != null ? this.implementation.hashCode() : 0);
        result = 31 * result + (this.properties != null ? this.properties.hashCode() : 0);
        return result;
    }
}

