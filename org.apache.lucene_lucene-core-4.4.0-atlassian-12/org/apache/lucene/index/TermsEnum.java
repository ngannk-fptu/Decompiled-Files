/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.TermState;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefIterator;

public abstract class TermsEnum
implements BytesRefIterator {
    private AttributeSource atts = null;
    public static final TermsEnum EMPTY = new TermsEnum(){

        @Override
        public SeekStatus seekCeil(BytesRef term, boolean useCache) {
            return SeekStatus.END;
        }

        @Override
        public void seekExact(long ord) {
        }

        @Override
        public BytesRef term() {
            throw new IllegalStateException("this method should never be called");
        }

        @Override
        public Comparator<BytesRef> getComparator() {
            return null;
        }

        @Override
        public int docFreq() {
            throw new IllegalStateException("this method should never be called");
        }

        @Override
        public long totalTermFreq() {
            throw new IllegalStateException("this method should never be called");
        }

        @Override
        public long ord() {
            throw new IllegalStateException("this method should never be called");
        }

        @Override
        public DocsEnum docs(Bits liveDocs, DocsEnum reuse, int flags) {
            throw new IllegalStateException("this method should never be called");
        }

        @Override
        public DocsAndPositionsEnum docsAndPositions(Bits liveDocs, DocsAndPositionsEnum reuse, int flags) {
            throw new IllegalStateException("this method should never be called");
        }

        @Override
        public BytesRef next() {
            return null;
        }

        @Override
        public synchronized AttributeSource attributes() {
            return super.attributes();
        }

        @Override
        public TermState termState() {
            throw new IllegalStateException("this method should never be called");
        }

        @Override
        public void seekExact(BytesRef term, TermState state) {
            throw new IllegalStateException("this method should never be called");
        }
    };

    protected TermsEnum() {
    }

    public AttributeSource attributes() {
        if (this.atts == null) {
            this.atts = new AttributeSource();
        }
        return this.atts;
    }

    public boolean seekExact(BytesRef text, boolean useCache) throws IOException {
        return this.seekCeil(text, useCache) == SeekStatus.FOUND;
    }

    public abstract SeekStatus seekCeil(BytesRef var1, boolean var2) throws IOException;

    public final SeekStatus seekCeil(BytesRef text) throws IOException {
        return this.seekCeil(text, true);
    }

    public abstract void seekExact(long var1) throws IOException;

    public void seekExact(BytesRef term, TermState state) throws IOException {
        if (!this.seekExact(term, true)) {
            throw new IllegalArgumentException("term=" + term + " does not exist");
        }
    }

    public abstract BytesRef term() throws IOException;

    public abstract long ord() throws IOException;

    public abstract int docFreq() throws IOException;

    public abstract long totalTermFreq() throws IOException;

    public final DocsEnum docs(Bits liveDocs, DocsEnum reuse) throws IOException {
        return this.docs(liveDocs, reuse, 1);
    }

    public abstract DocsEnum docs(Bits var1, DocsEnum var2, int var3) throws IOException;

    public final DocsAndPositionsEnum docsAndPositions(Bits liveDocs, DocsAndPositionsEnum reuse) throws IOException {
        return this.docsAndPositions(liveDocs, reuse, 3);
    }

    public abstract DocsAndPositionsEnum docsAndPositions(Bits var1, DocsAndPositionsEnum var2, int var3) throws IOException;

    public TermState termState() throws IOException {
        return new TermState(){

            @Override
            public void copyFrom(TermState other) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static enum SeekStatus {
        END,
        FOUND,
        NOT_FOUND;

    }
}

