/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import org.apache.lucene.index.OrdTermState;
import org.apache.lucene.index.TermState;

public class BlockTermState
extends OrdTermState {
    public int docFreq;
    public long totalTermFreq;
    public int termBlockOrd;
    public long blockFilePointer;

    protected BlockTermState() {
    }

    @Override
    public void copyFrom(TermState _other) {
        assert (_other instanceof BlockTermState) : "can not copy from " + _other.getClass().getName();
        BlockTermState other = (BlockTermState)_other;
        super.copyFrom(_other);
        this.docFreq = other.docFreq;
        this.totalTermFreq = other.totalTermFreq;
        this.termBlockOrd = other.termBlockOrd;
        this.blockFilePointer = other.blockFilePointer;
    }

    @Override
    public String toString() {
        return "docFreq=" + this.docFreq + " totalTermFreq=" + this.totalTermFreq + " termBlockOrd=" + this.termBlockOrd + " blockFP=" + this.blockFilePointer;
    }
}

