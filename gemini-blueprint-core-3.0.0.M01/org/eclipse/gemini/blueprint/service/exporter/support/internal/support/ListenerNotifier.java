/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.exporter.support.internal.support;

import java.util.Map;
import org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener;

public class ListenerNotifier {
    private final OsgiServiceRegistrationListener[] listeners;

    public ListenerNotifier(OsgiServiceRegistrationListener[] listeners) {
        this.listeners = listeners;
    }

    public void callRegister(Object service, Map properties) {
        for (OsgiServiceRegistrationListener listener : this.listeners) {
            if (listener == null) continue;
            try {
                listener.registered(service, properties);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public void callUnregister(Object service, Map properties) {
        for (OsgiServiceRegistrationListener listener : this.listeners) {
            if (listener == null) continue;
            try {
                listener.unregistered(service, properties);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}

