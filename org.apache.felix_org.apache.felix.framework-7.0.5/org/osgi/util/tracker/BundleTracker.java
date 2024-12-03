/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.util.tracker;

import java.util.HashMap;
import java.util.Map;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.util.tracker.AbstractTracked;
import org.osgi.util.tracker.BundleTrackerCustomizer;

@ConsumerType
public class BundleTracker<T>
implements BundleTrackerCustomizer<T> {
    static final boolean DEBUG = false;
    protected final BundleContext context;
    final BundleTrackerCustomizer<T> customizer;
    private volatile Tracked tracked;
    final int mask;

    private Tracked tracked() {
        return this.tracked;
    }

    public BundleTracker(BundleContext context, int stateMask, BundleTrackerCustomizer<T> customizer) {
        this.context = context;
        this.mask = stateMask;
        this.customizer = customizer == null ? this : customizer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void open() {
        Tracked t;
        BundleTracker bundleTracker = this;
        synchronized (bundleTracker) {
            if (this.tracked != null) {
                return;
            }
            Tracked tracked = t = new Tracked();
            synchronized (tracked) {
                this.context.addBundleListener(t);
                Bundle[] bundles = this.context.getBundles();
                if (bundles != null) {
                    int length = bundles.length;
                    for (int i = 0; i < length; ++i) {
                        int state = bundles[i].getState();
                        if ((state & this.mask) != 0) continue;
                        bundles[i] = null;
                    }
                    t.setInitial(bundles);
                }
            }
            this.tracked = t;
        }
        t.trackInitial();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() {
        Bundle[] bundles;
        Tracked outgoing;
        BundleTracker bundleTracker = this;
        synchronized (bundleTracker) {
            outgoing = this.tracked;
            if (outgoing == null) {
                return;
            }
            outgoing.close();
            bundles = this.getBundles();
            this.tracked = null;
            try {
                this.context.removeBundleListener(outgoing);
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
        }
        if (bundles != null) {
            for (int i = 0; i < bundles.length; ++i) {
                outgoing.untrack(bundles[i], null);
            }
        }
    }

    @Override
    public T addingBundle(Bundle bundle, BundleEvent event) {
        Bundle result = bundle;
        return (T)result;
    }

    @Override
    public void modifiedBundle(Bundle bundle, BundleEvent event, T object) {
    }

    @Override
    public void removedBundle(Bundle bundle, BundleEvent event, T object) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Bundle[] getBundles() {
        Tracked t = this.tracked();
        if (t == null) {
            return null;
        }
        Tracked tracked = t;
        synchronized (tracked) {
            if (t.isEmpty()) {
                return null;
            }
            return t.copyKeys(new Bundle[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T getObject(Bundle bundle) {
        Tracked t = this.tracked();
        if (t == null) {
            return null;
        }
        Tracked tracked = t;
        synchronized (tracked) {
            return t.getCustomizedObject(bundle);
        }
    }

    public void remove(Bundle bundle) {
        Tracked t = this.tracked();
        if (t == null) {
            return;
        }
        t.untrack(bundle, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int size() {
        Tracked t = this.tracked();
        if (t == null) {
            return 0;
        }
        Tracked tracked = t;
        synchronized (tracked) {
            return t.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getTrackingCount() {
        Tracked t = this.tracked();
        if (t == null) {
            return -1;
        }
        Tracked tracked = t;
        synchronized (tracked) {
            return t.getTrackingCount();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<Bundle, T> getTracked() {
        HashMap map = new HashMap();
        Tracked t = this.tracked();
        if (t == null) {
            return map;
        }
        Tracked tracked = t;
        synchronized (tracked) {
            return t.copyEntries(map);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isEmpty() {
        Tracked t = this.tracked();
        if (t == null) {
            return true;
        }
        Tracked tracked = t;
        synchronized (tracked) {
            return t.isEmpty();
        }
    }

    private final class Tracked
    extends AbstractTracked<Bundle, T, BundleEvent>
    implements SynchronousBundleListener {
        Tracked() {
        }

        @Override
        public void bundleChanged(BundleEvent event) {
            if (this.closed) {
                return;
            }
            Bundle bundle = event.getBundle();
            int state = bundle.getState();
            if ((state & BundleTracker.this.mask) != 0) {
                this.track(bundle, event);
            } else {
                this.untrack(bundle, event);
            }
        }

        @Override
        T customizerAdding(Bundle item, BundleEvent related) {
            return BundleTracker.this.customizer.addingBundle(item, related);
        }

        @Override
        void customizerModified(Bundle item, BundleEvent related, T object) {
            BundleTracker.this.customizer.modifiedBundle(item, related, object);
        }

        @Override
        void customizerRemoved(Bundle item, BundleEvent related, T object) {
            BundleTracker.this.customizer.removedBundle(item, related, object);
        }
    }
}

