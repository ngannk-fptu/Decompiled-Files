/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.MapMaker
 */
package com.atlassian.event.internal;

import com.atlassian.event.spi.ListenerInvoker;
import com.google.common.collect.MapMaker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

class InvokerRegistry
implements Iterable<InvokerRegistration> {
    static final InvokerRegistry EMPTY = new InvokerRegistry();
    private final ConcurrentMap<Object, Collection<InvokerRegistration>> invokers = new MapMaker().weakKeys().makeMap();

    InvokerRegistry() {
    }

    void remove(Object listener) {
        this.invokers.remove(listener);
    }

    void add(Object listener, ListenerInvoker invoker, int order) {
        this.invokers.computeIfAbsent(listener, k -> new ArrayList()).add(new InvokerRegistration(invoker, order));
    }

    @Override
    public Iterator<InvokerRegistration> iterator() {
        return this.invokers.values().stream().flatMap(Collection::stream).iterator();
    }

    static class InvokerRegistration {
        private final ListenerInvoker listenerInvoker;
        private final int order;

        InvokerRegistration(ListenerInvoker listenerInvoker, int order) {
            this.listenerInvoker = listenerInvoker;
            this.order = order;
        }

        ListenerInvoker getListenerInvoker() {
            return this.listenerInvoker;
        }

        int getOrder() {
            return this.order;
        }
    }
}

