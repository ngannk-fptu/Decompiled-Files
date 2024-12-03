/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.Bundle
 */
package org.eclipse.gemini.blueprint.extender.internal.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;

class LazyBundleRegistry<T> {
    private final Log log;
    private final ConcurrentMap<Bundle, T> activeBundles = new ConcurrentHashMap<Bundle, T>(8);
    private final ConcurrentMap<Bundle, Boolean> lazyBundles = new ConcurrentHashMap<Bundle, Boolean>(8);
    private final List<Bundle> promotionQueue = new ArrayList<Bundle>(4);
    private volatile AtomicInteger threadCounter = new AtomicInteger();
    private final Condition condition;
    private final Activator<T> activator;

    LazyBundleRegistry(Condition promotionCondition, Activator<T> activator, Log log) {
        this.condition = promotionCondition;
        this.activator = activator;
        this.log = log;
    }

    void add(Bundle bundle, boolean isLazy, boolean applyCondition) {
        if (isLazy) {
            this.lazyBundles.put(bundle, applyCondition);
        } else {
            this.activeBundles.put(bundle, this.activator.activate(bundle));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean remove(Bundle bundle) {
        boolean value = false;
        value = this.activeBundles.remove(bundle) != null;
        value |= this.lazyBundles.remove(bundle) != null;
        List<Bundle> list = this.promotionQueue;
        synchronized (list) {
        }
        return value |= this.promotionQueue.remove(bundle);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Could not resolve type clashes
     */
    <V> V apply(Operation<T, V> action) throws Exception {
        boolean debug = this.log.isDebugEnabled();
        this.threadCounter.incrementAndGet();
        try {
            for (Object result : this.activeBundles.values()) {
                V value = action.operate(result);
                if (value == null) continue;
                V v = value;
                return v;
            }
            for (Map.Entry entry : this.lazyBundles.entrySet()) {
                Object bundle = (Bundle)entry.getKey();
                Boolean applyCondition = (Boolean)entry.getValue();
                if (Boolean.FALSE.equals(applyCondition) || Boolean.TRUE.equals(applyCondition) && this.condition.pass((Bundle)bundle)) {
                    V value;
                    Object result = this.activeBundles.putIfAbsent((Bundle)bundle, this.activator.activate((Bundle)bundle));
                    if (result == null) {
                        result = this.activeBundles.get(bundle);
                        List<Bundle> builder = this.promotionQueue;
                        synchronized (builder) {
                            this.promotionQueue.add((Bundle)bundle);
                        }
                    }
                    if (result == null || (value = action.operate(result)) == null) continue;
                    V v = value;
                    return v;
                }
                this.lazyBundles.remove(bundle);
                if (!debug) continue;
                this.log.debug((Object)("Activated lazy bundle " + OsgiStringUtils.nullSafeNameAndSymName((Bundle)bundle) + " but found it unsuitable"));
            }
        }
        finally {
            Bundle[] loggedBundles = null;
            List<Bundle> list = this.promotionQueue;
            synchronized (list) {
                if (this.threadCounter.decrementAndGet() == 0) {
                    for (Bundle bundle : this.promotionQueue) {
                        if (this.lazyBundles.remove(bundle) != null) continue;
                        this.activeBundles.remove(bundle);
                    }
                }
                if (debug && !this.promotionQueue.isEmpty()) {
                    loggedBundles = this.promotionQueue.toArray(new Bundle[this.promotionQueue.size()]);
                }
                this.promotionQueue.clear();
            }
            if (loggedBundles != null) {
                StringBuilder builder = new StringBuilder("Activated (and validated) lazy bundles [ ");
                for (Bundle bundle : loggedBundles) {
                    builder.append(OsgiStringUtils.nullSafeNameAndSymName((Bundle)bundle));
                    builder.append(" ");
                }
                builder.append("]");
                this.log.debug((Object)builder);
            }
        }
    }

    public void clear() {
        this.promotionQueue.clear();
        this.lazyBundles.clear();
        this.activeBundles.clear();
    }

    static interface Operation<T, V> {
        public V operate(T var1) throws Exception;
    }

    static interface Activator<V> {
        public V activate(Bundle var1);
    }

    static interface Condition {
        public boolean pass(Bundle var1);
    }
}

