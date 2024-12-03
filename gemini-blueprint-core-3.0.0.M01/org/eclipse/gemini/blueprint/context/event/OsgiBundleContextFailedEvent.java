/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.springframework.context.ApplicationContext
 */
package org.eclipse.gemini.blueprint.context.event;

import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.osgi.framework.Bundle;
import org.springframework.context.ApplicationContext;

public class OsgiBundleContextFailedEvent
extends OsgiBundleApplicationContextEvent {
    private final Throwable cause;

    public OsgiBundleContextFailedEvent(ApplicationContext source, Bundle bundle, Throwable cause) {
        super(source, bundle);
        this.cause = cause;
    }

    public final Throwable getFailureCause() {
        return this.cause;
    }
}

