/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.pool;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.nio.reactor.SessionRequest;
import org.apache.http.pool.PoolEntry;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

abstract class RouteSpecificPool<T, C, E extends PoolEntry<T, C>> {
    private final T route;
    private final Set<E> leased;
    private final LinkedList<E> available;
    private final Map<SessionRequest, BasicFuture<E>> pending;

    RouteSpecificPool(T route) {
        this.route = route;
        this.leased = new HashSet();
        this.available = new LinkedList();
        this.pending = new HashMap<SessionRequest, BasicFuture<E>>();
    }

    public T getRoute() {
        return this.route;
    }

    protected abstract E createEntry(T var1, C var2);

    public int getLeasedCount() {
        return this.leased.size();
    }

    public int getPendingCount() {
        return this.pending.size();
    }

    public int getAvailableCount() {
        return this.available.size();
    }

    public int getAllocatedCount() {
        return this.available.size() + this.leased.size() + this.pending.size();
    }

    public E getFree(Object state) {
        if (!this.available.isEmpty()) {
            PoolEntry entry;
            Iterator it;
            if (state != null) {
                it = this.available.iterator();
                while (it.hasNext()) {
                    entry = (PoolEntry)it.next();
                    if (!state.equals(entry.getState())) continue;
                    it.remove();
                    this.leased.add(entry);
                    return (E)entry;
                }
            }
            it = this.available.iterator();
            while (it.hasNext()) {
                entry = (PoolEntry)it.next();
                if (entry.getState() != null) continue;
                it.remove();
                this.leased.add(entry);
                return (E)entry;
            }
        }
        return null;
    }

    public E getLastUsed() {
        return (E)(this.available.isEmpty() ? null : (PoolEntry)this.available.getLast());
    }

    public boolean remove(E entry) {
        Args.notNull(entry, "Pool entry");
        return this.available.remove(entry) || this.leased.remove(entry);
    }

    public void free(E entry, boolean reusable) {
        Args.notNull(entry, "Pool entry");
        boolean found = this.leased.remove(entry);
        Asserts.check(found, "Entry %s has not been leased from this pool", entry);
        if (reusable) {
            this.available.addFirst(entry);
        }
    }

    public void addPending(SessionRequest request, BasicFuture<E> future) {
        this.pending.put(request, future);
    }

    private BasicFuture<E> removeRequest(SessionRequest request) {
        return this.pending.remove(request);
    }

    public E createEntry(SessionRequest request, C conn) {
        E entry = this.createEntry(this.route, conn);
        this.leased.add(entry);
        return entry;
    }

    public boolean completed(SessionRequest request, E entry) {
        BasicFuture<E> future = this.removeRequest(request);
        if (future != null) {
            return future.completed(entry);
        }
        request.cancel();
        return false;
    }

    public void cancelled(SessionRequest request) {
        BasicFuture<E> future = this.removeRequest(request);
        if (future != null) {
            future.cancel(true);
        }
    }

    public void failed(SessionRequest request, Exception ex) {
        BasicFuture<E> future = this.removeRequest(request);
        if (future != null) {
            future.failed(ex);
        }
    }

    public void timeout(SessionRequest request) {
        BasicFuture<E> future = this.removeRequest(request);
        if (future != null) {
            future.failed(new ConnectException("Timeout connecting to [" + request.getRemoteAddress() + "]"));
        }
    }

    public void shutdown() {
        for (SessionRequest request : this.pending.keySet()) {
            request.cancel();
        }
        this.pending.clear();
        for (PoolEntry entry : this.available) {
            entry.close();
        }
        this.available.clear();
        for (PoolEntry entry : this.leased) {
            entry.close();
        }
        this.leased.clear();
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[route: ");
        buffer.append(this.route);
        buffer.append("][leased: ");
        buffer.append(this.leased.size());
        buffer.append("][available: ");
        buffer.append(this.available.size());
        buffer.append("][pending: ");
        buffer.append(this.pending.size());
        buffer.append("]");
        return buffer.toString();
    }
}

