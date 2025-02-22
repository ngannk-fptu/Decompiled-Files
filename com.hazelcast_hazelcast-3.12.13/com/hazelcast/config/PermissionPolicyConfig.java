/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.security.IPermissionPolicy;
import java.util.Properties;

public class PermissionPolicyConfig {
    private String className;
    private IPermissionPolicy implementation;
    private Properties properties = new Properties();

    public PermissionPolicyConfig() {
    }

    public PermissionPolicyConfig(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

    public PermissionPolicyConfig setClassName(String className) {
        this.className = className;
        return this;
    }

    public IPermissionPolicy getImplementation() {
        return this.implementation;
    }

    public PermissionPolicyConfig setImplementation(IPermissionPolicy policyImpl) {
        this.implementation = policyImpl;
        return this;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public PermissionPolicyConfig setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public String toString() {
        return "PermissionPolicyConfig{className='" + this.className + '\'' + ", implementation=" + this.implementation + ", properties=" + this.properties + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PermissionPolicyConfig that = (PermissionPolicyConfig)o;
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

