/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.index.AutomatonTermsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.automaton.CompiledAutomaton;

public abstract class Terms {
    public static final Terms[] EMPTY_ARRAY = new Terms[0];

    protected Terms() {
    }

    public abstract TermsEnum iterator(TermsEnum var1) throws IOException;

    public TermsEnum intersect(CompiledAutomaton compiled, final BytesRef startTerm) throws IOException {
        if (compiled.type != CompiledAutomaton.AUTOMATON_TYPE.NORMAL) {
            throw new IllegalArgumentException("please use CompiledAutomaton.getTermsEnum instead");
        }
        if (startTerm == null) {
            return new AutomatonTermsEnum(this.iterator(null), compiled);
        }
        return new AutomatonTermsEnum(this.iterator(null), compiled){

            @Override
            protected BytesRef nextSeekTerm(BytesRef term) throws IOException {
                if (term == null) {
                    term = startTerm;
                }
                return super.nextSeekTerm(term);
            }
        };
    }

    public abstract Comparator<BytesRef> getComparator();

    public abstract long size() throws IOException;

    public abstract long getSumTotalTermFreq() throws IOException;

    public abstract long getSumDocFreq() throws IOException;

    public abstract int getDocCount() throws IOException;

    public abstract boolean hasOffsets();

    public abstract boolean hasPositions();

    public abstract boolean hasPayloads();
}

