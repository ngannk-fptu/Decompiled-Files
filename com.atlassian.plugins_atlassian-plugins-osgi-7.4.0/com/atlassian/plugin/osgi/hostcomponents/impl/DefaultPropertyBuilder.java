/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.osgi.hostcomponents.impl;

import com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy;
import com.atlassian.plugin.osgi.hostcomponents.PropertyBuilder;
import com.atlassian.plugin.osgi.hostcomponents.impl.Registration;

class DefaultPropertyBuilder
implements PropertyBuilder {
    private Registration registration;

    public DefaultPropertyBuilder(Registration registration) {
        this.registration = registration;
    }

    @Override
    public PropertyBuilder withName(String name) {
        return this.withProperty("bean-name", name);
    }

    @Override
    public PropertyBuilder withContextClassLoaderStrategy(ContextClassLoaderStrategy strategy) {
        return this.withProperty("context-class-loader-strategy", strategy.name());
    }

    @Override
    public PropertyBuilder withTrackBundleEnabled(boolean enabled) {
        return this.withProperty("track-bundle", Boolean.toString(enabled));
    }

    @Override
    public PropertyBuilder withProperty(String name, String value) {
        this.registration.getProperties().put(name, value);
        return this;
    }
}

