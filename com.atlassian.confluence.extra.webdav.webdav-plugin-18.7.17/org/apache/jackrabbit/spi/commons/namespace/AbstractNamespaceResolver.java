/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.namespace;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceListener;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceResolver;

public abstract class AbstractNamespaceResolver
implements NamespaceResolver {
    private final Set listeners;

    public AbstractNamespaceResolver() {
        this(false);
    }

    public AbstractNamespaceResolver(boolean supportListeners) {
        this.listeners = supportListeners ? new HashSet() : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addListener(NamespaceListener listener) {
        if (this.listeners == null) {
            throw new UnsupportedOperationException("addListener");
        }
        Set set = this.listeners;
        synchronized (set) {
            this.listeners.add(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeListener(NamespaceListener listener) {
        if (this.listeners == null) {
            throw new UnsupportedOperationException("removeListener");
        }
        Set set = this.listeners;
        synchronized (set) {
            this.listeners.remove(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void notifyNamespaceAdded(String prefix, String uri) {
        NamespaceListener[] currentListeners;
        if (this.listeners == null) {
            throw new UnsupportedOperationException("notifyNamespaceAdded");
        }
        Set set = this.listeners;
        synchronized (set) {
            int i = 0;
            currentListeners = new NamespaceListener[this.listeners.size()];
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                currentListeners[i++] = (NamespaceListener)it.next();
            }
        }
        for (int i = 0; i < currentListeners.length; ++i) {
            currentListeners[i].namespaceAdded(prefix, uri);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void notifyNamespaceRemapped(String oldPrefix, String newPrefix, String uri) {
        NamespaceListener[] currentListeners;
        if (this.listeners == null) {
            throw new UnsupportedOperationException("notifyNamespaceRemapped");
        }
        Set set = this.listeners;
        synchronized (set) {
            int i = 0;
            currentListeners = new NamespaceListener[this.listeners.size()];
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                currentListeners[i++] = (NamespaceListener)it.next();
            }
        }
        for (int i = 0; i < currentListeners.length; ++i) {
            currentListeners[i].namespaceRemapped(oldPrefix, newPrefix, uri);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void notifyNamespaceRemoved(String uri) {
        NamespaceListener[] currentListeners;
        if (this.listeners == null) {
            throw new UnsupportedOperationException("notifyNamespaceRemapped");
        }
        Set set = this.listeners;
        synchronized (set) {
            int i = 0;
            currentListeners = new NamespaceListener[this.listeners.size()];
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                currentListeners[i++] = (NamespaceListener)it.next();
            }
        }
        for (int i = 0; i < currentListeners.length; ++i) {
            currentListeners[i].namespaceRemoved(uri);
        }
    }
}

