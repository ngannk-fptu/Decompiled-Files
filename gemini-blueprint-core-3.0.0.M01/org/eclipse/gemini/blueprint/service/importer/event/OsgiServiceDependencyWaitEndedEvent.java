/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.importer.event;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyEvent;

public class OsgiServiceDependencyWaitEndedEvent
extends OsgiServiceDependencyEvent {
    private final long waitedTime;

    public OsgiServiceDependencyWaitEndedEvent(Object source, OsgiServiceDependency dependency, long elapsedTime) {
        super(source, dependency);
        this.waitedTime = elapsedTime;
    }

    public long getElapsedTime() {
        return this.waitedTime;
    }
}

