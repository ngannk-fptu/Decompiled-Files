/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client;

import com.sun.jersey.api.client.config.ClientConfig;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

class ComponentsClientConfig
implements ClientConfig {
    private final ClientConfig cc;
    private final Set<Class<?>> providers = new LinkedHashSet();

    public ComponentsClientConfig(ClientConfig cc, Class<?> ... components) {
        this(cc, new HashSet(Arrays.asList(components)));
    }

    public ComponentsClientConfig(ClientConfig cc, Set<Class<?>> components) {
        this.cc = cc;
        this.providers.addAll(cc.getClasses());
        this.providers.addAll(components);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return this.providers;
    }

    @Override
    public Set<Object> getSingletons() {
        return this.cc.getSingletons();
    }

    @Override
    public Map<String, Boolean> getFeatures() {
        return this.cc.getFeatures();
    }

    @Override
    public boolean getFeature(String featureName) {
        return this.cc.getFeature(featureName);
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.cc.getProperties();
    }

    @Override
    public Object getProperty(String propertyName) {
        return this.cc.getProperty(propertyName);
    }

    @Override
    public boolean getPropertyAsFeature(String name) {
        return this.cc.getPropertyAsFeature(name);
    }
}

