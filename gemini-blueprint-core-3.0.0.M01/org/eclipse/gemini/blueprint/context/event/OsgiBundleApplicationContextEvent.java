/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.event.ApplicationContextEvent
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.context.event;

import org.osgi.framework.Bundle;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.util.Assert;

public abstract class OsgiBundleApplicationContextEvent
extends ApplicationContextEvent {
    private final Bundle bundle;

    public OsgiBundleApplicationContextEvent(ApplicationContext source, Bundle bundle) {
        super(source);
        Assert.notNull((Object)bundle);
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return this.bundle;
    }
}

