/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.spans;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.spans.SpanNearQuery;
import com.atlassian.lucene36.search.spans.SpanQuery;
import com.atlassian.lucene36.search.spans.Spans;
import com.atlassian.lucene36.util.ArrayUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class NearSpansOrdered
extends Spans {
    private final int allowedSlop;
    private boolean firstTime = true;
    private boolean more = false;
    private final Spans[] subSpans;
    private boolean inSameDoc = false;
    private int matchDoc = -1;
    private int matchStart = -1;
    private int matchEnd = -1;
    private List<byte[]> matchPayload;
    private final Spans[] subSpansByDoc;
    private final Comparator<Spans> spanDocComparator = new Comparator<Spans>(){

        @Override
        public int compare(Spans o1, Spans o2) {
            return o1.doc() - o2.doc();
        }
    };
    private SpanNearQuery query;
    private boolean collectPayloads = true;

    public NearSpansOrdered(SpanNearQuery spanNearQuery, IndexReader reader) throws IOException {
        this(spanNearQuery, reader, true);
    }

    public NearSpansOrdered(SpanNearQuery spanNearQuery, IndexReader reader, boolean collectPayloads) throws IOException {
        if (spanNearQuery.getClauses().length < 2) {
            throw new IllegalArgumentException("Less than 2 clauses: " + spanNearQuery);
        }
        this.collectPayloads = collectPayloads;
        this.allowedSlop = spanNearQuery.getSlop();
        SpanQuery[] clauses = spanNearQuery.getClauses();
        this.subSpans = new Spans[clauses.length];
        this.matchPayload = new LinkedList<byte[]>();
        this.subSpansByDoc = new Spans[clauses.length];
        for (int i = 0; i < clauses.length; ++i) {
            this.subSpans[i] = clauses[i].getSpans(reader);
            this.subSpansByDoc[i] = this.subSpans[i];
        }
        this.query = spanNearQuery;
    }

    @Override
    public int doc() {
        return this.matchDoc;
    }

    @Override
    public int start() {
        return this.matchStart;
    }

    @Override
    public int end() {
        return this.matchEnd;
    }

    public Spans[] getSubSpans() {
        return this.subSpans;
    }

    @Override
    public Collection<byte[]> getPayload() throws IOException {
        return this.matchPayload;
    }

    @Override
    public boolean isPayloadAvailable() {
        return !this.matchPayload.isEmpty();
    }

    @Override
    public boolean next() throws IOException {
        if (this.firstTime) {
            this.firstTime = false;
            for (int i = 0; i < this.subSpans.length; ++i) {
                if (this.subSpans[i].next()) continue;
                this.more = false;
                return false;
            }
            this.more = true;
        }
        if (this.collectPayloads) {
            this.matchPayload.clear();
        }
        return this.advanceAfterOrdered();
    }

    @Override
    public boolean skipTo(int target) throws IOException {
        if (this.firstTime) {
            this.firstTime = false;
            for (int i = 0; i < this.subSpans.length; ++i) {
                if (this.subSpans[i].skipTo(target)) continue;
                this.more = false;
                return false;
            }
            this.more = true;
        } else if (this.more && this.subSpans[0].doc() < target) {
            if (this.subSpans[0].skipTo(target)) {
                this.inSameDoc = false;
            } else {
                this.more = false;
                return false;
            }
        }
        if (this.collectPayloads) {
            this.matchPayload.clear();
        }
        return this.advanceAfterOrdered();
    }

    private boolean advanceAfterOrdered() throws IOException {
        while (this.more && (this.inSameDoc || this.toSameDoc())) {
            if (!this.stretchToOrder() || !this.shrinkToAfterShortestMatch()) continue;
            return true;
        }
        return false;
    }

    private boolean toSameDoc() throws IOException {
        ArrayUtil.mergeSort(this.subSpansByDoc, this.spanDocComparator);
        int firstIndex = 0;
        int maxDoc = this.subSpansByDoc[this.subSpansByDoc.length - 1].doc();
        while (this.subSpansByDoc[firstIndex].doc() != maxDoc) {
            if (!this.subSpansByDoc[firstIndex].skipTo(maxDoc)) {
                this.more = false;
                this.inSameDoc = false;
                return false;
            }
            maxDoc = this.subSpansByDoc[firstIndex].doc();
            if (++firstIndex != this.subSpansByDoc.length) continue;
            firstIndex = 0;
        }
        for (int i = 0; i < this.subSpansByDoc.length; ++i) {
            assert (this.subSpansByDoc[i].doc() == maxDoc) : " NearSpansOrdered.toSameDoc() spans " + this.subSpansByDoc[0] + "\n at doc " + this.subSpansByDoc[i].doc() + ", but should be at " + maxDoc;
        }
        this.inSameDoc = true;
        return true;
    }

    static final boolean docSpansOrdered(Spans spans1, Spans spans2) {
        int start2;
        assert (spans1.doc() == spans2.doc()) : "doc1 " + spans1.doc() + " != doc2 " + spans2.doc();
        int start1 = spans1.start();
        return start1 == (start2 = spans2.start()) ? spans1.end() < spans2.end() : start1 < start2;
    }

    private static final boolean docSpansOrdered(int start1, int end1, int start2, int end2) {
        return start1 == start2 ? end1 < end2 : start1 < start2;
    }

    private boolean stretchToOrder() throws IOException {
        this.matchDoc = this.subSpans[0].doc();
        block0: for (int i = 1; this.inSameDoc && i < this.subSpans.length; ++i) {
            while (!NearSpansOrdered.docSpansOrdered(this.subSpans[i - 1], this.subSpans[i])) {
                if (!this.subSpans[i].next()) {
                    this.inSameDoc = false;
                    this.more = false;
                    continue block0;
                }
                if (this.matchDoc == this.subSpans[i].doc()) continue;
                this.inSameDoc = false;
                continue block0;
            }
        }
        return this.inSameDoc;
    }

    private boolean shrinkToAfterShortestMatch() throws IOException {
        boolean match;
        this.matchStart = this.subSpans[this.subSpans.length - 1].start();
        this.matchEnd = this.subSpans[this.subSpans.length - 1].end();
        HashSet<Object> possibleMatchPayloads = new HashSet<Object>();
        if (this.subSpans[this.subSpans.length - 1].isPayloadAvailable()) {
            possibleMatchPayloads.addAll(this.subSpans[this.subSpans.length - 1].getPayload());
        }
        ArrayList<byte[]> possiblePayload = null;
        int matchSlop = 0;
        int lastStart = this.matchStart;
        int lastEnd = this.matchEnd;
        for (int i = this.subSpans.length - 2; i >= 0; --i) {
            Spans prevSpans = this.subSpans[i];
            if (this.collectPayloads && prevSpans.isPayloadAvailable()) {
                Collection<byte[]> payload = prevSpans.getPayload();
                possiblePayload = new ArrayList<byte[]>(payload.size());
                possiblePayload.addAll(payload);
            }
            int prevStart = prevSpans.start();
            int prevEnd = prevSpans.end();
            while (true) {
                int ppEnd;
                if (!prevSpans.next()) {
                    this.inSameDoc = false;
                    this.more = false;
                    break;
                }
                if (this.matchDoc != prevSpans.doc()) {
                    this.inSameDoc = false;
                    break;
                }
                int ppStart = prevSpans.start();
                if (!NearSpansOrdered.docSpansOrdered(ppStart, ppEnd = prevSpans.end(), lastStart, lastEnd)) break;
                prevStart = ppStart;
                prevEnd = ppEnd;
                if (!this.collectPayloads || !prevSpans.isPayloadAvailable()) continue;
                Collection<byte[]> payload = prevSpans.getPayload();
                possiblePayload = new ArrayList(payload.size());
                possiblePayload.addAll(payload);
            }
            if (this.collectPayloads && possiblePayload != null) {
                possibleMatchPayloads.addAll(possiblePayload);
            }
            assert (prevStart <= this.matchStart);
            if (this.matchStart > prevEnd) {
                matchSlop += this.matchStart - prevEnd;
            }
            this.matchStart = prevStart;
            lastStart = prevStart;
            lastEnd = prevEnd;
        }
        boolean bl = match = matchSlop <= this.allowedSlop;
        if (this.collectPayloads && match && possibleMatchPayloads.size() > 0) {
            this.matchPayload.addAll(possibleMatchPayloads);
        }
        return match;
    }

    public String toString() {
        return this.getClass().getName() + "(" + this.query.toString() + ")@" + (this.firstTime ? "START" : (this.more ? this.doc() + ":" + this.start() + "-" + this.end() : "END"));
    }
}

