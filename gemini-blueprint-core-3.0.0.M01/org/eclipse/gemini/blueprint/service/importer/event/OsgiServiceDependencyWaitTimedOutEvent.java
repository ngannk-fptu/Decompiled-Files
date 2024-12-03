/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.importer.event;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyEvent;

public class OsgiServiceDependencyWaitTimedOutEvent
extends OsgiServiceDependencyEvent {
    private final long elapsedTime;

    public OsgiServiceDependencyWaitTimedOutEvent(Object source, OsgiServiceDependency dependency, long elapsedTime) {
        super(source, dependency);
        this.elapsedTime = elapsedTime;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }
}

