/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.NanoTime
 *  org.eclipse.jetty.util.annotation.ManagedAttribute
 *  org.eclipse.jetty.util.component.Dumpable
 *  org.eclipse.jetty.util.component.DumpableCollection
 *  org.eclipse.jetty.util.thread.Scheduler
 *  org.eclipse.jetty.util.thread.Scheduler$Task
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.client.DuplexConnectionPool;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.api.Connection;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidatingConnectionPool
extends DuplexConnectionPool {
    private static final Logger LOG = LoggerFactory.getLogger(ValidatingConnectionPool.class);
    private final Scheduler scheduler;
    private final long timeout;
    private final Map<Connection, Holder> quarantine;

    public ValidatingConnectionPool(HttpDestination destination, int maxConnections, Callback requester, Scheduler scheduler, long timeout) {
        super(destination, maxConnections, requester);
        this.scheduler = scheduler;
        this.timeout = timeout;
        this.quarantine = new ConcurrentHashMap<Connection, Holder>(maxConnections);
    }

    @ManagedAttribute(value="The number of validating connections", readonly=true)
    public int getValidatingConnectionCount() {
        return this.quarantine.size();
    }

    @Override
    public boolean release(Connection connection) {
        Holder holder = new Holder(connection);
        holder.task = this.scheduler.schedule((Runnable)holder, this.timeout, TimeUnit.MILLISECONDS);
        this.quarantine.put(connection, holder);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Validating for {}ms {}", (Object)this.timeout, (Object)connection);
        }
        this.released(connection);
        return true;
    }

    @Override
    public boolean remove(Connection connection) {
        boolean cancelled;
        Holder holder = this.quarantine.remove(connection);
        if (holder == null) {
            return super.remove(connection);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Removed while validating {}", (Object)connection);
        }
        if (cancelled = holder.cancel()) {
            return this.remove(connection, true);
        }
        return super.remove(connection);
    }

    @Override
    public void dump(Appendable out, String indent) throws IOException {
        DumpableCollection toDump = new DumpableCollection("quarantine", this.quarantine.values());
        Dumpable.dumpObjects((Appendable)out, (String)indent, (Object)this, (Object[])new Object[]{toDump});
    }

    @Override
    public String toString() {
        int size = this.quarantine.size();
        return String.format("%s[v=%d]", super.toString(), size);
    }

    private class Holder
    implements Runnable {
        private final long creationNanoTime = NanoTime.now();
        private final AtomicBoolean done = new AtomicBoolean();
        private final Connection connection;
        public Scheduler.Task task;

        public Holder(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            if (this.done.compareAndSet(false, true)) {
                boolean closed = ValidatingConnectionPool.this.isClosed();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Validated {}", (Object)this.connection);
                }
                ValidatingConnectionPool.this.quarantine.remove(this.connection);
                if (!closed) {
                    ValidatingConnectionPool.this.deactivate(this.connection);
                }
                ValidatingConnectionPool.this.idle(this.connection, closed);
                ValidatingConnectionPool.this.proceed();
            }
        }

        public boolean cancel() {
            if (this.done.compareAndSet(false, true)) {
                this.task.cancel();
                return true;
            }
            return false;
        }

        public String toString() {
            return String.format("%s[validationLeft=%dms]", this.connection, ValidatingConnectionPool.this.timeout - NanoTime.millisSince((long)this.creationNanoTime));
        }
    }
}

