/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent
 *  org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyEvent
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.Filter
 *  org.springframework.context.ApplicationContext
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.extender.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyEvent;
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

public class BootstrappingDependenciesEvent
extends OsgiBundleApplicationContextEvent {
    private final Collection<OsgiServiceDependencyEvent> dependencyEvents;
    private final Collection<String> dependencyFilters;
    private final Filter dependenciesFilter;
    private final long timeLeft;

    public BootstrappingDependenciesEvent(ApplicationContext source, Bundle bundle, Collection<OsgiServiceDependencyEvent> nestedEvents, Filter filter, long timeLeft) {
        super(source, bundle);
        Assert.notNull(nestedEvents);
        this.dependencyEvents = nestedEvents;
        this.dependenciesFilter = filter;
        this.timeLeft = timeLeft;
        ArrayList<String> depFilters = new ArrayList<String>(this.dependencyEvents.size());
        for (OsgiServiceDependencyEvent dependency : nestedEvents) {
            depFilters.add(dependency.getServiceDependency().getServiceFilter().toString());
        }
        this.dependencyFilters = Collections.unmodifiableCollection(depFilters);
    }

    public Collection<OsgiServiceDependencyEvent> getDependencyEvents() {
        return this.dependencyEvents;
    }

    public Filter getDependenciesAsFilter() {
        return this.dependenciesFilter;
    }

    public Collection<String> getDependencyFilters() {
        return this.dependencyFilters;
    }

    public long getTimeToWait() {
        return this.timeLeft;
    }
}

