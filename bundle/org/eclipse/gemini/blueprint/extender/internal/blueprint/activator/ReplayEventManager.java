/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleEvent
 *  org.osgi.framework.BundleListener
 *  org.osgi.service.blueprint.container.BlueprintEvent
 *  org.osgi.service.blueprint.container.BlueprintListener
 */
package org.eclipse.gemini.blueprint.extender.internal.blueprint.activator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.blueprint.container.BlueprintEvent;
import org.osgi.service.blueprint.container.BlueprintListener;

class ReplayEventManager {
    private static final Log log = LogFactory.getLog(ReplayEventManager.class);
    private final Map<Bundle, BlueprintEvent> events = Collections.synchronizedMap(new LinkedHashMap());
    private final BundleContext bundleContext;
    private final BundleListener listener = new BundleListener(){

        public void bundleChanged(BundleEvent event) {
            if (4 == event.getType() || 16 == event.getType() || 64 == event.getType()) {
                BlueprintEvent removed = (BlueprintEvent)ReplayEventManager.this.events.remove(event.getBundle());
                if (log.isTraceEnabled()) {
                    log.trace((Object)("Removed  bundle " + event.getBundle() + " for sending replayes events; last one was " + removed));
                }
            }
        }
    };

    ReplayEventManager(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        bundleContext.addBundleListener(this.listener);
    }

    void addEvent(BlueprintEvent event) {
        BlueprintEvent replay = new BlueprintEvent(event, true);
        Bundle bnd = replay.getBundle();
        if (bnd.getState() == 32 || bnd.getState() == 8 || bnd.getState() == 16) {
            this.events.put(bnd, replay);
            if (log.isTraceEnabled()) {
                log.trace((Object)("Adding replay event  " + replay.getType() + " for bundle " + replay.getBundle()));
            }
        } else if (log.isTraceEnabled()) {
            log.trace((Object)("Replay event " + replay.getType() + " ignored; owning bundle has been uninstalled " + bnd));
            this.events.remove(bnd);
        }
    }

    void destroy() {
        this.events.clear();
        try {
            this.bundleContext.removeBundleListener(this.listener);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void dispatchReplayEvents(BlueprintListener listener) {
        Map<Bundle, BlueprintEvent> map = this.events;
        synchronized (map) {
            for (BlueprintEvent event : this.events.values()) {
                listener.blueprintEvent(event);
            }
        }
    }
}

