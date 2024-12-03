/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.servicemanager;

import com.hazelcast.spi.ConfigurableService;
import com.hazelcast.spi.CoreService;
import com.hazelcast.spi.ManagedService;

public final class ServiceInfo {
    private final String name;
    private final Object service;

    public ServiceInfo(String name, Object service) {
        this.name = name;
        this.service = service;
    }

    public String getName() {
        return this.name;
    }

    public <T> T getService() {
        return (T)this.service;
    }

    public boolean isCoreService() {
        return this.service instanceof CoreService;
    }

    public boolean isManagedService() {
        return this.service instanceof ManagedService;
    }

    public boolean isConfigurableService() {
        return this.service instanceof ConfigurableService;
    }

    public boolean isInstanceOf(Class type) {
        return type.isAssignableFrom(this.service.getClass());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ServiceInfo that = (ServiceInfo)o;
        return !(this.name != null ? !this.name.equals(that.name) : that.name != null);
    }

    public int hashCode() {
        return this.name != null ? this.name.hashCode() : 0;
    }

    public String toString() {
        return "ServiceInfo{name='" + this.name + "', service=" + this.service + '}';
    }
}

