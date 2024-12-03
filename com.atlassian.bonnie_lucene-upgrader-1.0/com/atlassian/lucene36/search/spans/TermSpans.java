/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.spans;

import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermPositions;
import com.atlassian.lucene36.search.spans.Spans;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TermSpans
extends Spans {
    protected TermPositions positions;
    protected Term term;
    protected int doc;
    protected int freq;
    protected int count;
    protected int position;

    public TermSpans(TermPositions positions, Term term) throws IOException {
        this.positions = positions;
        this.term = term;
        this.doc = -1;
    }

    @Override
    public boolean next() throws IOException {
        if (this.count == this.freq) {
            if (!this.positions.next()) {
                this.doc = Integer.MAX_VALUE;
                return false;
            }
            this.doc = this.positions.doc();
            this.freq = this.positions.freq();
            this.count = 0;
        }
        this.position = this.positions.nextPosition();
        ++this.count;
        return true;
    }

    @Override
    public boolean skipTo(int target) throws IOException {
        if (!this.positions.skipTo(target)) {
            this.doc = Integer.MAX_VALUE;
            return false;
        }
        this.doc = this.positions.doc();
        this.freq = this.positions.freq();
        this.count = 0;
        this.position = this.positions.nextPosition();
        ++this.count;
        return true;
    }

    @Override
    public int doc() {
        return this.doc;
    }

    @Override
    public int start() {
        return this.position;
    }

    @Override
    public int end() {
        return this.position + 1;
    }

    @Override
    public Collection<byte[]> getPayload() throws IOException {
        byte[] bytes = new byte[this.positions.getPayloadLength()];
        bytes = this.positions.getPayload(bytes, 0);
        return Collections.singletonList(bytes);
    }

    @Override
    public boolean isPayloadAvailable() {
        return this.positions.isPayloadAvailable();
    }

    public String toString() {
        return "spans(" + this.term.toString() + ")@" + (this.doc == -1 ? "START" : (this.doc == Integer.MAX_VALUE ? "END" : this.doc + "-" + this.position));
    }

    public TermPositions getPositions() {
        return this.positions;
    }
}

