/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.lucene.index.BufferedDeletes;
import org.apache.lucene.index.FrozenBufferedDeletes;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;

final class DocumentsWriterDeleteQueue {
    private volatile Node<?> tail;
    private static final AtomicReferenceFieldUpdater<DocumentsWriterDeleteQueue, Node> tailUpdater = AtomicReferenceFieldUpdater.newUpdater(DocumentsWriterDeleteQueue.class, Node.class, "tail");
    private final DeleteSlice globalSlice;
    private final BufferedDeletes globalBufferedDeletes;
    private final ReentrantLock globalBufferLock = new ReentrantLock();
    final long generation;

    DocumentsWriterDeleteQueue() {
        this(0L);
    }

    DocumentsWriterDeleteQueue(long generation) {
        this(new BufferedDeletes(), generation);
    }

    DocumentsWriterDeleteQueue(BufferedDeletes globalBufferedDeletes, long generation) {
        this.globalBufferedDeletes = globalBufferedDeletes;
        this.generation = generation;
        this.tail = new Node<Object>(null);
        this.globalSlice = new DeleteSlice(this.tail);
    }

    void addDelete(Query ... queries) {
        this.add(new QueryArrayNode(queries));
        this.tryApplyGlobalSlice();
    }

    void addDelete(Term ... terms) {
        this.add(new TermArrayNode(terms));
        this.tryApplyGlobalSlice();
    }

    void add(Term term, DeleteSlice slice) {
        TermNode termNode = new TermNode(term);
        this.add(termNode);
        slice.sliceTail = termNode;
        assert (slice.sliceHead != slice.sliceTail) : "slice head and tail must differ after add";
        this.tryApplyGlobalSlice();
    }

    void add(Node<?> item) {
        Node<?> currentTail;
        while (true) {
            currentTail = this.tail;
            Node<?> tailNext = currentTail.next;
            if (this.tail != currentTail) continue;
            if (tailNext != null) {
                tailUpdater.compareAndSet(this, currentTail, tailNext);
                continue;
            }
            if (currentTail.casNext(null, item)) break;
        }
        tailUpdater.compareAndSet(this, currentTail, item);
    }

    boolean anyChanges() {
        this.globalBufferLock.lock();
        try {
            boolean bl = this.globalBufferedDeletes.any() || !this.globalSlice.isEmpty() || this.globalSlice.sliceTail != this.tail || this.tail.next != null;
            return bl;
        }
        finally {
            this.globalBufferLock.unlock();
        }
    }

