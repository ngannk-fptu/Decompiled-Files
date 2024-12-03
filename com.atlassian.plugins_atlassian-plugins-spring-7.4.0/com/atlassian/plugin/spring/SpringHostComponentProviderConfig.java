/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy
 */
package com.atlassian.plugin.spring;

import com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class SpringHostComponentProviderConfig {
    private Set<String> beanNames = Collections.emptySet();
    private Map<String, Class[]> beanInterfaces = Collections.emptyMap();
    private Map<String, ContextClassLoaderStrategy> beanContextClassLoaderStrategies = Collections.emptyMap();
    private Set<String> bundleTrackingBeans = Collections.emptySet();
    private boolean useAnnotation = false;

    public Set<String> getBeanNames() {
        return this.beanNames;
    }

    public void setBeanNames(Set<String> beanNames) {
        this.beanNames = beanNames;
    }

    public Map<String, Class[]> getBeanInterfaces() {
        return this.beanInterfaces;
    }

    public void setBeanInterfaces(Map<String, Class[]> beanInterfaces) {
        this.beanInterfaces = beanInterfaces;
    }

    public Map<String, ContextClassLoaderStrategy> getBeanContextClassLoaderStrategies() {
        return this.beanContextClassLoaderStrategies;
    }

    public void setBeanContextClassLoaderStrategies(Map<String, ContextClassLoaderStrategy> beanContextClassLoaderStrategies) {
        this.beanContextClassLoaderStrategies = beanContextClassLoaderStrategies;
    }

    public Set<String> getBundleTrackingBeans() {
        return this.bundleTrackingBeans;
    }

    public void setBundleTrackingBeans(Set<String> bundleTrackingBeans) {
        this.bundleTrackingBeans = bundleTrackingBeans;
    }

    public void setUseAnnotation(boolean useAnnotation) {
        this.useAnnotation = useAnnotation;
    }

    public boolean isUseAnnotation() {
        return this.useAnnotation;
    }
}

