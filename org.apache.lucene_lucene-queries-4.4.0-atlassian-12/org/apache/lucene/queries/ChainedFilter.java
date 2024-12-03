/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.BitsFilteredDocIdSet
 *  org.apache.lucene.search.DocIdSet
 *  org.apache.lucene.search.DocIdSetIterator
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.OpenBitSet
 *  org.apache.lucene.util.OpenBitSetDISI
 */
package org.apache.lucene.queries;

import java.io.IOException;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.BitsFilteredDocIdSet;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.OpenBitSet;
import org.apache.lucene.util.OpenBitSetDISI;

public class ChainedFilter
extends Filter {
    public static final int OR = 0;
    public static final int AND = 1;
    public static final int ANDNOT = 2;
    public static final int XOR = 3;
    public static final int DEFAULT = 0;
    private Filter[] chain = null;
    private int[] logicArray;
    private int logic = -1;

    public ChainedFilter(Filter[] chain) {
        this.chain = chain;
    }

    public ChainedFilter(Filter[] chain, int[] logicArray) {
        this.chain = chain;
        this.logicArray = logicArray;
    }

    public ChainedFilter(Filter[] chain, int logic) {
        this.chain = chain;
        this.logic = logic;
    }

    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        int[] index = new int[]{0};
        if (this.logic != -1) {
            return BitsFilteredDocIdSet.wrap((DocIdSet)this.getDocIdSet(context, this.logic, index), (Bits)acceptDocs);
        }
        if (this.logicArray != null) {
            return BitsFilteredDocIdSet.wrap((DocIdSet)this.getDocIdSet(context, this.logicArray, index), (Bits)acceptDocs);
        }
        return BitsFilteredDocIdSet.wrap((DocIdSet)this.getDocIdSet(context, 0, index), (Bits)acceptDocs);
    }

    private DocIdSetIterator getDISI(Filter filter, AtomicReaderContext context) throws IOException {
        DocIdSet docIdSet = filter.getDocIdSet(context, null);
        if (docIdSet == null) {
            return DocIdSetIterator.empty();
        }
        DocIdSetIterator iter = docIdSet.iterator();
        if (iter == null) {
            return DocIdSetIterator.empty();
        }
        return iter;
    }

    private OpenBitSetDISI initialResult(AtomicReaderContext context, int logic, int[] index) throws IOException {
        OpenBitSetDISI result;
        AtomicReader reader = context.reader();
        if (logic == 1) {
            result = new OpenBitSetDISI(this.getDISI(this.chain[index[0]], context), reader.maxDoc());
            index[0] = index[0] + 1;
        } else if (logic == 2) {
            result = new OpenBitSetDISI(this.getDISI(this.chain[index[0]], context), reader.maxDoc());
            result.flip(0L, (long)reader.maxDoc());
            index[0] = index[0] + 1;
        } else {
            result = new OpenBitSetDISI(reader.maxDoc());
        }
        return result;
    }

    private DocIdSet getDocIdSet(AtomicReaderContext context, int logic, int[] index) throws IOException {
        OpenBitSetDISI result = this.initialResult(context, logic, index);
        while (index[0] < this.chain.length) {
            this.doChain(result, logic, this.chain[index[0]].getDocIdSet(context, null));
            index[0] = index[0] + 1;
        }
        return result;
    }

    private DocIdSet getDocIdSet(AtomicReaderContext context, int[] logic, int[] index) throws IOException {
        if (logic.length != this.chain.length) {
            throw new IllegalArgumentException("Invalid number of elements in logic array");
        }
        OpenBitSetDISI result = this.initialResult(context, logic[0], index);
        while (index[0] < this.chain.length) {
            this.doChain(result, logic[index[0]], this.chain[index[0]].getDocIdSet(context, null));
            index[0] = index[0] + 1;
        }
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ChainedFilter: [");
        for (Filter aChain : this.chain) {
            sb.append(aChain);
            sb.append(' ');
        }
        sb.append(']');
        return sb.toString();
    }

    private void doChain(OpenBitSetDISI result, int logic, DocIdSet dis) throws IOException {
        if (dis instanceof OpenBitSet) {
            switch (logic) {
                case 0: {
                    result.or((OpenBitSet)dis);
                    break;
                }
                case 1: {
                    result.and((OpenBitSet)dis);
                    break;
                }
                case 2: {
                    result.andNot((OpenBitSet)dis);
                    break;
                }
                case 3: {
                    result.xor((OpenBitSet)dis);
                    break;
                }
                default: {
                    this.doChain(result, 0, dis);
                    break;
                }
            }
        } else {
            DocIdSetIterator disi;
            if (dis == null) {
                disi = DocIdSetIterator.empty();
            } else {
                disi = dis.iterator();
                if (disi == null) {
                    disi = DocIdSetIterator.empty();
                }
            }
            switch (logic) {
                case 0: {
                    result.inPlaceOr(disi);
                    break;
                }
                case 1: {
                    result.inPlaceAnd(disi);
                    break;
                }
                case 2: {
                    result.inPlaceNot(disi);
                    break;
                }
                case 3: {
                    result.inPlaceXor(disi);
                    break;
                }
                default: {
                    this.doChain(result, 0, dis);
                }
            }
        }
    }
}

