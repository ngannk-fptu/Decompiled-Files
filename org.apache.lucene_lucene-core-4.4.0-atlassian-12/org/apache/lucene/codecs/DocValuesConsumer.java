/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.MultiDocValues;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.OpenBitSet;

public abstract class DocValuesConsumer
implements Closeable {
    protected DocValuesConsumer() {
    }

    public abstract void addNumericField(FieldInfo var1, Iterable<Number> var2) throws IOException;

    public abstract void addBinaryField(FieldInfo var1, Iterable<BytesRef> var2) throws IOException;

    public abstract void addSortedField(FieldInfo var1, Iterable<BytesRef> var2, Iterable<Number> var3) throws IOException;

    public abstract void addSortedSetField(FieldInfo var1, Iterable<BytesRef> var2, Iterable<Number> var3, Iterable<Number> var4) throws IOException;

    public void mergeNumericField(FieldInfo fieldInfo, final MergeState mergeState, final List<NumericDocValues> toMerge) throws IOException {
        this.addNumericField(fieldInfo, new Iterable<Number>(){

            @Override
            public Iterator<Number> iterator() {
                return new Iterator<Number>(){
                    int readerUpto = -1;
                    int docIDUpto;
                    long nextValue;
                    AtomicReader currentReader;
                    NumericDocValues currentValues;
                    Bits currentLiveDocs;
                    boolean nextIsSet;

                    @Override
                    public boolean hasNext() {
                        return this.nextIsSet || this.setNext();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public Number next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        assert (this.nextIsSet);
                        this.nextIsSet = false;
                        return this.nextValue;
                    }

                    private boolean setNext() {
                        while (this.readerUpto != toMerge.size()) {
                            if (this.currentReader == null || this.docIDUpto == this.currentReader.maxDoc()) {
                                ++this.readerUpto;
                                if (this.readerUpto < toMerge.size()) {
                                    this.currentReader = mergeState.readers.get(this.readerUpto);
                                    this.currentValues = (NumericDocValues)toMerge.get(this.readerUpto);
                                    this.currentLiveDocs = this.currentReader.getLiveDocs();
                                }
                                this.docIDUpto = 0;
                                continue;
                            }
                            if (this.currentLiveDocs == null || this.currentLiveDocs.get(this.docIDUpto)) {
                                this.nextIsSet = true;
                                this.nextValue = this.currentValues.get(this.docIDUpto);
                                ++this.docIDUpto;
                                return true;
                            }
                            ++this.docIDUpto;
                        }
                        return false;
                    }
                };
            }
        });
    }

    public void mergeBinaryField(FieldInfo fieldInfo, final MergeState mergeState, final List<BinaryDocValues> toMerge) throws IOException {
        this.addBinaryField(fieldInfo, new Iterable<BytesRef>(){

            @Override
            public Iterator<BytesRef> iterator() {
                return new Iterator<BytesRef>(){
                    int readerUpto = -1;
                    int docIDUpto;
                    BytesRef nextValue = new BytesRef();
                    AtomicReader currentReader;
                    BinaryDocValues currentValues;
                    Bits currentLiveDocs;
                    boolean nextIsSet;

                    @Override
                    public boolean hasNext() {
                        return this.nextIsSet || this.setNext();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public BytesRef next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        assert (this.nextIsSet);
                        this.nextIsSet = false;
                        return this.nextValue;
                    }

                    private boolean setNext() {
                        while (this.readerUpto != toMerge.size()) {
                            if (this.currentReader == null || this.docIDUpto == this.currentReader.maxDoc()) {
                                ++this.readerUpto;
                                if (this.readerUpto < toMerge.size()) {
                                    this.currentReader = mergeState.readers.get(this.readerUpto);
                                    this.currentValues = (BinaryDocValues)toMerge.get(this.readerUpto);
                                    this.currentLiveDocs = this.currentReader.getLiveDocs();
                                }
                                this.docIDUpto = 0;
                                continue;
                            }
                            if (this.currentLiveDocs == null || this.currentLiveDocs.get(this.docIDUpto)) {
                                this.nextIsSet = true;
                                this.currentValues.get(this.docIDUpto, this.nextValue);
                                ++this.docIDUpto;
                                return true;
                            }
                            ++this.docIDUpto;
                        }
                        return false;
                    }
                };
            }
        });
    }

    public void mergeSortedField(FieldInfo fieldInfo, MergeState mergeState, List<SortedDocValues> toMerge) throws IOException {
        final AtomicReader[] readers = mergeState.readers.toArray(new AtomicReader[toMerge.size()]);
        final SortedDocValues[] dvs = toMerge.toArray(new SortedDocValues[toMerge.size()]);
        TermsEnum[] liveTerms = new TermsEnum[dvs.length];
        for (int sub = 0; sub < liveTerms.length; ++sub) {
            AtomicReader reader = readers[sub];
            SortedDocValues dv = dvs[sub];
            Bits liveDocs = reader.getLiveDocs();
            if (liveDocs == null) {
                liveTerms[sub] = dv.termsEnum();
                continue;
            }
            OpenBitSet bitset = new OpenBitSet(dv.getValueCount());
            for (int i = 0; i < reader.maxDoc(); ++i) {
                if (!liveDocs.get(i)) continue;
                bitset.set(dv.getOrd(i));
            }
            liveTerms[sub] = new BitsFilteredTermsEnum(dv.termsEnum(), bitset);
        }
        final MultiDocValues.OrdinalMap map = new MultiDocValues.OrdinalMap(this, liveTerms);
        this.addSortedField(fieldInfo, new Iterable<BytesRef>(){

            @Override
            public Iterator<BytesRef> iterator() {
                return new Iterator<BytesRef>(){
                    final BytesRef scratch = new BytesRef();
                    int currentOrd;

                    @Override
                    public boolean hasNext() {
                        return (long)this.currentOrd < map.getValueCount();
                    }

                    @Override
                    public BytesRef next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        int segmentNumber = map.getSegmentNumber(this.currentOrd);
                        int segmentOrd = (int)map.getSegmentOrd(segmentNumber, this.currentOrd);
                        dvs[segmentNumber].lookupOrd(segmentOrd, this.scratch);
                        ++this.currentOrd;
                        return this.scratch;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        }, new Iterable<Number>(){

            @Override
            public Iterator<Number> iterator() {
                return new Iterator<Number>(){
                    int readerUpto = -1;
                    int docIDUpto;
                    int nextValue;
                    AtomicReader currentReader;
                    Bits currentLiveDocs;
                    boolean nextIsSet;

                    @Override
                    public boolean hasNext() {
                        return this.nextIsSet || this.setNext();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public Number next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        assert (this.nextIsSet);
                        this.nextIsSet = false;
                        return this.nextValue;
                    }

                    private boolean setNext() {
                        while (this.readerUpto != readers.length) {
                            if (this.currentReader == null || this.docIDUpto == this.currentReader.maxDoc()) {
                                ++this.readerUpto;
                                if (this.readerUpto < readers.length) {
                                    this.currentReader = readers[this.readerUpto];
                                    this.currentLiveDocs = this.currentReader.getLiveDocs();
                                }
                                this.docIDUpto = 0;
                                continue;
                            }
                            if (this.currentLiveDocs == null || this.currentLiveDocs.get(this.docIDUpto)) {
                                this.nextIsSet = true;
                                int segOrd = dvs[this.readerUpto].getOrd(this.docIDUpto);
                                this.nextValue = (int)map.getGlobalOrd(this.readerUpto, segOrd);
                                ++this.docIDUpto;
                                return true;
                            }
                            ++this.docIDUpto;
                        }
                        return false;
                    }
                };
            }
        });
    }

    public void mergeSortedSetField(FieldInfo fieldInfo, MergeState mergeState, List<SortedSetDocValues> toMerge) throws IOException {
        final AtomicReader[] readers = mergeState.readers.toArray(new AtomicReader[toMerge.size()]);
        final SortedSetDocValues[] dvs = toMerge.toArray(new SortedSetDocValues[toMerge.size()]);
        TermsEnum[] liveTerms = new TermsEnum[dvs.length];
        for (int sub = 0; sub < liveTerms.length; ++sub) {
            AtomicReader reader = readers[sub];
            SortedSetDocValues dv = dvs[sub];
            Bits liveDocs = reader.getLiveDocs();
            if (liveDocs == null) {
                liveTerms[sub] = dv.termsEnum();
                continue;
            }
            OpenBitSet bitset = new OpenBitSet(dv.getValueCount());
            for (int i = 0; i < reader.maxDoc(); ++i) {
                long ord;
                if (!liveDocs.get(i)) continue;
                dv.setDocument(i);
                while ((ord = dv.nextOrd()) != -1L) {
                    bitset.set(ord);
                }
            }
            liveTerms[sub] = new BitsFilteredTermsEnum(dv.termsEnum(), bitset);
        }
        final MultiDocValues.OrdinalMap map = new MultiDocValues.OrdinalMap(this, liveTerms);
        this.addSortedSetField(fieldInfo, new Iterable<BytesRef>(){

            @Override
            public Iterator<BytesRef> iterator() {
                return new Iterator<BytesRef>(){
                    final BytesRef scratch = new BytesRef();
                    long currentOrd;

                    @Override
                    public boolean hasNext() {
                        return this.currentOrd < map.getValueCount();
                    }

                    @Override
                    public BytesRef next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        int segmentNumber = map.getSegmentNumber(this.currentOrd);
                        long segmentOrd = map.getSegmentOrd(segmentNumber, this.currentOrd);
                        dvs[segmentNumber].lookupOrd(segmentOrd, this.scratch);
                        ++this.currentOrd;
                        return this.scratch;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        }, new Iterable<Number>(){

            @Override
            public Iterator<Number> iterator() {
                return new Iterator<Number>(){
                    int readerUpto = -1;
                    int docIDUpto;
                    int nextValue;
                    AtomicReader currentReader;
                    Bits currentLiveDocs;
                    boolean nextIsSet;

                    @Override
                    public boolean hasNext() {
                        return this.nextIsSet || this.setNext();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public Number next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        assert (this.nextIsSet);
                        this.nextIsSet = false;
                        return this.nextValue;
                    }

                    private boolean setNext() {
                        while (this.readerUpto != readers.length) {
                            if (this.currentReader == null || this.docIDUpto == this.currentReader.maxDoc()) {
                                ++this.readerUpto;
                                if (this.readerUpto < readers.length) {
                                    this.currentReader = readers[this.readerUpto];
                                    this.currentLiveDocs = this.currentReader.getLiveDocs();
                                }
                                this.docIDUpto = 0;
                                continue;
                            }
                            if (this.currentLiveDocs == null || this.currentLiveDocs.get(this.docIDUpto)) {
                                this.nextIsSet = true;
                                SortedSetDocValues dv = dvs[this.readerUpto];
                                dv.setDocument(this.docIDUpto);
                                this.nextValue = 0;
                                while (dv.nextOrd() != -1L) {
                                    ++this.nextValue;
                                }
                                ++this.docIDUpto;
                                return true;
                            }
                            ++this.docIDUpto;
                        }
                        return false;
                    }
                };
            }
        }, new Iterable<Number>(){

            @Override
            public Iterator<Number> iterator() {
                return new Iterator<Number>(){
                    int readerUpto = -1;
                    int docIDUpto;
                    long nextValue;
                    AtomicReader currentReader;
                    Bits currentLiveDocs;
                    boolean nextIsSet;
                    long[] ords = new long[8];
                    int ordUpto;
                    int ordLength;

                    @Override
                    public boolean hasNext() {
                        return this.nextIsSet || this.setNext();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public Number next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        assert (this.nextIsSet);
                        this.nextIsSet = false;
                        return this.nextValue;
                    }

                    private boolean setNext() {
                        while (this.readerUpto != readers.length) {
                            if (this.ordUpto < this.ordLength) {
                                this.nextValue = this.ords[this.ordUpto];
                                ++this.ordUpto;
                                this.nextIsSet = true;
                                return true;
                            }
                            if (this.currentReader == null || this.docIDUpto == this.currentReader.maxDoc()) {
                                ++this.readerUpto;
                                if (this.readerUpto < readers.length) {
                                    this.currentReader = readers[this.readerUpto];
                                    this.currentLiveDocs = this.currentReader.getLiveDocs();
                                }
                                this.docIDUpto = 0;
                                continue;
                            }
                            if (this.currentLiveDocs == null || this.currentLiveDocs.get(this.docIDUpto)) {
                                long ord;
                                assert (this.docIDUpto < this.currentReader.maxDoc());
                                SortedSetDocValues dv = dvs[this.readerUpto];
                                dv.setDocument(this.docIDUpto);
                                this.ordLength = 0;
                                this.ordUpto = 0;
                                while ((ord = dv.nextOrd()) != -1L) {
                                    if (this.ordLength == this.ords.length) {
                                        this.ords = ArrayUtil.grow(this.ords, this.ordLength + 1);
                                    }
                                    this.ords[this.ordLength] = map.getGlobalOrd(this.readerUpto, ord);
                                    ++this.ordLength;
                                }
                                ++this.docIDUpto;
                                continue;
                            }
                            ++this.docIDUpto;
                        }
                        return false;
                    }
                };
            }
        });
    }

    static class BitsFilteredTermsEnum
    extends FilteredTermsEnum {
        final OpenBitSet liveTerms;

        BitsFilteredTermsEnum(TermsEnum in, OpenBitSet liveTerms) {
            super(in, false);
            assert (liveTerms != null);
            this.liveTerms = liveTerms;
        }

        @Override
        protected FilteredTermsEnum.AcceptStatus accept(BytesRef term) throws IOException {
            if (this.liveTerms.get(this.ord())) {
                return FilteredTermsEnum.AcceptStatus.YES;
            }
            return FilteredTermsEnum.AcceptStatus.NO;
        }
    }
}

