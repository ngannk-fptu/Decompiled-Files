/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermEnum;
import com.atlassian.lucene36.index.TermPositions;
import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.PriorityQueue;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MultipleTermPositions
implements TermPositions {
    private int _doc;
    private int _freq;
    private TermPositionsQueue _termPositionsQueue;
    private IntQueue _posList;

    public MultipleTermPositions(IndexReader indexReader, Term[] terms) throws IOException {
        LinkedList<TermPositions> termPositions = new LinkedList<TermPositions>();
        for (int i = 0; i < terms.length; ++i) {
            termPositions.add(indexReader.termPositions(terms[i]));
        }
        this._termPositionsQueue = new TermPositionsQueue(termPositions);
        this._posList = new IntQueue();
    }

    public final boolean next() throws IOException {
        if (this._termPositionsQueue.size() == 0) {
            return false;
        }
        this._posList.clear();
        this._doc = this._termPositionsQueue.peek().doc();
        do {
            TermPositions tp = this._termPositionsQueue.peek();
            for (int i = 0; i < tp.freq(); ++i) {
                this._posList.add(tp.nextPosition());
            }
            if (tp.next()) {
                this._termPositionsQueue.updateTop();
                continue;
            }
            this._termPositionsQueue.pop();
            tp.close();
        } while (this._termPositionsQueue.size() > 0 && this._termPositionsQueue.peek().doc() == this._doc);
        this._posList.sort();
        this._freq = this._posList.size();
        return true;
    }

    public final int nextPosition() {
        return this._posList.next();
    }

    public final boolean skipTo(int target) throws IOException {
        while (this._termPositionsQueue.peek() != null && target > this._termPositionsQueue.peek().doc()) {
            TermPositions tp = (TermPositions)this._termPositionsQueue.pop();
            if (tp.skipTo(target)) {
                this._termPositionsQueue.add(tp);
                continue;
            }
            tp.close();
        }
        return this.next();
    }

    public final int doc() {
        return this._doc;
    }

    public final int freq() {
        return this._freq;
    }

    public final void close() throws IOException {
        while (this._termPositionsQueue.size() > 0) {
            ((TermPositions)this._termPositionsQueue.pop()).close();
        }
    }

    public void seek(Term arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void seek(TermEnum termEnum) throws IOException {
        throw new UnsupportedOperationException();
    }

    public int read(int[] arg0, int[] arg1) throws IOException {
        throw new UnsupportedOperationException();
    }

    public int getPayloadLength() {
        throw new UnsupportedOperationException();
    }

    public byte[] getPayload(byte[] data, int offset) throws IOException {
        throw new UnsupportedOperationException();
    }

    public boolean isPayloadAvailable() {
        return false;
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
            this._array = ArrayUtil.grow(this._array, this._arraySize + 1);
            this._arraySize = this._array.length;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class TermPositionsQueue
    extends PriorityQueue<TermPositions> {
        TermPositionsQueue(List<TermPositions> termPositions) throws IOException {
            this.initialize(termPositions.size());
            for (TermPositions tp : termPositions) {
                if (!tp.next()) continue;
                this.add(tp);
            }
        }

        final TermPositions peek() {
            return (TermPositions)this.top();
        }

        @Override
        public final boolean lessThan(TermPositions a, TermPositions b) {
            return a.doc() < b.doc();
        }
    }
}

