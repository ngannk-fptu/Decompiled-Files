/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.osgi.hostcomponents.impl;

import com.atlassian.plugin.osgi.hostcomponents.InstanceBuilder;
import com.atlassian.plugin.osgi.hostcomponents.PropertyBuilder;
import com.atlassian.plugin.osgi.hostcomponents.impl.DefaultPropertyBuilder;
import com.atlassian.plugin.osgi.hostcomponents.impl.Registration;

class DefaultInstanceBuilder
implements InstanceBuilder {
    private Registration registration;

    public DefaultInstanceBuilder(Registration registration) {
        this.registration = registration;
    }

    @Override
    public PropertyBuilder forInstance(Object instance) {
        this.registration.setInstance(instance);
        return new DefaultPropertyBuilder(this.registration);
    }
}

