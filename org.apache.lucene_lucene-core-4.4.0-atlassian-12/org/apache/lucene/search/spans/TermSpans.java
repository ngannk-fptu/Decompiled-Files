/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.BytesRef;

public class TermSpans
extends Spans {
    protected final DocsAndPositionsEnum postings;
    protected final Term term;
    protected int doc;
    protected int freq;
    protected int count;
    protected int position;
    protected boolean readPayload;
    public static final TermSpans EMPTY_TERM_SPANS = new EmptyTermSpans();

    public TermSpans(DocsAndPositionsEnum postings, Term term) {
        this.postings = postings;
        this.term = term;
        this.doc = -1;
    }

    TermSpans() {
        this.term = null;
        this.postings = null;
    }

    @Override
    public boolean next() throws IOException {
        if (this.count == this.freq) {
            if (this.postings == null) {
                return false;
            }
            this.doc = this.postings.nextDoc();
            if (this.doc == Integer.MAX_VALUE) {
                return false;
            }
            this.freq = this.postings.freq();
            this.count = 0;
        }
        this.position = this.postings.nextPosition();
        ++this.count;
        this.readPayload = false;
        return true;
    }

    @Override
    public boolean skipTo(int target) throws IOException {
        assert (target > this.doc);
        this.doc = this.postings.advance(target);
        if (this.doc == Integer.MAX_VALUE) {
            return false;
        }
        this.freq = this.postings.freq();
        this.count = 0;
        this.position = this.postings.nextPosition();
        ++this.count;
        this.readPayload = false;
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
    public long cost() {
        return this.postings.cost();
    }

    @Override
    public Collection<byte[]> getPayload() throws IOException {
        byte[] bytes;
        BytesRef payload = this.postings.getPayload();
        this.readPayload = true;
        if (payload != null) {
            bytes = new byte[payload.length];
            System.arraycopy(payload.bytes, payload.offset, bytes, 0, payload.length);
        } else {
            bytes = null;
        }
        return Collections.singletonList(bytes);
    }

    @Override
    public boolean isPayloadAvailable() throws IOException {
        return !this.readPayload && this.postings.getPayload() != null;
    }

    public String toString() {
        return "spans(" + this.term.toString() + ")@" + (this.doc == -1 ? "START" : (this.doc == Integer.MAX_VALUE ? "END" : this.doc + "-" + this.position));
    }

    public DocsAndPositionsEnum getPostings() {
        return this.postings;
    }

    private static final class EmptyTermSpans
    extends TermSpans {
        private EmptyTermSpans() {
        }

        @Override
        public boolean next() {
            return false;
        }

        @Override
        public boolean skipTo(int target) {
            return false;
        }

        @Override
        public int doc() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int start() {
            return -1;
        }

        @Override
        public int end() {
            return -1;
        }

        @Override
        public Collection<byte[]> getPayload() {
            return null;
        }

        @Override
        public boolean isPayloadAvailable() {
            return false;
        }

        @Override
        public long cost() {
            return 0L;
        }
    }
}

