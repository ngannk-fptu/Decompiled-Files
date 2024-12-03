/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.importer.event;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyEvent;

public class OsgiServiceDependencyWaitStartingEvent
extends OsgiServiceDependencyEvent {
    private final long timeToWait;

    public OsgiServiceDependencyWaitStartingEvent(Object source, OsgiServiceDependency dependency, long timeToWait) {
        super(source, dependency);
        this.timeToWait = timeToWait;
    }

    public long getTimeToWait() {
        return this.timeToWait;
    }
}

