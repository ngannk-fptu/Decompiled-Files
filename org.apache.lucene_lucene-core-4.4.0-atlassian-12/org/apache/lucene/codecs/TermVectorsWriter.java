/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.Closeable;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

public abstract class TermVectorsWriter
implements Closeable {
    protected TermVectorsWriter() {
    }

    public abstract void startDocument(int var1) throws IOException;

    public void finishDocument() throws IOException {
    }

    public abstract void startField(FieldInfo var1, int var2, boolean var3, boolean var4, boolean var5) throws IOException;

    public void finishField() throws IOException {
    }

    public abstract void startTerm(BytesRef var1, int var2) throws IOException;

    public void finishTerm() throws IOException {
    }

    public abstract void addPosition(int var1, int var2, int var3, BytesRef var4) throws IOException;

    public abstract void abort();

    public abstract void finish(FieldInfos var1, int var2) throws IOException;

    public void addProx(int numProx, DataInput positions, DataInput offsets) throws IOException {
        int position = 0;
        int lastOffset = 0;
        BytesRef payload = null;
        for (int i = 0; i < numProx; ++i) {
            int startOffset;
            int endOffset;
            BytesRef thisPayload;
            if (positions == null) {
                position = -1;
                thisPayload = null;
            } else {
                int code = positions.readVInt();
                position += code >>> 1;
                if ((code & 1) != 0) {
                    int payloadLength = positions.readVInt();
                    if (payload == null) {
                        payload = new BytesRef();
                        payload.bytes = new byte[payloadLength];
                    } else if (payload.bytes.length < payloadLength) {
                        payload.grow(payloadLength);
                    }
                    positions.readBytes(payload.bytes, 0, payloadLength);
                    payload.length = payloadLength;
                    thisPayload = payload;
                } else {
                    thisPayload = null;
                }
            }
            if (offsets == null) {
                endOffset = -1;
                startOffset = -1;
            } else {
                startOffset = lastOffset + offsets.readVInt();
                lastOffset = endOffset = startOffset + offsets.readVInt();
            }
            this.addPosition(position, startOffset, endOffset, thisPayload);
        }
    }

    public int merge(MergeState mergeState) throws IOException {
        int docCount = 0;
        for (int i = 0; i < mergeState.readers.size(); ++i) {
            AtomicReader reader = mergeState.readers.get(i);
            int maxDoc = reader.maxDoc();
            Bits liveDocs = reader.getLiveDocs();
            for (int docID = 0; docID < maxDoc; ++docID) {
                if (liveDocs != null && !liveDocs.get(docID)) continue;
                Fields vectors = reader.getTermVectors(docID);
                this.addAllDocVectors(vectors, mergeState);
                ++docCount;
                mergeState.checkAbort.work(300.0);
            }
        }
        this.finish(mergeState.fieldInfos, docCount);
        return docCount;
    }

    protected final void addAllDocVectors(Fields vectors, MergeState mergeState) throws IOException {
        if (vectors == null) {
            this.startDocument(0);
            this.finishDocument();
            return;
        }
        int numFields = vectors.size();
        if (numFields == -1) {
            numFields = 0;
            Iterator<String> it = vectors.iterator();
            while (it.hasNext()) {
                it.next();
                ++numFields;
            }
        }
        this.startDocument(numFields);
        String lastFieldName = null;
        TermsEnum termsEnum = null;
        DocsAndPositionsEnum docsAndPositionsEnum = null;
        int fieldCount = 0;
        for (String fieldName : vectors) {
            ++fieldCount;
            FieldInfo fieldInfo = mergeState.fieldInfos.fieldInfo(fieldName);
            assert (lastFieldName == null || fieldName.compareTo(lastFieldName) > 0) : "lastFieldName=" + lastFieldName + " fieldName=" + fieldName;
            lastFieldName = fieldName;
            Terms terms = vectors.terms(fieldName);
            if (terms == null) continue;
            boolean hasPositions = terms.hasPositions();
            boolean hasOffsets = terms.hasOffsets();
            boolean hasPayloads = terms.hasPayloads();
            assert (!hasPayloads || hasPositions);
            int numTerms = (int)terms.size();
            if (numTerms == -1) {
                numTerms = 0;
                termsEnum = terms.iterator(termsEnum);
                while (termsEnum.next() != null) {
                    ++numTerms;
                }
            }
            this.startField(fieldInfo, numTerms, hasPositions, hasOffsets, hasPayloads);
            termsEnum = terms.iterator(termsEnum);
            int termCount = 0;
            while (termsEnum.next() != null) {
                ++termCount;
                int freq = (int)termsEnum.totalTermFreq();
                this.startTerm(termsEnum.term(), freq);
                if (hasPositions || hasOffsets) {
                    docsAndPositionsEnum = termsEnum.docsAndPositions(null, docsAndPositionsEnum);
                    assert (docsAndPositionsEnum != null);
                    int docID = docsAndPositionsEnum.nextDoc();
                    assert (docID != Integer.MAX_VALUE);
                    assert (docsAndPositionsEnum.freq() == freq);
                    for (int posUpto = 0; posUpto < freq; ++posUpto) {
                        int pos = docsAndPositionsEnum.nextPosition();
                        int startOffset = docsAndPositionsEnum.startOffset();
                        int endOffset = docsAndPositionsEnum.endOffset();
                        BytesRef payload = docsAndPositionsEnum.getPayload();
                        assert (!hasPositions || pos >= 0);
                        this.addPosition(pos, startOffset, endOffset, payload);
                    }
                }
                this.finishTerm();
            }
            assert (termCount == numTerms);
            this.finishField();
        }
        assert (fieldCount == numFields);
        this.finishDocument();
    }

    public abstract Comparator<BytesRef> getComparator() throws IOException;

    @Override
    public abstract void close() throws IOException;
}

