/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.event.ApplicationEventMulticaster
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.context.event;

import org.eclipse.gemini.blueprint.context.event.ApplicationListenerAdapter;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticaster;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.util.Assert;

public class OsgiBundleApplicationContextEventMulticasterAdapter
implements OsgiBundleApplicationContextEventMulticaster {
    private final ApplicationEventMulticaster delegatedMulticaster;

    public OsgiBundleApplicationContextEventMulticasterAdapter(ApplicationEventMulticaster delegatedMulticaster) {
        Assert.notNull((Object)delegatedMulticaster);
        this.delegatedMulticaster = delegatedMulticaster;
    }

    @Override
    public void addApplicationListener(OsgiBundleApplicationContextListener osgiListener) {
        Assert.notNull((Object)osgiListener);
        this.delegatedMulticaster.addApplicationListener(ApplicationListenerAdapter.createAdapter(osgiListener));
    }

    @Override
    public void multicastEvent(OsgiBundleApplicationContextEvent osgiEvent) {
        this.delegatedMulticaster.multicastEvent((ApplicationEvent)osgiEvent);
    }

    @Override
    public void removeAllListeners() {
        this.delegatedMulticaster.removeAllListeners();
    }

    @Override
    public void removeApplicationListener(OsgiBundleApplicationContextListener osgiListener) {
        Assert.notNull(null);
        this.delegatedMulticaster.removeApplicationListener(ApplicationListenerAdapter.createAdapter(osgiListener));
    }
}

