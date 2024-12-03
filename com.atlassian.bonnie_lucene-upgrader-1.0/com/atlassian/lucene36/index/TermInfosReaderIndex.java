/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.SegmentTermEnum;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermInfo;
import com.atlassian.lucene36.util.BitUtil;
import com.atlassian.lucene36.util.BytesRef;
import com.atlassian.lucene36.util.PagedBytes;
import com.atlassian.lucene36.util.packed.GrowableWriter;
import com.atlassian.lucene36.util.packed.PackedInts;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

class TermInfosReaderIndex {
    private static final int MAX_PAGE_BITS = 18;
    private Term[] fields;
    private int totalIndexInterval;
    private Comparator<BytesRef> comparator = BytesRef.getUTF8SortedAsUTF16Comparator();
    private final PagedBytes.PagedBytesDataInput dataInput;
    private final PackedInts.Reader indexToDataOffset;
    private final int indexSize;
    private final int skipInterval;

    TermInfosReaderIndex(SegmentTermEnum indexEnum, int indexDivisor, long tiiFileLength, int totalIndexInterval) throws IOException {
        this.totalIndexInterval = totalIndexInterval;
        this.indexSize = 1 + ((int)indexEnum.size - 1) / indexDivisor;
        this.skipInterval = indexEnum.skipInterval;
        long initialSize = (long)((double)tiiFileLength * 1.5) / (long)indexDivisor;
        PagedBytes dataPagedBytes = new PagedBytes(TermInfosReaderIndex.estimatePageBits(initialSize));
        PagedBytes.PagedBytesDataOutput dataOutput = dataPagedBytes.getDataOutput();
        GrowableWriter indexToTerms = new GrowableWriter(4, this.indexSize, false);
        String currentField = null;
        ArrayList<String> fieldStrs = new ArrayList<String>();
        int fieldCounter = -1;
        int i = 0;
        while (indexEnum.next()) {
            Term term = indexEnum.term();
            if (currentField != term.field) {
                currentField = term.field;
                fieldStrs.add(currentField);
                ++fieldCounter;
            }
            TermInfo termInfo = indexEnum.termInfo();
            indexToTerms.set(i, dataOutput.getPosition());
            dataOutput.writeVInt(fieldCounter);
            dataOutput.writeString(term.text());
            dataOutput.writeVInt(termInfo.docFreq);
            if (termInfo.docFreq >= this.skipInterval) {
                dataOutput.writeVInt(termInfo.skipOffset);
            }
            dataOutput.writeVLong(termInfo.freqPointer);
            dataOutput.writeVLong(termInfo.proxPointer);
            dataOutput.writeVLong(indexEnum.indexPointer);
            for (int j = 1; j < indexDivisor && indexEnum.next(); ++j) {
            }
            ++i;
        }
        this.fields = new Term[fieldStrs.size()];
        for (i = 0; i < this.fields.length; ++i) {
            this.fields[i] = new Term((String)fieldStrs.get(i));
        }
        dataPagedBytes.freeze(true);
        this.dataInput = dataPagedBytes.getDataInput();
        this.indexToDataOffset = indexToTerms.getMutable();
    }

    private static int estimatePageBits(long estSize) {
        return Math.max(Math.min(64 - BitUtil.nlz(estSize), 18), 4);
    }

    void seekEnum(SegmentTermEnum enumerator, int indexOffset) throws IOException {
        PagedBytes.PagedBytesDataInput input = (PagedBytes.PagedBytesDataInput)this.dataInput.clone();
        input.setPosition(this.indexToDataOffset.get(indexOffset));
        int fieldId = input.readVInt();
        Term field = this.fields[fieldId];
        Term term = field.createTerm(input.readString());
        TermInfo termInfo = new TermInfo();
        termInfo.docFreq = input.readVInt();
        termInfo.skipOffset = termInfo.docFreq >= this.skipInterval ? input.readVInt() : 0;
        termInfo.freqPointer = input.readVLong();
        termInfo.proxPointer = input.readVLong();
        long pointer = input.readVLong();
        enumerator.seek(pointer, (long)indexOffset * (long)this.totalIndexInterval - 1L, term, termInfo);
    }

    int getIndexOffset(Term term, BytesRef termBytesRef) throws IOException {
        int lo = 0;
        int hi = this.indexSize - 1;
        PagedBytes.PagedBytesDataInput input = (PagedBytes.PagedBytesDataInput)this.dataInput.clone();
        BytesRef scratch = new BytesRef();
        while (hi >= lo) {
            int mid = lo + hi >>> 1;
            int delta = this.compareTo(term, termBytesRef, mid, input, scratch);
            if (delta < 0) {
                hi = mid - 1;
                continue;
            }
            if (delta > 0) {
                lo = mid + 1;
                continue;
            }
            return mid;
        }
        return hi;
    }

    Term getTerm(int termIndex) throws IOException {
        PagedBytes.PagedBytesDataInput input = (PagedBytes.PagedBytesDataInput)this.dataInput.clone();
        input.setPosition(this.indexToDataOffset.get(termIndex));
        int fieldId = input.readVInt();
        Term field = this.fields[fieldId];
        return field.createTerm(input.readString());
    }

    int length() {
        return this.indexSize;
    }

    int compareTo(Term term, BytesRef termBytesRef, int termIndex) throws IOException {
        return this.compareTo(term, termBytesRef, termIndex, (PagedBytes.PagedBytesDataInput)this.dataInput.clone(), new BytesRef());
    }

    private int compareTo(Term term, BytesRef termBytesRef, int termIndex, PagedBytes.PagedBytesDataInput input, BytesRef reuse) throws IOException {
        int c = this.compareField(term, termIndex, input);
        if (c == 0) {
            reuse.length = input.readVInt();
            reuse.grow(reuse.length);
            input.readBytes(reuse.bytes, 0, reuse.length);
            return this.comparator.compare(termBytesRef, reuse);
        }
        return c;
    }

    private int compareField(Term term, int termIndex, PagedBytes.PagedBytesDataInput input) throws IOException {
        input.setPosition(this.indexToDataOffset.get(termIndex));
        return term.field.compareTo(this.fields[input.readVInt()].field);
    }
}

