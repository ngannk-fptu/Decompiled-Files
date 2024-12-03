/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.BinaryDocValues
 *  org.apache.lucene.index.DocsAndPositionsEnum
 *  org.apache.lucene.index.DocsEnum
 *  org.apache.lucene.index.FieldInfo$IndexOptions
 *  org.apache.lucene.index.FieldInfos
 *  org.apache.lucene.index.Fields
 *  org.apache.lucene.index.FilterAtomicReader
 *  org.apache.lucene.index.FilterAtomicReader$FilterDocsAndPositionsEnum
 *  org.apache.lucene.index.FilterAtomicReader$FilterDocsEnum
 *  org.apache.lucene.index.FilterAtomicReader$FilterFields
 *  org.apache.lucene.index.FilterAtomicReader$FilterTerms
 *  org.apache.lucene.index.FilterAtomicReader$FilterTermsEnum
 *  org.apache.lucene.index.NumericDocValues
 *  org.apache.lucene.index.SortedDocValues
 *  org.apache.lucene.index.SortedSetDocValues
 *  org.apache.lucene.index.StoredFieldVisitor
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.store.IndexInput
 *  org.apache.lucene.store.IndexOutput
 *  org.apache.lucene.store.RAMFile
 *  org.apache.lucene.store.RAMInputStream
 *  org.apache.lucene.store.RAMOutputStream
 *  org.apache.lucene.util.ArrayUtil
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.TimSorter
 *  org.apache.lucene.util.automaton.CompiledAutomaton
 */
package org.apache.lucene.index.sorter;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.FilterAtomicReader;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.sorter.Sorter;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.RAMFile;
import org.apache.lucene.store.RAMInputStream;
import org.apache.lucene.store.RAMOutputStream;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.TimSorter;
import org.apache.lucene.util.automaton.CompiledAutomaton;

