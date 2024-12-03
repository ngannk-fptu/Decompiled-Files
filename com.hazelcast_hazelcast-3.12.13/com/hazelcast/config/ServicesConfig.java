/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ServiceConfig;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServicesConfig {
    private boolean enableDefaults = true;
    private final Map<String, ServiceConfig> services = new HashMap<String, ServiceConfig>();

    public boolean isEnableDefaults() {
        return this.enableDefaults;
    }

    public ServicesConfig setEnableDefaults(boolean enableDefaults) {
        this.enableDefaults = enableDefaults;
        return this;
    }

    public ServicesConfig clear() {
        this.services.clear();
        return this;
    }

    public Collection<ServiceConfig> getServiceConfigs() {
        return Collections.unmodifiableCollection(this.services.values());
    }

    public ServicesConfig setServiceConfigs(Collection<ServiceConfig> services) {
        this.clear();
        for (ServiceConfig service : services) {
            this.addServiceConfig(service);
        }
        return this;
    }

    public ServicesConfig addServiceConfig(ServiceConfig service) {
        this.services.put(service.getName(), service);
        return this;
    }

    public ServiceConfig getServiceConfig(String name) {
        return this.services.get(name);
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof ServicesConfig)) {
            return false;
        }
        ServicesConfig that = (ServicesConfig)o;
        if (this.enableDefaults != that.enableDefaults) {
            return false;
        }
        return this.services.equals(that.services);
    }

    public final int hashCode() {
        int result = this.enableDefaults ? 1 : 0;
        result = 31 * result + this.services.hashCode();
        return result;
    }

    public String toString() {
        return "ServicesConfig{enableDefaults=" + this.enableDefaults + ", services=" + this.services + '}';
    }
}

