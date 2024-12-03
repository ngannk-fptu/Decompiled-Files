/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.ProcessorUtils
 *  org.eclipse.jetty.util.annotation.ManagedAttribute
 *  org.eclipse.jetty.util.annotation.ManagedObject
 *  org.eclipse.jetty.util.component.ContainerLifeCycle
 *  org.eclipse.jetty.util.component.Dumpable
 *  org.eclipse.jetty.util.thread.Scheduler
 *  org.eclipse.jetty.util.thread.ThreadPool$SizedThreadPool
 *  org.eclipse.jetty.util.thread.ThreadPoolBudget
 *  org.eclipse.jetty.util.thread.ThreadPoolBudget$Lease
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.io;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ManagedSelector;
import org.eclipse.jetty.util.ProcessorUtils;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.util.thread.ThreadPoolBudget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject(value="Manager of the NIO Selectors")
public abstract class SelectorManager
extends ContainerLifeCycle
implements Dumpable {
    public static final int DEFAULT_CONNECT_TIMEOUT = 15000;
    protected static final Logger LOG = LoggerFactory.getLogger(SelectorManager.class);
    private final Executor executor;
    private final Scheduler scheduler;
    private final ManagedSelector[] _selectors;
    private final AtomicInteger _selectorIndex = new AtomicInteger();
    private final IntUnaryOperator _selectorIndexUpdate;
    private final List<AcceptListener> _acceptListeners = new CopyOnWriteArrayList<AcceptListener>();
    private long _connectTimeout = 15000L;
    private ThreadPoolBudget.Lease _lease;

    private static int defaultSelectors(Executor executor) {
        if (executor instanceof ThreadPool.SizedThreadPool) {
            int threads = ((ThreadPool.SizedThreadPool)executor).getMaxThreads();
            int cpus = ProcessorUtils.availableProcessors();
            return Math.max(1, Math.min(cpus / 2, threads / 16));
        }
        return Math.max(1, ProcessorUtils.availableProcessors() / 2);
    }

    protected SelectorManager(Executor executor, Scheduler scheduler) {
        this(executor, scheduler, -1);
    }

    protected SelectorManager(Executor executor, Scheduler scheduler, int selectors) {
        if (selectors <= 0) {
            selectors = SelectorManager.defaultSelectors(executor);
        }
        this.executor = executor;
        this.scheduler = scheduler;
        this._selectors = new ManagedSelector[selectors];
        this._selectorIndexUpdate = index -> (index + 1) % this._selectors.length;
    }

    @ManagedAttribute(value="The Executor")
    public Executor getExecutor() {
        return this.executor;
    }

    @ManagedAttribute(value="The Scheduler")
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    @ManagedAttribute(value="The Connection timeout (ms)")
    public long getConnectTimeout() {
        return this._connectTimeout;
    }

    public void setConnectTimeout(long milliseconds) {
        this._connectTimeout = milliseconds;
    }

    protected void execute(Runnable task) {
        this.executor.execute(task);
    }

    @ManagedAttribute(value="Total number of keys in all selectors", readonly=true)
    public int getTotalKeys() {
        int keys = 0;
        for (ManagedSelector selector : this._selectors) {
            if (selector == null) continue;
            keys += selector.getTotalKeys();
        }
        return keys;
    }

    @ManagedAttribute(value="The number of NIO Selectors")
    public int getSelectorCount() {
        return this._selectors.length;
    }

    protected ManagedSelector chooseSelector() {
        return this._selectors[this._selectorIndex.updateAndGet(this._selectorIndexUpdate)];
    }

    public void connect(SelectableChannel channel, Object attachment) {
        ManagedSelector set = this.chooseSelector();
        if (set != null) {
            ManagedSelector managedSelector = set;
            Objects.requireNonNull(managedSelector);
            set.submit(new ManagedSelector.Connect(managedSelector, channel, attachment));
        }
    }

    public void accept(SelectableChannel channel) {
        this.accept(channel, null);
    }

    public void accept(SelectableChannel channel, Object attachment) {
        ManagedSelector selector;
        ManagedSelector managedSelector = selector = this.chooseSelector();
        Objects.requireNonNull(managedSelector);
        selector.submit(new ManagedSelector.Accept(managedSelector, channel, attachment));
    }

    public Closeable acceptor(SelectableChannel server) {
        ManagedSelector selector;
        ManagedSelector managedSelector = selector = this.chooseSelector();
        Objects.requireNonNull(managedSelector);
        ManagedSelector.Acceptor acceptor = new ManagedSelector.Acceptor(managedSelector, server);
        selector.submit(acceptor);
        return acceptor;
    }

    protected void accepted(SelectableChannel channel) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void doStart() throws Exception {
        this._lease = ThreadPoolBudget.leaseFrom((Executor)this.getExecutor(), (Object)((Object)this), (int)this._selectors.length);
        for (int i = 0; i < this._selectors.length; ++i) {
            ManagedSelector selector;
            this._selectors[i] = selector = this.newSelector(i);
            this.addBean((Object)selector);
        }
        super.doStart();
    }

    protected ManagedSelector newSelector(int id) {
        return new ManagedSelector(this, id);
    }

    protected Selector newSelector() throws IOException {
        return Selector.open();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doStop() throws Exception {
        try {
            super.doStop();
        }
        finally {
            for (ManagedSelector selector : this._selectors) {
                if (selector == null) continue;
                this.removeBean((Object)selector);
            }
            Arrays.fill((Object[])this._selectors, null);
            if (this._lease != null) {
                this._lease.close();
            }
        }
    }

    protected void endPointOpened(EndPoint endpoint) {
    }

    protected void endPointClosed(EndPoint endpoint) {
    }

    public void connectionOpened(Connection connection, Object context) {
        try {
            connection.onOpen();
        }
        catch (Throwable x) {
            if (this.isRunning()) {
                LOG.warn("Exception while notifying connection {}", (Object)connection, (Object)x);
            } else {
                LOG.debug("Exception while notifying connection {}", (Object)connection, (Object)x);
            }
            throw x;
        }
    }

    public void connectionClosed(Connection connection, Throwable cause) {
        block2: {
            try {
                connection.onClose(cause);
            }
            catch (Throwable x) {
                if (!LOG.isDebugEnabled()) break block2;
                LOG.debug("Exception while notifying connection {}", (Object)connection, (Object)x);
            }
        }
    }

    protected boolean doFinishConnect(SelectableChannel channel) throws IOException {
        return ((SocketChannel)channel).finishConnect();
    }

    protected boolean isConnectionPending(SelectableChannel channel) {
        return ((SocketChannel)channel).isConnectionPending();
    }

    protected SelectableChannel doAccept(SelectableChannel server) throws IOException {
        return ((ServerSocketChannel)server).accept();
    }

    protected void connectionFailed(SelectableChannel channel, Throwable ex, Object attachment) {
        LOG.warn(String.format("%s - %s", channel, attachment), ex);
    }

    protected abstract EndPoint newEndPoint(SelectableChannel var1, ManagedSelector var2, SelectionKey var3) throws IOException;

    public abstract Connection newConnection(SelectableChannel var1, EndPoint var2, Object var3) throws IOException;

    public boolean addEventListener(EventListener listener) {
        if (super.addEventListener(listener)) {
            if (listener instanceof AcceptListener) {
                this._acceptListeners.add((AcceptListener)listener);
            }
            return true;
        }
        return false;
    }

    public boolean removeEventListener(EventListener listener) {
        if (super.removeEventListener(listener)) {
            if (listener instanceof AcceptListener) {
                this._acceptListeners.remove(listener);
            }
            return true;
        }
        return false;
    }

    protected void onAccepting(SelectableChannel channel) {
        for (AcceptListener l : this._acceptListeners) {
            try {
                l.onAccepting(channel);
            }
            catch (Throwable x) {
                LOG.warn("Failed to notify onAccepting on listener {}", (Object)l, (Object)x);
            }
        }
    }

    protected void onAcceptFailed(SelectableChannel channel, Throwable cause) {
        for (AcceptListener l : this._acceptListeners) {
            try {
                l.onAcceptFailed(channel, cause);
            }
            catch (Throwable x) {
                LOG.warn("Failed to notify onAcceptFailed on listener {}", (Object)l, (Object)x);
            }
        }
    }

    protected void onAccepted(SelectableChannel channel) {
        for (AcceptListener l : this._acceptListeners) {
            try {
                l.onAccepted(channel);
            }
            catch (Throwable x) {
                LOG.warn("Failed to notify onAccepted on listener {}", (Object)l, (Object)x);
            }
        }
    }

    public String toString() {
        return String.format("%s@%x[keys=%d]", ((Object)((Object)this)).getClass().getSimpleName(), ((Object)((Object)this)).hashCode(), this.getTotalKeys());
    }

    public static interface AcceptListener
    extends SelectorManagerListener {
        default public void onAccepting(SelectableChannel channel) {
        }

        default public void onAcceptFailed(SelectableChannel channel, Throwable cause) {
        }

        default public void onAccepted(SelectableChannel channel) {
        }
    }

    public static interface SelectorManagerListener
    extends EventListener {
    }
}

