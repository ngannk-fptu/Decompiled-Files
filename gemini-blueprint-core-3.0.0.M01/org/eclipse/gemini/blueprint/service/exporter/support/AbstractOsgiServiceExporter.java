/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.DisposableBean
 */
package org.eclipse.gemini.blueprint.service.exporter.support;

import org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.support.ListenerNotifier;
import org.springframework.beans.factory.DisposableBean;

abstract class AbstractOsgiServiceExporter
implements DisposableBean {
    private OsgiServiceRegistrationListener[] listeners = new OsgiServiceRegistrationListener[0];
    private boolean lazyListeners = false;
    private ListenerNotifier notifier;

    AbstractOsgiServiceExporter() {
    }

    ListenerNotifier getNotifier() {
        return this.notifier;
    }

    public void setListeners(OsgiServiceRegistrationListener[] listeners) {
        if (listeners != null) {
            this.listeners = listeners;
            this.notifier = new ListenerNotifier(listeners);
        }
    }

    public void destroy() {
        this.unregisterService();
    }

    abstract void registerService();

    abstract void unregisterService();

    public void setLazyListeners(boolean lazyListeners) {
        this.lazyListeners = lazyListeners;
    }

    public boolean getLazyListeners() {
        return this.lazyListeners;
    }
}

