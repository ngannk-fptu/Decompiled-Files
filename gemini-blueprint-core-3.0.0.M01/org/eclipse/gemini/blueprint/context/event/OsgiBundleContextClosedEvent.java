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

public class OsgiBundleContextClosedEvent
extends OsgiBundleApplicationContextEvent {
    private Throwable cause;

    public OsgiBundleContextClosedEvent(ApplicationContext source, Bundle bundle, Throwable cause) {
        super(source, bundle);
        this.cause = cause;
    }

    public OsgiBundleContextClosedEvent(ApplicationContext source, Bundle bundle) {
        super(source, bundle);
    }

    public final Throwable getFailureCause() {
        return this.cause;
    }
}

