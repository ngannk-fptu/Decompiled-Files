/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client.config;

import com.sun.jersey.api.client.config.ClientConfig;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DefaultClientConfig
implements ClientConfig {
    private final Set<Class<?>> providers = new LinkedHashSet();
    private final Set<Object> providerInstances = new LinkedHashSet<Object>();
    private final Map<String, Boolean> features = new HashMap<String, Boolean>();
    private final Map<String, Object> properties = new HashMap<String, Object>();

    public DefaultClientConfig() {
    }

    public DefaultClientConfig(Class<?> ... providers) {
        Collections.addAll(this.providers, providers);
    }

    public DefaultClientConfig(Set<Class<?>> providers) {
        this.providers.addAll(providers);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return this.providers;
    }

    @Override
    public Set<Object> getSingletons() {
        return this.providerInstances;
    }

    @Override
    public Map<String, Boolean> getFeatures() {
        return this.features;
    }

    @Override
    public boolean getFeature(String featureName) {
        Boolean v = this.features.get(featureName);
        return v != null ? v : false;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    @Override
    public Object getProperty(String propertyName) {
        return this.properties.get(propertyName);
    }

    @Override
    public boolean getPropertyAsFeature(String name) {
        Boolean v = (Boolean)this.getProperties().get(name);
        return v != null ? v : false;
    }
}

