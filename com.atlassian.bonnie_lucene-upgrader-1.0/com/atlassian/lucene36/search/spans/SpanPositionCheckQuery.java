/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.spans;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.spans.SpanQuery;
import com.atlassian.lucene36.search.spans.Spans;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
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
    public Spans getSpans(IndexReader reader) throws IOException {
        return new PositionCheckSpan(reader);
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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected class PositionCheckSpan
    extends Spans {
        private Spans spans;

        public PositionCheckSpan(IndexReader reader) throws IOException {
            this.spans = SpanPositionCheckQuery.this.match.getSpans(reader);
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
        public boolean isPayloadAvailable() {
            return this.spans.isPayloadAvailable();
        }

        public String toString() {
            return "spans(" + SpanPositionCheckQuery.this.toString() + ")";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum AcceptStatus {
        YES,
        NO,
        NO_AND_ADVANCE;

    }
}

