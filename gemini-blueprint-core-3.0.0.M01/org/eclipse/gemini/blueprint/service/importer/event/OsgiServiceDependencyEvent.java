/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.service.importer.event;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency;
import org.springframework.context.ApplicationEvent;
import org.springframework.util.Assert;

public abstract class OsgiServiceDependencyEvent
extends ApplicationEvent {
    private final OsgiServiceDependency dependency;

    public OsgiServiceDependencyEvent(Object source, OsgiServiceDependency dependency) {
        super(source);
        Assert.notNull((Object)dependency);
        this.dependency = dependency;
    }

    public OsgiServiceDependency getServiceDependency() {
        return this.dependency;
    }
}

