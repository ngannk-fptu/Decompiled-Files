/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.PriorityQueue;

class UnionDocsAndPositionsEnum
extends DocsAndPositionsEnum {
    private int _doc;
    private int _freq;
    private DocsQueue _queue;
    private IntQueue _posList;
    private long cost;

    public UnionDocsAndPositionsEnum(Bits liveDocs, AtomicReaderContext context, Term[] terms, Map<Term, TermContext> termContexts, TermsEnum termsEnum) throws IOException {
        LinkedList<DocsAndPositionsEnum> docsEnums = new LinkedList<DocsAndPositionsEnum>();
        for (int i = 0; i < terms.length; ++i) {
            Term term = terms[i];
            TermState termState = termContexts.get(term).get(context.ord);
            if (termState == null) continue;
            termsEnum.seekExact(term.bytes(), termState);
            DocsAndPositionsEnum postings = termsEnum.docsAndPositions(liveDocs, null, 0);
            if (postings == null) {
                throw new IllegalStateException("field \"" + term.field() + "\" was indexed without position data; cannot run PhraseQuery (term=" + term.text() + ")");
            }
            this.cost += postings.cost();
            docsEnums.add(postings);
        }
        this._queue = new DocsQueue(docsEnums);
        this._posList = new IntQueue();
    }

    @Override
    public final int nextDoc() throws IOException {
        if (this._queue.size() == 0) {
            return Integer.MAX_VALUE;
        }
        this._posList.clear();
        this._doc = ((DocsAndPositionsEnum)this._queue.top()).docID();
        do {
            DocsAndPositionsEnum postings = (DocsAndPositionsEnum)this._queue.top();
            int freq = postings.freq();
            for (int i = 0; i < freq; ++i) {
                this._posList.add(postings.nextPosition());
            }
            if (postings.nextDoc() != Integer.MAX_VALUE) {
                this._queue.updateTop();
                continue;
            }
            this._queue.pop();
        } while (this._queue.size() > 0 && ((DocsAndPositionsEnum)this._queue.top()).docID() == this._doc);
        this._posList.sort();
        this._freq = this._posList.size();
        return this._doc;
    }

    @Override
    public int nextPosition() {
        return this._posList.next();
    }

    @Override
    public int startOffset() {
        return -1;
    }

    @Override
    public int endOffset() {
        return -1;
    }

    @Override
    public BytesRef getPayload() {
        return null;
    }

    @Override
    public final int advance(int target) throws IOException {
        while (this._queue.top() != null && target > ((DocsAndPositionsEnum)this._queue.top()).docID()) {
            DocsAndPositionsEnum postings = (DocsAndPositionsEnum)this._queue.pop();
            if (postings.advance(target) == Integer.MAX_VALUE) continue;
            this._queue.add(postings);
        }
        return this.nextDoc();
    }

    @Override
    public final int freq() {
        return this._freq;
    }

    @Override
    public final int docID() {
        return this._doc;
    }

    @Override
    public long cost() {
        return this.cost;
    }

    private static final class IntQueue {
        private int _arraySize = 16;
        private int _index = 0;
        private int _lastIndex = 0;
        private int[] _array = new int[this._arraySize];

        private IntQueue() {
        }

        final void add(int i) {
            if (this._lastIndex == this._arraySize) {
                this.growArray();
            }
            this._array[this._lastIndex++] = i;
        }

        final int next() {
            return this._array[this._index++];
        }

        final void sort() {
            Arrays.sort(this._array, this._index, this._lastIndex);
        }

        final void clear() {
            this._index = 0;
            this._lastIndex = 0;
        }

        final int size() {
            return this._lastIndex - this._index;
        }

        private void growArray() {
            int[] newArray = new int[this._arraySize * 2];
            System.arraycopy(this._array, 0, newArray, 0, this._arraySize);
            this._array = newArray;
            this._arraySize *= 2;
        }
    }

    private static final class DocsQueue
    extends PriorityQueue<DocsAndPositionsEnum> {
        DocsQueue(List<DocsAndPositionsEnum> docsEnums) throws IOException {
            super(docsEnums.size());
            for (DocsAndPositionsEnum postings : docsEnums) {
                if (postings.nextDoc() == Integer.MAX_VALUE) continue;
                this.add(postings);
            }
        }

        @Override
        public final boolean lessThan(DocsAndPositionsEnum a, DocsAndPositionsEnum b) {
            return a.docID() < b.docID();
        }
    }
}

