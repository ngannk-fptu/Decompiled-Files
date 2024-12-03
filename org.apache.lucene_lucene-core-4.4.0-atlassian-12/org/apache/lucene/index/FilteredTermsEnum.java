/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

public abstract class FilteredTermsEnum
extends TermsEnum {
    private BytesRef initialSeekTerm = null;
    private boolean doSeek;
    private BytesRef actualTerm = null;
    private final TermsEnum tenum;

    protected abstract AcceptStatus accept(BytesRef var1) throws IOException;

    public FilteredTermsEnum(TermsEnum tenum) {
        this(tenum, true);
    }

    public FilteredTermsEnum(TermsEnum tenum, boolean startWithSeek) {
        assert (tenum != null);
        this.tenum = tenum;
        this.doSeek = startWithSeek;
    }

    protected final void setInitialSeekTerm(BytesRef term) {
        this.initialSeekTerm = term;
    }

    protected BytesRef nextSeekTerm(BytesRef currentTerm) throws IOException {
        BytesRef t = this.initialSeekTerm;
        this.initialSeekTerm = null;
        return t;
    }

    @Override
    public AttributeSource attributes() {
        return this.tenum.attributes();
    }

    @Override
    public BytesRef term() throws IOException {
        return this.tenum.term();
    }

    @Override
    public Comparator<BytesRef> getComparator() {
        return this.tenum.getComparator();
    }

    @Override
    public int docFreq() throws IOException {
        return this.tenum.docFreq();
    }

    @Override
    public long totalTermFreq() throws IOException {
        return this.tenum.totalTermFreq();
    }

    @Override
    public boolean seekExact(BytesRef term, boolean useCache) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support seeking");
    }

    @Override
    public TermsEnum.SeekStatus seekCeil(BytesRef term, boolean useCache) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support seeking");
    }

    @Override
    public void seekExact(long ord) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support seeking");
    }

    @Override
    public long ord() throws IOException {
        return this.tenum.ord();
    }

    @Override
    public DocsEnum docs(Bits bits, DocsEnum reuse, int flags) throws IOException {
        return this.tenum.docs(bits, reuse, flags);
    }

    @Override
    public DocsAndPositionsEnum docsAndPositions(Bits bits, DocsAndPositionsEnum reuse, int flags) throws IOException {
        return this.tenum.docsAndPositions(bits, reuse, flags);
    }

    @Override
    public void seekExact(BytesRef term, TermState state) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support seeking");
    }

    @Override
    public TermState termState() throws IOException {
        assert (this.tenum != null);
        return this.tenum.termState();
    }

    @Override
    public BytesRef next() throws IOException {
        while (true) {
            if (this.doSeek) {
                this.doSeek = false;
                BytesRef t = this.nextSeekTerm(this.actualTerm);
                assert (this.actualTerm == null || t == null || this.getComparator().compare(t, this.actualTerm) > 0) : "curTerm=" + this.actualTerm + " seekTerm=" + t;
                if (t == null || this.tenum.seekCeil(t, false) == TermsEnum.SeekStatus.END) {
                    return null;
                }
                this.actualTerm = this.tenum.term();
            } else {
                this.actualTerm = this.tenum.next();
                if (this.actualTerm == null) {
                    return null;
                }
            }
            switch (this.accept(this.actualTerm)) {
                case YES_AND_SEEK: {
                    this.doSeek = true;
                }
                case YES: {
                    return this.actualTerm;
                }
                case NO_AND_SEEK: {
                    this.doSeek = true;
                    break;
                }
                case END: {
                    return null;
                }
            }
        }
    }

    protected static enum AcceptStatus {
        YES,
        YES_AND_SEEK,
        NO,
        NO_AND_SEEK,
        END;

    }
}

