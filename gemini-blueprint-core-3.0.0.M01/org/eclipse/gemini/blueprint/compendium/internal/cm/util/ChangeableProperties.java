/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.compendium.internal.cm.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.eclipse.gemini.blueprint.service.exporter.support.ServicePropertiesChangeEvent;
import org.eclipse.gemini.blueprint.service.exporter.support.ServicePropertiesChangeListener;
import org.eclipse.gemini.blueprint.service.exporter.support.ServicePropertiesListenerManager;

public class ChangeableProperties
extends Properties
implements ServicePropertiesListenerManager {
    private List<ServicePropertiesChangeListener> listeners = Collections.synchronizedList(new ArrayList(4));

    @Override
    public void addListener(ServicePropertiesChangeListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
    }

    @Override
    public void removeListener(ServicePropertiesChangeListener listener) {
        if (listener != null) {
            this.listeners.remove(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void notifyListeners() {
        ServicePropertiesChangeEvent event = new ServicePropertiesChangeEvent(this);
        List<ServicePropertiesChangeListener> list = this.listeners;
        synchronized (list) {
            for (ServicePropertiesChangeListener listener : this.listeners) {
                listener.propertiesChange(event);
            }
        }
    }
}

