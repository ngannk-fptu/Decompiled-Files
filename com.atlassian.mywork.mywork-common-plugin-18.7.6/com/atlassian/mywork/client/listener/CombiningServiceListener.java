/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.mywork.client.listener;

import com.atlassian.mywork.client.listener.ServiceListener;
import com.google.common.base.Function;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

public class CombiningServiceListener
implements ServiceListener {
    private final Iterable<ServiceListener> listeners;

    public CombiningServiceListener(Iterable<ServiceListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public <T> Closeable addListener(Class<T> type, Function<T, Void> callback) {
        ArrayList<Closeable> closeables = new ArrayList<Closeable>();
        for (ServiceListener listener : this.listeners) {
            closeables.add(listener.addListener(type, callback));
        }
        return new CombiningCloseable(closeables);
    }

    private static class CombiningCloseable
    implements Closeable {
        private final Iterable<Closeable> closeables;

        private CombiningCloseable(Iterable<Closeable> closeables) {
            this.closeables = closeables;
        }

        @Override
        public void close() throws IOException {
            for (Closeable closeable : this.closeables) {
                closeable.close();
            }
        }
    }
}

