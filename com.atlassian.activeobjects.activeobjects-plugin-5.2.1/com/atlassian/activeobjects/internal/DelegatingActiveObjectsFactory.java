/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.config.ActiveObjectsConfiguration;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.internal.ActiveObjectsFactory;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;

public final class DelegatingActiveObjectsFactory
implements ActiveObjectsFactory {
    private final ImmutableSet<ActiveObjectsFactory> factories;

    public DelegatingActiveObjectsFactory(Collection<ActiveObjectsFactory> factories) {
        this.factories = ImmutableSet.builder().addAll(factories).build();
    }

    @Override
    public boolean accept(ActiveObjectsConfiguration configuration) {
        for (ActiveObjectsFactory factory : this.factories) {
            if (!factory.accept(configuration)) continue;
            return true;
        }
        return false;
    }

    @Override
    public ActiveObjects create(ActiveObjectsConfiguration configuration) {
        for (ActiveObjectsFactory factory : this.factories) {
            if (!factory.accept(configuration)) continue;
            return factory.create(configuration);
        }
        throw new IllegalStateException("Could not find a factory for this configuration, " + configuration + ", did you call #accept(ActiveObjectsConfiguration) before calling me?");
    }
}

