/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.spi.MemberAddressProvider;
import com.hazelcast.util.Preconditions;
import java.util.Properties;

public final class MemberAddressProviderConfig {
    private boolean enabled;
    private String className;
    private Properties properties = new Properties();
    private MemberAddressProvider implementation;

    public boolean isEnabled() {
        return this.enabled;
    }

    public MemberAddressProviderConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getClassName() {
        return this.className;
    }

    public MemberAddressProviderConfig setClassName(String className) {
        this.className = className;
        return this;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public MemberAddressProviderConfig setProperties(Properties properties) {
        Preconditions.checkNotNull(properties, "MemberAddressProvider properties cannot be null");
        this.properties = properties;
        return this;
    }

    public MemberAddressProvider getImplementation() {
        return this.implementation;
    }

    public MemberAddressProviderConfig setImplementation(MemberAddressProvider implementation) {
        this.implementation = implementation;
        return this;
    }

    public String toString() {
        return "MemberAddressProviderConfig{enabled=" + this.enabled + ", className='" + this.className + '\'' + ", properties=" + this.properties + ", implementation=" + this.implementation + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MemberAddressProviderConfig that = (MemberAddressProviderConfig)o;
        if (this.isEnabled() != that.isEnabled()) {
            return false;
        }
        if (this.getClassName() != null ? !this.getClassName().equals(that.getClassName()) : that.getClassName() != null) {
            return false;
        }
        if (!this.getProperties().equals(that.getProperties())) {
            return false;
        }
        return this.getImplementation() != null ? this.getImplementation().equals(that.getImplementation()) : that.getImplementation() == null;
    }

    public int hashCode() {
        int result = this.isEnabled() ? 1 : 0;
        result = 31 * result + (this.getClassName() != null ? this.getClassName().hashCode() : 0);
        result = 31 * result + this.getProperties().hashCode();
        result = 31 * result + (this.getImplementation() != null ? this.getImplementation().hashCode() : 0);
        return result;
    }
}

