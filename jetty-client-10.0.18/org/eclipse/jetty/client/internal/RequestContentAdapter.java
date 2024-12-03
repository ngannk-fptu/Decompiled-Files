/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.IO
 *  org.eclipse.jetty.util.thread.AutoLock
 *  org.eclipse.jetty.util.thread.Invocable$InvocationType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.internal;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.eclipse.jetty.client.AsyncContentProvider;
import org.eclipse.jetty.client.Synchronizable;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.Invocable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestContentAdapter
implements Request.Content,
Request.Content.Subscription,
AsyncContentProvider.Listener,
Callback {
    private static final Logger LOG = LoggerFactory.getLogger(RequestContentAdapter.class);
    private final AutoLock lock = new AutoLock();
    private final ContentProvider provider;
    private Iterator<ByteBuffer> iterator;
    private Request.Content.Consumer consumer;
    private boolean emitInitialContent;
    private boolean lastContent;
    private boolean committed;
    private int demand;
    private boolean stalled;
    private boolean hasContent;
    private Throwable failure;

    public RequestContentAdapter(ContentProvider provider) {
        this.provider = provider;
        if (provider instanceof AsyncContentProvider) {
            ((AsyncContentProvider)provider).setListener(this);
        }
    }

    public ContentProvider getContentProvider() {
        return this.provider;
    }

    @Override
    public String getContentType() {
        return this.provider instanceof ContentProvider.Typed ? ((ContentProvider.Typed)this.provider).getContentType() : null;
    }

    @Override
    public long getLength() {
        return this.provider.getLength();
    }

    @Override
    public boolean isReproducible() {
        return this.provider.isReproducible();
    }

    @Override
    public Request.Content.Subscription subscribe(Request.Content.Consumer consumer, boolean emitInitialContent) {
        try (AutoLock ignored = this.lock.lock();){
            if (this.consumer != null && !this.isReproducible()) {
                throw new IllegalStateException("Multiple subscriptions not supported on " + this);
            }
            this.iterator = this.provider.iterator();
            this.consumer = consumer;
            this.emitInitialContent = emitInitialContent;
            this.lastContent = false;
            this.committed = false;
            this.demand = 0;
            this.stalled = true;
            this.hasContent = false;
        }
        return this;
    }

    @Override
    public void demand() {
        boolean produce;
        try (AutoLock ignored = this.lock.lock();){
            ++this.demand;
            produce = this.stalled;
            if (this.stalled) {
                this.stalled = false;
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Content demand, producing {} for {}", (Object)produce, (Object)this);
        }
        if (produce) {
            this.produce();
        }
    }

    @Override
    public void fail(Throwable failure) {
        try (AutoLock ignored = this.lock.lock();){
            if (this.failure == null) {
                this.failure = failure;
            }
        }
        this.failed(failure);
    }

    @Override
    public void onContent() {
        boolean produce = false;
        try (AutoLock ignored = this.lock.lock();){
            this.hasContent = true;
            if (this.demand > 0) {
                produce = this.stalled;
                if (this.stalled) {
                    this.stalled = false;
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Content event, processing {} for {}", (Object)produce, (Object)this);
        }
        if (produce) {
            this.produce();
        }
    }

    public void succeeded() {
        if (this.iterator instanceof Callback) {
            ((Callback)this.iterator).succeeded();
        }
        if (this.lastContent && this.iterator instanceof Closeable) {
            IO.close((Closeable)((Closeable)((Object)this.iterator)));
        }
    }

    public void failed(Throwable x) {
        if (this.iterator == null) {
            this.failed(this.provider, x);
        } else {
            this.failed(this.iterator, x);
        }
    }

    private void failed(Object object, Throwable failure) {
        if (object instanceof Callback) {
            ((Callback)object).failed(failure);
        }
        if (object instanceof Closeable) {
            IO.close((Closeable)((Closeable)object));
        }
    }

    public Invocable.InvocationType getInvocationType() {
        return Invocable.InvocationType.NON_BLOCKING;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void produce() {
        while (true) {
            boolean noDemand;
            AutoLock ignored;
            block32: {
                ByteBuffer content;
                block34: {
                    Throwable failure;
                    try (AutoLock ignored2 = this.lock.lock();){
                        failure = this.failure;
                    }
                    if (failure != null) {
                        this.notifyFailure(failure);
                        return;
                    }
                    if (!this.committed) break block34;
                    content = this.advance();
                    if (content != null) {
                        this.notifyContent(content, this.lastContent);
                        break block32;
                    } else {
                        ignored = this.lock.lock();
                        try {
                            if (this.hasContent) {
                                this.hasContent = false;
                                continue;
                            }
                            this.stalled = true;
                        }
                        finally {
                            if (ignored == null) continue;
                            ignored.close();
                            continue;
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("No content, processing stalled for {}", (Object)this);
                        }
                        return;
                    }
                }
                this.committed = true;
                if (this.emitInitialContent) {
                    content = this.advance();
                    if (content != null) {
                        this.notifyContent(content, this.lastContent);
                    } else {
                        this.notifyContent(BufferUtil.EMPTY_BUFFER, false);
                    }
                } else {
                    this.notifyContent(BufferUtil.EMPTY_BUFFER, false);
                }
            }
            ignored = this.lock.lock();
            try {
                boolean bl = noDemand = this.demand == 0;
                if (noDemand) {
                    this.stalled = true;
                }
            }
            finally {
                if (ignored != null) {
                    ignored.close();
                }
            }
            if (noDemand) break;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("No demand, processing stalled for {}", (Object)this);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ByteBuffer advance() {
        if (this.iterator instanceof Synchronizable) {
            Object object = ((Synchronizable)((Object)this.iterator)).getLock();
            synchronized (object) {
                return this.next();
            }
        }
        return this.next();
    }

    private ByteBuffer next() {
        boolean hasNext = this.iterator.hasNext();
        ByteBuffer bytes = hasNext ? this.iterator.next() : null;
        boolean hasMore = hasNext && this.iterator.hasNext();
        this.lastContent = !hasMore;
        return hasNext ? bytes : BufferUtil.EMPTY_BUFFER;
    }

    private void notifyContent(ByteBuffer buffer, boolean last) {
        try (AutoLock ignored = this.lock.lock();){
            --this.demand;
            this.hasContent = false;
        }
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Notifying content last={} {} for {}", new Object[]{last, BufferUtil.toDetailString((ByteBuffer)buffer), this});
            }
            this.consumer.onContent(buffer, last, this);
        }
        catch (Throwable x) {
            this.fail(x);
        }
    }

    private void notifyFailure(Throwable failure) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Notifying failure for {}", (Object)this, (Object)failure);
            }
            this.consumer.onFailure(failure);
        }
        catch (Exception x) {
            LOG.trace("Failure while notifying content failure {}", (Object)failure, (Object)x);
        }
    }

    public String toString() {
        boolean stalled;
        int demand;
        try (AutoLock ignored = this.lock.lock();){
            demand = this.demand;
            stalled = this.stalled;
        }
        return String.format("%s@%x[demand=%d,stalled=%b]", this.getClass().getSimpleName(), this.hashCode(), demand, stalled);
    }
}

