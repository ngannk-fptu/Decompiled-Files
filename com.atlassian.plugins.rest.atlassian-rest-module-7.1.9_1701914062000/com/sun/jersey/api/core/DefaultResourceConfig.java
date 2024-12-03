/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.core;

import com.sun.jersey.api.core.ResourceConfig;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MediaType;

public class DefaultResourceConfig
extends ResourceConfig {
    private final Set<Class<?>> classes = new LinkedHashSet();
    private final Set<Object> singletons = new LinkedHashSet<Object>(1);
    private final Map<String, MediaType> mediaExtentions = new HashMap<String, MediaType>(1);
    private final Map<String, String> languageExtentions = new HashMap<String, String>(1);
    private final Map<String, Object> explicitRootResources = new HashMap<String, Object>(1);
    private final Map<String, Boolean> features = new HashMap<String, Boolean>();
    private final Map<String, Object> properties = new HashMap<String, Object>();

    public DefaultResourceConfig() {
        this((Set)null);
    }

    public DefaultResourceConfig(Class<?> ... classes) {
        this(new LinkedHashSet(Arrays.asList(classes)));
    }

    public DefaultResourceConfig(Set<Class<?>> classes) {
        if (null != classes) {
            this.classes.addAll(classes);
        }
    }

    @Override
    public Set<Class<?>> getClasses() {
        return this.classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return this.singletons;
    }

    @Override
    public Map<String, MediaType> getMediaTypeMappings() {
        return this.mediaExtentions;
    }

    @Override
    public Map<String, String> getLanguageMappings() {
        return this.languageExtentions;
    }

    @Override
    public Map<String, Object> getExplicitRootResources() {
        return this.explicitRootResources;
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
}

