/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.composite;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeter;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

abstract class AbstractCompositeMeter<T extends Meter>
extends AbstractMeter
implements CompositeMeter {
    private final AtomicBoolean childrenGuard = new AtomicBoolean();
    private Map<MeterRegistry, T> children = Collections.emptyMap();
    @Nullable
    private volatile T noopMeter;

    AbstractCompositeMeter(Meter.Id id) {
        super(id);
    }

    abstract T newNoopMeter();

    @Nullable
    abstract T registerNewMeter(MeterRegistry var1);

    final Iterable<T> getChildren() {
        return this.children.values();
    }

    T firstChild() {
        Iterator<T> i = this.children.values().iterator();
        if (i.hasNext()) {
            return (T)((Meter)i.next());
        }
        T noopMeter = this.noopMeter;
        if (noopMeter != null) {
            return noopMeter;
        }
        this.noopMeter = this.newNoopMeter();
        return this.noopMeter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void add(MeterRegistry registry) {
        T newMeter = this.registerNewMeter(registry);
        if (newMeter == null) {
            return;
        }
        while (!this.childrenGuard.compareAndSet(false, true)) {
        }
        try {
            IdentityHashMap<MeterRegistry, T> newChildren = new IdentityHashMap<MeterRegistry, T>(this.children);
            newChildren.put(registry, newMeter);
            this.children = newChildren;
        }
        finally {
            this.childrenGuard.set(false);
        }
    }

    @Override
    @Deprecated
    public final void remove(MeterRegistry registry) {
        while (!this.childrenGuard.compareAndSet(false, true)) {
        }
        try {
            IdentityHashMap<MeterRegistry, T> newChildren = new IdentityHashMap<MeterRegistry, T>(this.children);
            newChildren.remove(registry);
            this.children = newChildren;
        }
        finally {
            this.childrenGuard.set(false);
        }
    }
}

