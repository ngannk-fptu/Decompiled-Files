/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent
 *  org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyEvent
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.Filter
 *  org.springframework.context.ApplicationContext
 */
package org.eclipse.gemini.blueprint.extender.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyEvent;
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.springframework.context.ApplicationContext;

public class BootstrappingDependenciesFailedEvent
extends OsgiBundleContextFailedEvent {
    private final Collection<OsgiServiceDependencyEvent> dependencyEvents;
    private final Collection<String> dependencyFilters;
    private final Filter dependenciesFilter;

    public BootstrappingDependenciesFailedEvent(ApplicationContext source, Bundle bundle, Throwable th, Collection<OsgiServiceDependencyEvent> nestedEvents, Filter filter) {
        super(source, bundle, th);
        this.dependencyEvents = nestedEvents;
        this.dependenciesFilter = filter;
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
}

