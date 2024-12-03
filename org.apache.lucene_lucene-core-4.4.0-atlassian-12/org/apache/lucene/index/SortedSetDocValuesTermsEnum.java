/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.OrdTermState;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

class SortedSetDocValuesTermsEnum
extends TermsEnum {
    private final SortedSetDocValues values;
    private long currentOrd = -1L;
    private final BytesRef term = new BytesRef();

    public SortedSetDocValuesTermsEnum(SortedSetDocValues values) {
        this.values = values;
    }

    @Override
    public TermsEnum.SeekStatus seekCeil(BytesRef text, boolean useCache) throws IOException {
        long ord = this.values.lookupTerm(text);
        if (ord >= 0L) {
            this.currentOrd = ord;
            this.term.offset = 0;
            this.term.bytes = new byte[text.length];
            this.term.copyBytes(text);
            return TermsEnum.SeekStatus.FOUND;
        }
        this.currentOrd = -ord - 1L;
        if (this.currentOrd == this.values.getValueCount()) {
            return TermsEnum.SeekStatus.END;
        }
        this.values.lookupOrd(this.currentOrd, this.term);
        return TermsEnum.SeekStatus.NOT_FOUND;
    }

    @Override
    public boolean seekExact(BytesRef text, boolean useCache) throws IOException {
        long ord = this.values.lookupTerm(text);
        if (ord >= 0L) {
            this.term.offset = 0;
            this.term.bytes = new byte[text.length];
            this.term.copyBytes(text);
            this.currentOrd = ord;
            return true;
        }
        return false;
    }

    @Override
    public void seekExact(long ord) throws IOException {
        assert (ord >= 0L && ord < this.values.getValueCount());
        this.currentOrd = (int)ord;
        this.values.lookupOrd(this.currentOrd, this.term);
    }

    @Override
    public BytesRef next() throws IOException {
        ++this.currentOrd;
        if (this.currentOrd >= this.values.getValueCount()) {
            return null;
        }
        this.values.lookupOrd(this.currentOrd, this.term);
        return this.term;
    }

    @Override
    public BytesRef term() throws IOException {
        return this.term;
    }

    @Override
    public long ord() throws IOException {
        return this.currentOrd;
    }

    @Override
    public int docFreq() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long totalTermFreq() {
        return -1L;
    }

    @Override
    public DocsEnum docs(Bits liveDocs, DocsEnum reuse, int flags) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DocsAndPositionsEnum docsAndPositions(Bits liveDocs, DocsAndPositionsEnum reuse, int flags) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comparator<BytesRef> getComparator() {
        return BytesRef.getUTF8SortedAsUnicodeComparator();
    }

    @Override
    public void seekExact(BytesRef term, TermState state) throws IOException {
        assert (state != null && state instanceof OrdTermState);
        this.seekExact(((OrdTermState)state).ord);
    }

    @Override
    public TermState termState() throws IOException {
        OrdTermState state = new OrdTermState();
        state.ord = this.currentOrd;
        return state;
    }
}