public class SortingAtomicReader
extends FilterAtomicReader {
    final Sorter.DocMap docMap;

    public static AtomicReader wrap(AtomicReader reader, Sorter sorter) throws IOException {
        return SortingAtomicReader.wrap(reader, sorter.sort(reader));
    }

    public static AtomicReader wrap(AtomicReader reader, Sorter.DocMap docMap) {
        if (docMap == null) {
            return reader;
        }
        if (reader.maxDoc() != docMap.size()) {
            throw new IllegalArgumentException("reader.maxDoc() should be equal to docMap.size(), got" + reader.maxDoc() + " != " + docMap.size());
        }
        assert (Sorter.isConsistent(docMap));
        return new SortingAtomicReader(reader, docMap);
    }

    private SortingAtomicReader(AtomicReader in, Sorter.DocMap docMap) {
        super(in);
        this.docMap = docMap;
    }

    public void document(int docID, StoredFieldVisitor visitor) throws IOException {
        this.in.document(this.docMap.newToOld(docID), visitor);
    }

    public Fields fields() throws IOException {
        Fields fields = this.in.fields();
        if (fields == null) {
            return null;
        }
        return new SortingFields(fields, this.in.getFieldInfos(), this.docMap);
    }

    public BinaryDocValues getBinaryDocValues(String field) throws IOException {
        BinaryDocValues oldDocValues = this.in.getBinaryDocValues(field);
        if (oldDocValues == null) {
            return null;
        }
        return new SortingBinaryDocValues(oldDocValues, this.docMap);
    }

    public Bits getLiveDocs() {
        final Bits inLiveDocs = this.in.getLiveDocs();
        if (inLiveDocs == null) {
            return null;
        }
        return new Bits(){

            public boolean get(int index) {
                return inLiveDocs.get(SortingAtomicReader.this.docMap.newToOld(index));
            }

            public int length() {
                return inLiveDocs.length();
            }
        };
    }

    public NumericDocValues getNormValues(String field) throws IOException {
        NumericDocValues norm = this.in.getNormValues(field);
        if (norm == null) {
            return null;
        }
        return new SortingNumericDocValues(norm, this.docMap);
    }

    public NumericDocValues getNumericDocValues(String field) throws IOException {
        NumericDocValues oldDocValues = this.in.getNumericDocValues(field);
        if (oldDocValues == null) {
            return null;
        }
        return new SortingNumericDocValues(oldDocValues, this.docMap);
    }

    public SortedDocValues getSortedDocValues(String field) throws IOException {
        SortedDocValues sortedDV = this.in.getSortedDocValues(field);
        if (sortedDV == null) {
            return null;
        }
        return new SortingSortedDocValues(sortedDV, this.docMap);
    }

    public SortedSetDocValues getSortedSetDocValues(String field) throws IOException {
        SortedSetDocValues sortedSetDV = this.in.getSortedSetDocValues(field);
        if (sortedSetDV == null) {
            return null;
        }
        return new SortingSortedSetDocValues(sortedSetDV, this.docMap);
    }

    public Fields getTermVectors(int docID) throws IOException {
        return this.in.getTermVectors(this.docMap.newToOld(docID));
    }

    static class SortingDocsAndPositionsEnum
    extends FilterAtomicReader.FilterDocsAndPositionsEnum {
        private final int maxDoc;
        private final DocOffsetSorter sorter;
        private int[] docs;
        private long[] offsets;
        private final int upto;
        private final IndexInput postingInput;
        private final boolean storeOffsets;
        private int docIt = -1;
        private int pos;
        private int startOffset = -1;
        private int endOffset = -1;
        private final BytesRef payload;
        private int currFreq;
        private final RAMFile file;

        SortingDocsAndPositionsEnum(int maxDoc, SortingDocsAndPositionsEnum reuse, DocsAndPositionsEnum in, Sorter.DocMap docMap, boolean storeOffsets) throws IOException {
            super(in);
            int doc;
            this.maxDoc = maxDoc;
            this.storeOffsets = storeOffsets;
            if (reuse != null) {
                this.docs = reuse.docs;
                this.offsets = reuse.offsets;
                this.payload = reuse.payload;
                this.file = reuse.file;
                this.sorter = reuse.maxDoc == maxDoc ? reuse.sorter : new DocOffsetSorter(maxDoc);
            } else {
                this.docs = new int[32];
                this.offsets = new long[32];
                this.payload = new BytesRef(32);
                this.file = new RAMFile();
                this.sorter = new DocOffsetSorter(maxDoc);
            }
            RAMOutputStream out = new RAMOutputStream(this.file);
            int i = 0;
            while ((doc = in.nextDoc()) != Integer.MAX_VALUE) {
                if (i == this.docs.length) {
                    int newLength = ArrayUtil.oversize((int)(i + 1), (int)4);
                    this.docs = Arrays.copyOf(this.docs, newLength);
                    this.offsets = Arrays.copyOf(this.offsets, newLength);
                }
                this.docs[i] = docMap.oldToNew(doc);
                this.offsets[i] = out.getFilePointer();
                this.addPositions(in, (IndexOutput)out);
                ++i;
            }
            this.upto = i;
            this.sorter.reset(this.docs, this.offsets);
            this.sorter.sort(0, this.upto);
            out.close();
            this.postingInput = new RAMInputStream("", this.file);
        }

        boolean reused(DocsAndPositionsEnum other) {
            if (other == null || !(other instanceof SortingDocsAndPositionsEnum)) {
                return false;
            }
            return this.docs == ((SortingDocsAndPositionsEnum)other).docs;
        }

        private void addPositions(DocsAndPositionsEnum in, IndexOutput out) throws IOException {
            int freq = in.freq();
            out.writeVInt(freq);
            int previousPosition = 0;
            int previousEndOffset = 0;
            for (int i = 0; i < freq; ++i) {
                int pos = in.nextPosition();
                BytesRef payload = in.getPayload();
                int token = pos - previousPosition << 1 | (payload == null ? 0 : 1);
                out.writeVInt(token);
                previousPosition = pos;
                if (this.storeOffsets) {
                    int startOffset = in.startOffset();
                    int endOffset = in.endOffset();
                    out.writeVInt(startOffset - previousEndOffset);
                    out.writeVInt(endOffset - startOffset);
                    previousEndOffset = endOffset;
                }
                if (payload == null) continue;
                out.writeVInt(payload.length);
                out.writeBytes(payload.bytes, payload.offset, payload.length);
            }
        }

        public int advance(int target) throws IOException {
            return this.slowAdvance(target);
        }

        public int docID() {
            return this.docIt < 0 ? -1 : (this.docIt >= this.upto ? Integer.MAX_VALUE : this.docs[this.docIt]);
        }

        public int endOffset() throws IOException {
            return this.endOffset;
        }

        public int freq() throws IOException {
            return this.currFreq;
        }

        public BytesRef getPayload() throws IOException {
            return this.payload.length == 0 ? null : this.payload;
        }

        public int nextDoc() throws IOException {
            if (++this.docIt >= this.upto) {
                return Integer.MAX_VALUE;
            }
            this.postingInput.seek(this.offsets[this.docIt]);
            this.currFreq = this.postingInput.readVInt();
            this.pos = 0;
            this.endOffset = 0;
            return this.docs[this.docIt];
        }

        public int nextPosition() throws IOException {
            int token = this.postingInput.readVInt();
            this.pos += token >>> 1;
            if (this.storeOffsets) {
                this.startOffset = this.endOffset + this.postingInput.readVInt();
                this.endOffset = this.startOffset + this.postingInput.readVInt();
            }
            if ((token & 1) != 0) {
                this.payload.offset = 0;
                this.payload.length = this.postingInput.readVInt();
                if (this.payload.length > this.payload.bytes.length) {
                    this.payload.bytes = new byte[ArrayUtil.oversize((int)this.payload.length, (int)1)];
                }
                this.postingInput.readBytes(this.payload.bytes, 0, this.payload.length);
            } else {
                this.payload.length = 0;
            }
            return this.pos;
        }

        public int startOffset() throws IOException {
            return this.startOffset;
        }

        DocsAndPositionsEnum getWrapped() {
            return this.in;
        }

        private static final class DocOffsetSorter
        extends TimSorter {
            private int[] docs;
            private long[] offsets;
            private final int[] tmpDocs;
            private final long[] tmpOffsets;

            public DocOffsetSorter(int maxDoc) {
                super(maxDoc / 64);
                this.tmpDocs = new int[maxDoc / 64];
                this.tmpOffsets = new long[maxDoc / 64];
            }

            public void reset(int[] docs, long[] offsets) {
                this.docs = docs;
                this.offsets = offsets;
            }

            protected int compare(int i, int j) {
                return this.docs[i] - this.docs[j];
            }

            protected void swap(int i, int j) {
                int tmpDoc = this.docs[i];
                this.docs[i] = this.docs[j];
                this.docs[j] = tmpDoc;
                long tmpOffset = this.offsets[i];
                this.offsets[i] = this.offsets[j];
                this.offsets[j] = tmpOffset;
            }

            protected void copy(int src, int dest) {
                this.docs[dest] = this.docs[src];
                this.offsets[dest] = this.offsets[src];
            }

            protected void save(int i, int len) {
                System.arraycopy(this.docs, i, this.tmpDocs, 0, len);
                System.arraycopy(this.offsets, i, this.tmpOffsets, 0, len);
            }

            protected void restore(int i, int j) {
                this.docs[j] = this.tmpDocs[i];
                this.offsets[j] = this.tmpOffsets[i];
            }

            protected int compareSaved(int i, int j) {
                return this.tmpDocs[i] - this.docs[j];
            }
        }
    }

    static class SortingDocsEnum
    extends FilterAtomicReader.FilterDocsEnum {
        private final int maxDoc;
        private final DocFreqSorter sorter;
        private int[] docs;
        private int[] freqs;
        private int docIt = -1;
        private final int upto;
        private final boolean withFreqs;

        SortingDocsEnum(int maxDoc, SortingDocsEnum reuse, DocsEnum in, boolean withFreqs, Sorter.DocMap docMap) throws IOException {
            super(in);
            this.maxDoc = maxDoc;
            this.withFreqs = withFreqs;
            if (reuse != null) {
                this.sorter = reuse.maxDoc == maxDoc ? reuse.sorter : new DocFreqSorter(maxDoc);
                this.docs = reuse.docs;
                this.freqs = reuse.freqs;
            } else {
                this.docs = new int[64];
                this.sorter = new DocFreqSorter(maxDoc);
            }
            this.docIt = -1;
            int i = 0;
            if (withFreqs) {
                int doc;
                if (this.freqs == null || this.freqs.length < this.docs.length) {
                    this.freqs = new int[this.docs.length];
                }
                while ((doc = in.nextDoc()) != Integer.MAX_VALUE) {
                    if (i >= this.docs.length) {
                        this.docs = ArrayUtil.grow((int[])this.docs, (int)(this.docs.length + 1));
                        this.freqs = ArrayUtil.grow((int[])this.freqs, (int)(this.freqs.length + 1));
                    }
                    this.docs[i] = docMap.oldToNew(doc);
                    this.freqs[i] = in.freq();
                    ++i;
                }
            } else {
                int doc;
                this.freqs = null;
                while ((doc = in.nextDoc()) != Integer.MAX_VALUE) {
                    if (i >= this.docs.length) {
                        this.docs = ArrayUtil.grow((int[])this.docs, (int)(this.docs.length + 1));
                    }
                    this.docs[i++] = docMap.oldToNew(doc);
                }
            }
            this.sorter.reset(this.docs, this.freqs);
            this.sorter.sort(0, i);
            this.upto = i;
        }

        boolean reused(DocsEnum other) {
            if (other == null || !(other instanceof SortingDocsEnum)) {
                return false;
            }
            return this.docs == ((SortingDocsEnum)other).docs;
        }

        public int advance(int target) throws IOException {
            return this.slowAdvance(target);
        }

        public int docID() {
            return this.docIt < 0 ? -1 : (this.docIt >= this.upto ? Integer.MAX_VALUE : this.docs[this.docIt]);
        }

        public int freq() throws IOException {
            return this.withFreqs && this.docIt < this.upto ? this.freqs[this.docIt] : 1;
        }

        public int nextDoc() throws IOException {
            if (++this.docIt >= this.upto) {
                return Integer.MAX_VALUE;
            }
            return this.docs[this.docIt];
        }

        DocsEnum getWrapped() {
            return this.in;
        }

        private static final class DocFreqSorter
        extends TimSorter {
            private int[] docs;
            private int[] freqs;
            private final int[] tmpDocs;
            private int[] tmpFreqs;

            public DocFreqSorter(int maxDoc) {
                super(maxDoc / 64);
                this.tmpDocs = new int[maxDoc / 64];
            }

            public void reset(int[] docs, int[] freqs) {
                this.docs = docs;
                this.freqs = freqs;
                if (freqs != null && this.tmpFreqs == null) {
                    this.tmpFreqs = new int[this.tmpDocs.length];
                }
            }

            protected int compare(int i, int j) {
                return this.docs[i] - this.docs[j];
            }

            protected void swap(int i, int j) {
                int tmpDoc = this.docs[i];
                this.docs[i] = this.docs[j];
                this.docs[j] = tmpDoc;
                if (this.freqs != null) {
                    int tmpFreq = this.freqs[i];
                    this.freqs[i] = this.freqs[j];
                    this.freqs[j] = tmpFreq;
                }
            }

            protected void copy(int src, int dest) {
                this.docs[dest] = this.docs[src];
                if (this.freqs != null) {
                    this.freqs[dest] = this.freqs[src];
                }
            }

            protected void save(int i, int len) {
                System.arraycopy(this.docs, i, this.tmpDocs, 0, len);
                if (this.freqs != null) {
                    System.arraycopy(this.freqs, i, this.tmpFreqs, 0, len);
                }
            }

            protected void restore(int i, int j) {
                this.docs[j] = this.tmpDocs[i];
                if (this.freqs != null) {
                    this.freqs[j] = this.tmpFreqs[i];
                }
            }

            protected int compareSaved(int i, int j) {
                return this.tmpDocs[i] - this.docs[j];
            }
        }
    }

    private static class SortingSortedSetDocValues
    extends SortedSetDocValues {
        private final SortedSetDocValues in;
        private final Sorter.DocMap docMap;

        SortingSortedSetDocValues(SortedSetDocValues in, Sorter.DocMap docMap) {
            this.in = in;
            this.docMap = docMap;
        }

        public long nextOrd() {
            return this.in.nextOrd();
        }

        public void setDocument(int docID) {
            this.in.setDocument(this.docMap.newToOld(docID));
        }

        public void lookupOrd(long ord, BytesRef result) {
            this.in.lookupOrd(ord, result);
        }

        public long getValueCount() {
            return this.in.getValueCount();
        }

        public long lookupTerm(BytesRef key) {
            return this.in.lookupTerm(key);
        }
    }

    private static class SortingSortedDocValues
    extends SortedDocValues {
        private final SortedDocValues in;
        private final Sorter.DocMap docMap;

        SortingSortedDocValues(SortedDocValues in, Sorter.DocMap docMap) {
            this.in = in;
            this.docMap = docMap;
        }

        public int getOrd(int docID) {
            return this.in.getOrd(this.docMap.newToOld(docID));
        }

        public void lookupOrd(int ord, BytesRef result) {
            this.in.lookupOrd(ord, result);
        }

        public int getValueCount() {
            return this.in.getValueCount();
        }

        public void get(int docID, BytesRef result) {
            this.in.get(this.docMap.newToOld(docID), result);
        }

        public int lookupTerm(BytesRef key) {
            return this.in.lookupTerm(key);
        }
    }

    private static class SortingNumericDocValues
    extends NumericDocValues {
        private final NumericDocValues in;
        private final Sorter.DocMap docMap;

        public SortingNumericDocValues(NumericDocValues in, Sorter.DocMap docMap) {
            this.in = in;
            this.docMap = docMap;
        }

        public long get(int docID) {
            return this.in.get(this.docMap.newToOld(docID));
        }
    }

    private static class SortingBinaryDocValues
    extends BinaryDocValues {
        private final BinaryDocValues in;
        private final Sorter.DocMap docMap;

        SortingBinaryDocValues(BinaryDocValues in, Sorter.DocMap docMap) {
            this.in = in;
            this.docMap = docMap;
        }

        public void get(int docID, BytesRef result) {
            this.in.get(this.docMap.newToOld(docID), result);
        }
    }

    private static class SortingTermsEnum
    extends FilterAtomicReader.FilterTermsEnum {
        final Sorter.DocMap docMap;
        private final FieldInfo.IndexOptions indexOptions;

        public SortingTermsEnum(TermsEnum in, Sorter.DocMap docMap, FieldInfo.IndexOptions indexOptions) {
            super(in);
            this.docMap = docMap;
            this.indexOptions = indexOptions;
        }

        Bits newToOld(final Bits liveDocs) {
            if (liveDocs == null) {
                return null;
            }
            return new Bits(){

                public boolean get(int index) {
                    return liveDocs.get(docMap.oldToNew(index));
                }

                public int length() {
                    return liveDocs.length();
                }
            };
        }

        public DocsEnum docs(Bits liveDocs, DocsEnum reuse, int flags) throws IOException {
            DocsEnum inReuse;
            SortingDocsEnum wrapReuse;
            if (reuse != null && reuse instanceof SortingDocsEnum) {
                wrapReuse = (SortingDocsEnum)reuse;
                inReuse = wrapReuse.getWrapped();
            } else {
                wrapReuse = null;
                inReuse = reuse;
            }
            DocsEnum inDocs = this.in.docs(this.newToOld(liveDocs), inReuse, flags);
            boolean withFreqs = this.indexOptions.compareTo((Enum)FieldInfo.IndexOptions.DOCS_AND_FREQS) >= 0 && (flags & 1) != 0;
            return new SortingDocsEnum(this.docMap.size(), wrapReuse, inDocs, withFreqs, this.docMap);
        }

        public DocsAndPositionsEnum docsAndPositions(Bits liveDocs, DocsAndPositionsEnum reuse, int flags) throws IOException {
            DocsAndPositionsEnum inReuse;
            SortingDocsAndPositionsEnum wrapReuse;
            if (reuse != null && reuse instanceof SortingDocsAndPositionsEnum) {
                wrapReuse = (SortingDocsAndPositionsEnum)reuse;
                inReuse = wrapReuse.getWrapped();
            } else {
                wrapReuse = null;
                inReuse = reuse;
            }
            DocsAndPositionsEnum inDocsAndPositions = this.in.docsAndPositions(this.newToOld(liveDocs), inReuse, flags);
            if (inDocsAndPositions == null) {
                return null;
            }
            boolean storeOffsets = this.indexOptions.compareTo((Enum)FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
            return new SortingDocsAndPositionsEnum(this.docMap.size(), wrapReuse, inDocsAndPositions, this.docMap, storeOffsets);
        }
    }

    private static class SortingTerms
    extends FilterAtomicReader.FilterTerms {
        private final Sorter.DocMap docMap;
        private final FieldInfo.IndexOptions indexOptions;

        public SortingTerms(Terms in, FieldInfo.IndexOptions indexOptions, Sorter.DocMap docMap) {
            super(in);
            this.docMap = docMap;
            this.indexOptions = indexOptions;
        }

        public TermsEnum iterator(TermsEnum reuse) throws IOException {
            return new SortingTermsEnum(this.in.iterator(reuse), this.docMap, this.indexOptions);
        }

        public TermsEnum intersect(CompiledAutomaton compiled, BytesRef startTerm) throws IOException {
            return new SortingTermsEnum(this.in.intersect(compiled, startTerm), this.docMap, this.indexOptions);
        }
    }

    private static class SortingFields
    extends FilterAtomicReader.FilterFields {
        private final Sorter.DocMap docMap;
        private final FieldInfos infos;

        public SortingFields(Fields in, FieldInfos infos, Sorter.DocMap docMap) {
            super(in);
            this.docMap = docMap;
            this.infos = infos;
        }

        public Terms terms(String field) throws IOException {
            Terms terms = this.in.terms(field);
            if (terms == null) {
                return null;
            }
            return new SortingTerms(terms, this.infos.fieldInfo(field).getIndexOptions(), this.docMap);
        }
    }
}

