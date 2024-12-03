/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleEvent
 *  org.osgi.framework.SynchronousBundleListener
 */
package org.eclipse.gemini.blueprint.extender.internal.activator.listeners;

import java.util.Map;
import java.util.WeakHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;

public abstract class BaseListener
implements SynchronousBundleListener {
    public static final int LAZY_ACTIVATION_EVENT_TYPE = 512;
    protected final Log log = LogFactory.getLog(this.getClass());
    private volatile boolean isClosed = false;
    protected final Map<Bundle, Object> lazyBundleCache = new WeakHashMap<Bundle, Object>();
    private final Object VALUE = new Object();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void push(Bundle bundle) {
        Map<Bundle, Object> map = this.lazyBundleCache;
        synchronized (map) {
            this.lazyBundleCache.put(bundle, this.VALUE);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean pop(Bundle bundle) {
        Map<Bundle, Object> map = this.lazyBundleCache;
        synchronized (map) {
            return this.lazyBundleCache.remove(bundle) != null;
        }
    }

    public void bundleChanged(BundleEvent event) {
        boolean trace = this.log.isTraceEnabled();
        if (this.isClosed) {
            if (trace) {
                this.log.trace((Object)"Listener is closed; events are being ignored");
            }
            return;
        }
        if (trace) {
            this.log.trace((Object)("Processing bundle event [" + OsgiStringUtils.nullSafeToString((BundleEvent)event) + "] for bundle [" + OsgiStringUtils.nullSafeSymbolicName((Bundle)event.getBundle()) + "]"));
        }
        try {
            this.handleEvent(event);
        }
        catch (Exception ex) {
            this.log.warn((Object)("Got exception while handling event " + event), (Throwable)ex);
        }
    }

    protected abstract void handleEvent(BundleEvent var1);

    public void close() {
        this.isClosed = true;
    }
}

