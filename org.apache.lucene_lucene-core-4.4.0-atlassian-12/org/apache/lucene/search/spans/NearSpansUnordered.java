/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.spans.NearSpansOrdered;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.PriorityQueue;

public class NearSpansUnordered
extends Spans {
    private SpanNearQuery query;
    private List<SpansCell> ordered = new ArrayList<SpansCell>();
    private Spans[] subSpans;
    private int slop;
    private SpansCell first;
    private SpansCell last;
    private int totalLength;
    private CellQueue queue;
    private SpansCell max;
    private boolean more = true;
    private boolean firstTime = true;

    public NearSpansUnordered(SpanNearQuery query, AtomicReaderContext context, Bits acceptDocs, Map<Term, TermContext> termContexts) throws IOException {
        this.query = query;
        this.slop = query.getSlop();
        SpanQuery[] clauses = query.getClauses();
        this.queue = new CellQueue(clauses.length);
        this.subSpans = new Spans[clauses.length];
        for (int i = 0; i < clauses.length; ++i) {
            SpansCell cell = new SpansCell(clauses[i].getSpans(context, acceptDocs, termContexts), i);
            this.ordered.add(cell);
            this.subSpans[i] = cell.spans;
        }
    }

    public Spans[] getSubSpans() {
        return this.subSpans;
    }

    @Override
    public boolean next() throws IOException {
        if (this.firstTime) {
            this.initList(true);
            this.listToQueue();
            this.firstTime = false;
        } else if (this.more) {
            if (this.min().next()) {
                this.queue.updateTop();
            } else {
                this.more = false;
            }
        }
        while (this.more) {
            boolean queueStale = false;
            if (this.min().doc() != this.max.doc()) {
                this.queueToList();
                queueStale = true;
            }
            while (this.more && this.first.doc() < this.last.doc()) {
                this.more = this.first.skipTo(this.last.doc());
                this.firstToLast();
                queueStale = true;
            }
            if (!this.more) {
                return false;
            }
            if (queueStale) {
                this.listToQueue();
                queueStale = false;
            }
            if (this.atMatch()) {
                return true;
            }
            this.more = this.min().next();
            if (!this.more) continue;
            this.queue.updateTop();
        }
        return false;
    }

    @Override
    public boolean skipTo(int target) throws IOException {
        if (this.firstTime) {
            this.initList(false);
            SpansCell cell = this.first;
            while (this.more && cell != null) {
                this.more = cell.skipTo(target);
                cell = cell.next;
            }
            if (this.more) {
                this.listToQueue();
            }
            this.firstTime = false;
        } else {
            while (this.more && this.min().doc() < target) {
                if (this.min().skipTo(target)) {
                    this.queue.updateTop();
                    continue;
                }
                this.more = false;
            }
        }
        return this.more && (this.atMatch() || this.next());
    }

    private SpansCell min() {
        return (SpansCell)this.queue.top();
    }

    @Override
    public int doc() {
        return this.min().doc();
    }

    @Override
    public int start() {
        return this.min().start();
    }

    @Override
    public int end() {
        return this.max.end();
    }

    @Override
    public Collection<byte[]> getPayload() throws IOException {
        HashSet<byte[]> matchPayload = new HashSet<byte[]>();
        SpansCell cell = this.first;
        while (cell != null) {
            if (cell.isPayloadAvailable()) {
                matchPayload.addAll(cell.getPayload());
            }
            cell = cell.next;
        }
        return matchPayload;
    }

    @Override
    public boolean isPayloadAvailable() throws IOException {
        SpansCell pointer = this.min();
        while (pointer != null) {
            if (pointer.isPayloadAvailable()) {
                return true;
            }
            pointer = pointer.next;
        }
        return false;
    }

    @Override
    public long cost() {
        long minCost = Long.MAX_VALUE;
        for (int i = 0; i < this.subSpans.length; ++i) {
            minCost = Math.min(minCost, this.subSpans[i].cost());
        }
        return minCost;
    }

    public String toString() {
        return this.getClass().getName() + "(" + this.query.toString() + ")@" + (this.firstTime ? "START" : (this.more ? this.doc() + ":" + this.start() + "-" + this.end() : "END"));
    }

    private void initList(boolean next) throws IOException {
        for (int i = 0; this.more && i < this.ordered.size(); ++i) {
            SpansCell cell = this.ordered.get(i);
            if (next) {
                this.more = cell.next();
            }
            if (!this.more) continue;
            this.addToList(cell);
        }
    }

    private void addToList(SpansCell cell) {
        if (this.last != null) {
            this.last.next = cell;
        } else {
            this.first = cell;
        }
        this.last = cell;
        cell.next = null;
    }

    private void firstToLast() {
        this.last.next = this.first;
        this.last = this.first;
        this.first = this.first.next;
        this.last.next = null;
    }

    private void queueToList() {
        this.first = null;
        this.last = null;
        while (this.queue.top() != null) {
            this.addToList((SpansCell)this.queue.pop());
        }
    }

    private void listToQueue() {
        this.queue.clear();
        SpansCell cell = this.first;
        while (cell != null) {
            this.queue.add(cell);
            cell = cell.next;
        }
    }

    private boolean atMatch() {
        return this.min().doc() == this.max.doc() && this.max.end() - this.min().start() - this.totalLength <= this.slop;
    }

    private class SpansCell
    extends Spans {
        private Spans spans;
        private SpansCell next;
        private int length = -1;
        private int index;

        public SpansCell(Spans spans, int index) {
            this.spans = spans;
            this.index = index;
        }

        @Override
        public boolean next() throws IOException {
            return this.adjust(this.spans.next());
        }

        @Override
        public boolean skipTo(int target) throws IOException {
            return this.adjust(this.spans.skipTo(target));
        }

        private boolean adjust(boolean condition) {
            if (this.length != -1) {
                NearSpansUnordered.this.totalLength = NearSpansUnordered.this.totalLength - this.length;
            }
            if (condition) {
                this.length = this.end() - this.start();
                NearSpansUnordered.this.totalLength = NearSpansUnordered.this.totalLength + this.length;
                if (NearSpansUnordered.this.max == null || this.doc() > NearSpansUnordered.this.max.doc() || this.doc() == NearSpansUnordered.this.max.doc() && this.end() > NearSpansUnordered.this.max.end()) {
                    NearSpansUnordered.this.max = this;
                }
            }
            NearSpansUnordered.this.more = condition;
            return condition;
        }

        @Override
        public int doc() {
            return this.spans.doc();
        }

        @Override
        public int start() {
            return this.spans.start();
        }

        @Override
        public int end() {
            return this.spans.end();
        }

        @Override
        public Collection<byte[]> getPayload() throws IOException {
            return new ArrayList<byte[]>(this.spans.getPayload());
        }

        @Override
        public boolean isPayloadAvailable() throws IOException {
            return this.spans.isPayloadAvailable();
        }

        @Override
        public long cost() {
            return this.spans.cost();
        }

        public String toString() {
            return this.spans.toString() + "#" + this.index;
        }
    }

    private class CellQueue
    extends PriorityQueue<SpansCell> {
        public CellQueue(int size) {
            super(size);
        }

        @Override
        protected final boolean lessThan(SpansCell spans1, SpansCell spans2) {
            if (spans1.doc() == spans2.doc()) {
                return NearSpansOrdered.docSpansOrdered(spans1, spans2);
            }
            return spans1.doc() < spans2.doc();
        }
    }
}

