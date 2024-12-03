/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent
 *  org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyEvent
 *  org.osgi.framework.Bundle
 *  org.springframework.context.ApplicationContext
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.extender.event;

import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyEvent;
import org.osgi.framework.Bundle;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

public class BootstrappingDependencyEvent
extends OsgiBundleApplicationContextEvent {
    private final OsgiServiceDependencyEvent dependencyEvent;

    public BootstrappingDependencyEvent(ApplicationContext source, Bundle bundle, OsgiServiceDependencyEvent nestedEvent) {
        super(source, bundle);
        Assert.notNull((Object)nestedEvent);
        this.dependencyEvent = nestedEvent;
    }

    public OsgiServiceDependencyEvent getDependencyEvent() {
        return this.dependencyEvent;
    }
}