    void tryApplyGlobalSlice() {
        if (this.globalBufferLock.tryLock()) {
            try {
                if (this.updateSlice(this.globalSlice)) {
                    this.globalSlice.apply(this.globalBufferedDeletes, BufferedDeletes.MAX_INT);
                }
            }
            finally {
                this.globalBufferLock.unlock();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    FrozenBufferedDeletes freezeGlobalBuffer(DeleteSlice callerSlice) {
        this.globalBufferLock.lock();
        Node<?> currentTail = this.tail;
        if (callerSlice != null) {
            callerSlice.sliceTail = currentTail;
        }
        try {
            if (this.globalSlice.sliceTail != currentTail) {
                this.globalSlice.sliceTail = currentTail;
                this.globalSlice.apply(this.globalBufferedDeletes, BufferedDeletes.MAX_INT);
            }
            FrozenBufferedDeletes packet = new FrozenBufferedDeletes(this.globalBufferedDeletes, false);
            this.globalBufferedDeletes.clear();
            FrozenBufferedDeletes frozenBufferedDeletes = packet;
            return frozenBufferedDeletes;
        }
        finally {
            this.globalBufferLock.unlock();
        }
    }

    DeleteSlice newSlice() {
        return new DeleteSlice(this.tail);
    }

    boolean updateSlice(DeleteSlice slice) {
        if (slice.sliceTail != this.tail) {
            slice.sliceTail = this.tail;
            return true;
        }
        return false;
    }

    public int numGlobalTermDeletes() {
        return this.globalBufferedDeletes.numTermDeletes.get();
    }

    void clear() {
        this.globalBufferLock.lock();
        try {
            Node<?> currentTail = this.tail;
            this.globalSlice.sliceTail = currentTail;
            this.globalSlice.sliceHead = this.globalSlice.sliceTail;
            this.globalBufferedDeletes.clear();
        }
        finally {
            this.globalBufferLock.unlock();
        }
    }

    private boolean forceApplyGlobalSlice() {
        this.globalBufferLock.lock();
        Node<?> currentTail = this.tail;
        try {
            if (this.globalSlice.sliceTail != currentTail) {
                this.globalSlice.sliceTail = currentTail;
                this.globalSlice.apply(this.globalBufferedDeletes, BufferedDeletes.MAX_INT);
            }
            boolean bl = this.globalBufferedDeletes.any();
            return bl;
        }
        finally {
            this.globalBufferLock.unlock();
        }
    }

    public int getBufferedDeleteTermsSize() {
        this.globalBufferLock.lock();
        try {
            this.forceApplyGlobalSlice();
            int n = this.globalBufferedDeletes.terms.size();
            return n;
        }
        finally {
            this.globalBufferLock.unlock();
        }
    }

    public long bytesUsed() {
        return this.globalBufferedDeletes.bytesUsed.get();
    }

    public String toString() {
        return "DWDQ: [ generation: " + this.generation + " ]";
    }

    private static final class TermArrayNode
    extends Node<Term[]> {
        TermArrayNode(Term[] term) {
            super(term);
        }

        @Override
        void apply(BufferedDeletes bufferedDeletes, int docIDUpto) {
            for (Term term : (Term[])this.item) {
                bufferedDeletes.addTerm(term, docIDUpto);
            }
        }

        public String toString() {
            return "dels=" + Arrays.toString((Object[])this.item);
        }
    }

    private static final class QueryArrayNode
    extends Node<Query[]> {
        QueryArrayNode(Query[] query) {
            super(query);
        }

        @Override
        void apply(BufferedDeletes bufferedDeletes, int docIDUpto) {
            for (Query query : (Query[])this.item) {
                bufferedDeletes.addQuery(query, docIDUpto);
            }
        }
    }

    private static final class TermNode
    extends Node<Term> {
        TermNode(Term term) {
            super(term);
        }

        @Override
        void apply(BufferedDeletes bufferedDeletes, int docIDUpto) {
            bufferedDeletes.addTerm((Term)this.item, docIDUpto);
        }

        public String toString() {
            return "del=" + this.item;
        }
    }

    private static class Node<T> {
        volatile Node<?> next;
        final T item;
        static final AtomicReferenceFieldUpdater<Node, Node> nextUpdater = AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "next");

        Node(T item) {
            this.item = item;
        }

        void apply(BufferedDeletes bufferedDeletes, int docIDUpto) {
            throw new IllegalStateException("sentinel item must never be applied");
        }

        boolean casNext(Node<?> cmp, Node<?> val) {
            return nextUpdater.compareAndSet(this, cmp, val);
        }
    }

    static class DeleteSlice {
        Node<?> sliceHead;
        Node<?> sliceTail;

        DeleteSlice(Node<?> currentTail) {
            assert (currentTail != null);
            this.sliceTail = currentTail;
            this.sliceHead = this.sliceTail;
        }

        void apply(BufferedDeletes del, int docIDUpto) {
            if (this.sliceHead == this.sliceTail) {
                return;
            }
            Node<?> current = this.sliceHead;
            do {
                current = current.next;
                assert (current != null) : "slice property violated between the head on the tail must not be a null node";
                current.apply(del, docIDUpto);
            } while (current != this.sliceTail);
            this.reset();
        }

        void reset() {
            this.sliceHead = this.sliceTail;
        }

        boolean isTailItem(Object item) {
            return this.sliceTail.item == item;
        }

        boolean isEmpty() {
            return this.sliceHead == this.sliceTail;
        }
    }
}

