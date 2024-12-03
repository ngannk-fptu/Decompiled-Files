/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.IO
 *  org.eclipse.jetty.util.thread.AutoLock
 *  org.eclipse.jetty.util.thread.Invocable$InvocationType
 *  org.eclipse.jetty.util.thread.Invocable$Task
 *  org.eclipse.jetty.util.thread.Scheduler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.io;

import java.io.Closeable;
import java.net.SocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.NetworkChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import org.eclipse.jetty.io.AbstractEndPoint;
import org.eclipse.jetty.io.ManagedSelector;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.Invocable;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SelectableChannelEndPoint
extends AbstractEndPoint
implements ManagedSelector.Selectable {
    private static final Logger LOG = LoggerFactory.getLogger(SelectableChannelEndPoint.class);
    private final AutoLock _lock = new AutoLock();
    private final SelectableChannel _channel;
    private final ManagedSelector _selector;
    private SelectionKey _key;
    private boolean _updatePending;
    private int _currentInterestOps;
    private int _desiredInterestOps;
    private final ManagedSelector.SelectorUpdate _updateKeyAction = this::updateKeyAction;
    private final Runnable _runFillable = new RunnableCloseable("runFillable"){

        public void run() {
            SelectableChannelEndPoint.this.getFillInterest().fillable();
        }

        public Invocable.InvocationType getInvocationType() {
            return SelectableChannelEndPoint.this.getFillInterest().getCallbackInvocationType();
        }
    };
    private final Runnable _runCompleteWrite = new RunnableCloseable("runCompleteWrite"){

        public void run() {
            SelectableChannelEndPoint.this.getWriteFlusher().completeWrite();
        }

        public Invocable.InvocationType getInvocationType() {
            return SelectableChannelEndPoint.this.getWriteFlusher().getCallbackInvocationType();
        }

        @Override
        public String toString() {
            return String.format("%s:%s:%s->%s", SelectableChannelEndPoint.this, this._operation, this.getInvocationType(), SelectableChannelEndPoint.this.getWriteFlusher());
        }
    };
    private final Runnable _runCompleteWriteFillable = new RunnableCloseable("runCompleteWriteFillable"){

        public void run() {
            SelectableChannelEndPoint.this.getWriteFlusher().completeWrite();
            SelectableChannelEndPoint.this.getFillInterest().fillable();
        }

        public Invocable.InvocationType getInvocationType() {
            Invocable.InvocationType flushT;
            Invocable.InvocationType fillT = SelectableChannelEndPoint.this.getFillInterest().getCallbackInvocationType();
            if (fillT == (flushT = SelectableChannelEndPoint.this.getWriteFlusher().getCallbackInvocationType())) {
                return fillT;
            }
            if (fillT == Invocable.InvocationType.EITHER && flushT == Invocable.InvocationType.NON_BLOCKING) {
                return Invocable.InvocationType.EITHER;
            }
            if (fillT == Invocable.InvocationType.NON_BLOCKING && flushT == Invocable.InvocationType.EITHER) {
                return Invocable.InvocationType.EITHER;
            }
            return Invocable.InvocationType.BLOCKING;
        }
    };

    public SelectableChannelEndPoint(Scheduler scheduler, SelectableChannel channel, ManagedSelector selector, SelectionKey selectionKey) {
        super(scheduler);
        this._channel = channel;
        this._selector = selector;
        this._key = selectionKey;
    }

    public SelectableChannel getChannel() {
        return this._channel;
    }

    @Override
    public Object getTransport() {
        return this.getChannel();
    }

    @Override
    public SocketAddress getLocalSocketAddress() {
        try {
            SelectableChannel channel = this.getChannel();
            if (channel instanceof NetworkChannel) {
                return ((NetworkChannel)((Object)channel)).getLocalAddress();
            }
            return super.getLocalSocketAddress();
        }
        catch (Throwable x) {
            LOG.trace("Could not retrieve local socket address", x);
            return null;
        }
    }

    @Override
    public boolean isOpen() {
        return this._channel.isOpen();
    }

    @Override
    public void doClose() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("doClose {}", (Object)this);
        }
        IO.close((Closeable)this._channel);
        super.doClose();
    }

    @Override
    public void onClose(Throwable cause) {
        try {
            super.onClose(cause);
        }
        finally {
            if (this._selector != null) {
                this._selector.destroyEndPoint(this, cause);
            }
        }
    }

    @Override
    protected void needsFillInterest() {
        this.changeInterests(1);
    }

    @Override
    protected void onIncompleteFlush() {
        this.changeInterests(4);
    }

    private void changeInterests(int operation) {
        int newInterestOps;
        int oldInterestOps;
        boolean pending;
        try (AutoLock l = this._lock.lock();){
            pending = this._updatePending;
            oldInterestOps = this._desiredInterestOps;
            newInterestOps = oldInterestOps | operation;
            if (newInterestOps != oldInterestOps) {
                this._desiredInterestOps = newInterestOps;
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("changeInterests p={} {}->{} for {}", new Object[]{pending, oldInterestOps, newInterestOps, this});
        }
        if (!pending && this._selector != null) {
            this._selector.submit(this._updateKeyAction);
        }
    }

    @Override
    public Runnable onSelected() {
        Runnable task;
        boolean flushable;
        int newInterestOps;
        int oldInterestOps;
        int readyOps = this._key.readyOps();
        try (AutoLock l = this._lock.lock();){
            this._updatePending = true;
            oldInterestOps = this._desiredInterestOps;
            this._desiredInterestOps = newInterestOps = oldInterestOps & ~readyOps;
        }
        boolean fillable = (readyOps & 1) != 0;
        boolean bl = flushable = (readyOps & 4) != 0;
        if (LOG.isDebugEnabled()) {
            LOG.debug("onSelected {}->{} r={} w={} for {}", new Object[]{oldInterestOps, newInterestOps, fillable, flushable, this});
        }
        Runnable runnable = fillable ? (flushable ? this._runCompleteWriteFillable : this._runFillable) : (task = flushable ? this._runCompleteWrite : null);
        if (LOG.isDebugEnabled()) {
            LOG.debug("task {}", (Object)task);
        }
        return task;
    }

    private void updateKeyAction(Selector selector) {
        this.updateKey();
    }

    @Override
    public void updateKey() {
        try {
            int newInterestOps;
            int oldInterestOps;
            try (AutoLock l = this._lock.lock();){
                this._updatePending = false;
                oldInterestOps = this._currentInterestOps;
                newInterestOps = this._desiredInterestOps;
                if (oldInterestOps != newInterestOps) {
                    this._currentInterestOps = newInterestOps;
                    this._key.interestOps(newInterestOps);
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Key interests updated {} -> {} on {}", new Object[]{oldInterestOps, newInterestOps, this});
            }
        }
        catch (CancelledKeyException x) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ignoring key update for cancelled key {}", (Object)this, (Object)x);
            }
            this.close();
        }
        catch (Throwable x) {
            LOG.warn("Ignoring key update for {}", (Object)this, (Object)x);
            this.close();
        }
    }

    @Override
    public void replaceKey(SelectionKey newKey) {
        this._key = newKey;
    }

    @Override
    public String toEndPointString() {
        return String.format("%s{io=%d/%d,kio=%d,kro=%d}", super.toEndPointString(), this._currentInterestOps, this._desiredInterestOps, ManagedSelector.safeInterestOps(this._key), ManagedSelector.safeReadyOps(this._key));
    }

    private abstract class RunnableCloseable
    implements Invocable.Task,
    Closeable {
        final String _operation;

        private RunnableCloseable(String operation) {
            this._operation = operation;
        }

        @Override
        public void close() {
            try {
                SelectableChannelEndPoint.this.close();
            }
            catch (Throwable x) {
                LOG.warn("Unable to close {}", (Object)SelectableChannelEndPoint.this, (Object)x);
            }
        }

        public String toString() {
            return String.format("%s:%s:%s", SelectableChannelEndPoint.this, this._operation, this.getInvocationType());
        }
    }
}

