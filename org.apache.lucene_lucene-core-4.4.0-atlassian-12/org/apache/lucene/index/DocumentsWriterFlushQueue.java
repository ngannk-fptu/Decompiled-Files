/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.lucene.index.DocumentsWriter;
import org.apache.lucene.index.DocumentsWriterDeleteQueue;
import org.apache.lucene.index.DocumentsWriterPerThread;
import org.apache.lucene.index.FrozenBufferedDeletes;

class DocumentsWriterFlushQueue {
    private final Queue<FlushTicket> queue = new LinkedList<FlushTicket>();
    private final AtomicInteger ticketCount = new AtomicInteger();
    private final ReentrantLock purgeLock = new ReentrantLock();

    DocumentsWriterFlushQueue() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void addDeletesAndPurge(DocumentsWriter writer, DocumentsWriterDeleteQueue deleteQueue) throws IOException {
        DocumentsWriterFlushQueue documentsWriterFlushQueue = this;
        synchronized (documentsWriterFlushQueue) {
            this.incTickets();
            boolean success = false;
            try {
                this.queue.add(new GlobalDeletesTicket(deleteQueue.freezeGlobalBuffer(null)));
                success = true;
            }
            finally {
                if (!success) {
                    this.decTickets();
                }
            }
        }
        this.forcePurge(writer);
    }

    private void incTickets() {
        int numTickets = this.ticketCount.incrementAndGet();
        assert (numTickets > 0);
    }

    private void decTickets() {
        int numTickets = this.ticketCount.decrementAndGet();
        assert (numTickets >= 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized SegmentFlushTicket addFlushTicket(DocumentsWriterPerThread dwpt) {
        this.incTickets();
        boolean success = false;
        try {
            SegmentFlushTicket ticket = new SegmentFlushTicket(dwpt.prepareFlush());
            this.queue.add(ticket);
            success = true;
            SegmentFlushTicket segmentFlushTicket = ticket;
            return segmentFlushTicket;
        }
        finally {
            if (!success) {
                this.decTickets();
            }
        }
    }

    synchronized void addSegment(SegmentFlushTicket ticket, DocumentsWriterPerThread.FlushedSegment segment) {
        ticket.setSegment(segment);
    }

    synchronized void markTicketFailed(SegmentFlushTicket ticket) {
        ticket.setFailed();
    }

    boolean hasTickets() {
        assert (this.ticketCount.get() >= 0) : "ticketCount should be >= 0 but was: " + this.ticketCount.get();
        return this.ticketCount.get() != 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void innerPurge(DocumentsWriter writer) throws IOException {
        assert (this.purgeLock.isHeldByCurrentThread());
        while (true) {
            boolean canPublish;
            FlushTicket head;
            DocumentsWriterFlushQueue documentsWriterFlushQueue = this;
            synchronized (documentsWriterFlushQueue) {
                head = this.queue.peek();
                if (head == null) return;
                if (!head.canPublish()) return;
                boolean bl = true;
                canPublish = bl;
            }
            if (!canPublish) return;
            try {
                head.publish(writer);
                continue;
            }
            finally {
                documentsWriterFlushQueue = this;
                synchronized (documentsWriterFlushQueue) {
                    FlushTicket poll = this.queue.poll();
                    this.ticketCount.decrementAndGet();
                    assert (poll == head);
                }
                continue;
            }
            break;
        }
    }

    void forcePurge(DocumentsWriter writer) throws IOException {
        assert (!Thread.holdsLock(this));
        this.purgeLock.lock();
        try {
            this.innerPurge(writer);
        }
        finally {
            this.purgeLock.unlock();
        }
    }

    void tryPurge(DocumentsWriter writer) throws IOException {
        assert (!Thread.holdsLock(this));
        if (this.purgeLock.tryLock()) {
            try {
                this.innerPurge(writer);
            }
            finally {
                this.purgeLock.unlock();
            }
        }
    }

    public int getTicketCount() {
        return this.ticketCount.get();
    }

    synchronized void clear() {
        this.queue.clear();
        this.ticketCount.set(0);
    }

    static final class SegmentFlushTicket
    extends FlushTicket {
        private DocumentsWriterPerThread.FlushedSegment segment;
        private boolean failed = false;

        protected SegmentFlushTicket(FrozenBufferedDeletes frozenDeletes) {
            super(frozenDeletes);
        }

        @Override
        protected void publish(DocumentsWriter writer) throws IOException {
            assert (!this.published) : "ticket was already publised - can not publish twice";
            this.published = true;
            writer.finishFlush(this.segment, this.frozenDeletes);
        }

        protected void setSegment(DocumentsWriterPerThread.FlushedSegment segment) {
            assert (!this.failed);
            this.segment = segment;
        }

        protected void setFailed() {
            assert (this.segment == null);
            this.failed = true;
        }

        @Override
        protected boolean canPublish() {
            return this.segment != null || this.failed;
        }
    }

    static final class GlobalDeletesTicket
    extends FlushTicket {
        protected GlobalDeletesTicket(FrozenBufferedDeletes frozenDeletes) {
            super(frozenDeletes);
        }

        @Override
        protected void publish(DocumentsWriter writer) throws IOException {
            assert (!this.published) : "ticket was already publised - can not publish twice";
            this.published = true;
            writer.finishFlush(null, this.frozenDeletes);
        }

        @Override
        protected boolean canPublish() {
            return true;
        }
    }

    static abstract class FlushTicket {
        protected FrozenBufferedDeletes frozenDeletes;
        protected boolean published = false;

        protected FlushTicket(FrozenBufferedDeletes frozenDeletes) {
            assert (frozenDeletes != null);
            this.frozenDeletes = frozenDeletes;
        }

        protected abstract void publish(DocumentsWriter var1) throws IOException;

        protected abstract boolean canPublish();
    }
}

