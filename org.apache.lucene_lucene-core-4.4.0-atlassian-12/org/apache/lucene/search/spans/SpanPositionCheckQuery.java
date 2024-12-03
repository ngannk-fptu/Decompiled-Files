/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.Bits;

public abstract class SpanPositionCheckQuery
extends SpanQuery
implements Cloneable {
    protected SpanQuery match;

    public SpanPositionCheckQuery(SpanQuery match) {
        this.match = match;
    }

    public SpanQuery getMatch() {
        return this.match;
    }

    @Override
    public String getField() {
        return this.match.getField();
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        this.match.extractTerms(terms);
    }

    protected abstract AcceptStatus acceptPosition(Spans var1) throws IOException;

    @Override
    public Spans getSpans(AtomicReaderContext context, Bits acceptDocs, Map<Term, TermContext> termContexts) throws IOException {
        return new PositionCheckSpan(context, acceptDocs, termContexts);
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        SpanPositionCheckQuery clone = null;
        SpanQuery rewritten = (SpanQuery)this.match.rewrite(reader);
        if (rewritten != this.match) {
            clone = (SpanPositionCheckQuery)this.clone();
            clone.match = rewritten;
        }
        if (clone != null) {
            return clone;
        }
        return this;
    }

    protected class PositionCheckSpan
    extends Spans {
        private Spans spans;

        public PositionCheckSpan(AtomicReaderContext context, Bits acceptDocs, Map<Term, TermContext> termContexts) throws IOException {
            this.spans = SpanPositionCheckQuery.this.match.getSpans(context, acceptDocs, termContexts);
        }

        @Override
        public boolean next() throws IOException {
            if (!this.spans.next()) {
                return false;
            }
            return this.doNext();
        }

        @Override
        public boolean skipTo(int target) throws IOException {
            if (!this.spans.skipTo(target)) {
                return false;
            }
            return this.doNext();
        }

        protected boolean doNext() throws IOException {
            while (true) {
                switch (SpanPositionCheckQuery.this.acceptPosition(this)) {
                    case YES: {
                        return true;
                    }
                    case NO: {
                        if (this.spans.next()) break;
                        return false;
                    }
                    case NO_AND_ADVANCE: {
                        if (this.spans.skipTo(this.spans.doc() + 1)) break;
                        return false;
                    }
                }
            }
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
            ArrayList<byte[]> result = null;
            if (this.spans.isPayloadAvailable()) {
                result = new ArrayList<byte[]>(this.spans.getPayload());
            }
            return result;
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
            return "spans(" + SpanPositionCheckQuery.this.toString() + ")";
        }
    }

    protected static enum AcceptStatus {
        YES,
        NO,
        NO_AND_ADVANCE;

    }
}

