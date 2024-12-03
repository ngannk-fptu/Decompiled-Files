/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.PagedBytes;
import org.apache.lucene.util.StringHelper;

public class DocTermOrds {
    private static final int TNUM_OFFSET = 2;
    public static final int DEFAULT_INDEX_INTERVAL_BITS = 7;
    private int indexIntervalBits;
    private int indexIntervalMask;
    private int indexInterval;
    protected final int maxTermDocFreq;
    protected final String field;
    protected int numTermsInField;
    protected long termInstances;
    private long memsz;
    protected int total_time;
    protected int phase1_time;
    protected int[] index;
    protected byte[][] tnums = new byte[256][];
    protected long sizeOfIndexedStrings;
    protected BytesRef[] indexedTermsArray;
    protected BytesRef prefix;
    protected int ordBase;
    protected DocsEnum docsEnum;

    public long ramUsedInBytes() {
        if (this.memsz != 0L) {
            return this.memsz;
        }
        long sz = 96L;
        if (this.index != null) {
            sz += (long)(this.index.length * 4);
        }
        if (this.tnums != null) {
            for (byte[] arr : this.tnums) {
                if (arr == null) continue;
                sz += (long)arr.length;
            }
        }
        this.memsz = sz;
        return sz;
    }

    public DocTermOrds(AtomicReader reader, Bits liveDocs, String field) throws IOException {
        this(reader, liveDocs, field, null, Integer.MAX_VALUE);
    }

    public DocTermOrds(AtomicReader reader, Bits liveDocs, String field, BytesRef termPrefix) throws IOException {
        this(reader, liveDocs, field, termPrefix, Integer.MAX_VALUE);
    }

    public DocTermOrds(AtomicReader reader, Bits liveDocs, String field, BytesRef termPrefix, int maxTermDocFreq) throws IOException {
        this(reader, liveDocs, field, termPrefix, maxTermDocFreq, 7);
    }

    public DocTermOrds(AtomicReader reader, Bits liveDocs, String field, BytesRef termPrefix, int maxTermDocFreq, int indexIntervalBits) throws IOException {
        this(field, maxTermDocFreq, indexIntervalBits);
        this.uninvert(reader, liveDocs, termPrefix);
    }

    protected DocTermOrds(String field, int maxTermDocFreq, int indexIntervalBits) {
        this.field = field;
        this.maxTermDocFreq = maxTermDocFreq;
        this.indexIntervalBits = indexIntervalBits;
        this.indexIntervalMask = -1 >>> 32 - indexIntervalBits;
        this.indexInterval = 1 << indexIntervalBits;
    }

    public TermsEnum getOrdTermsEnum(AtomicReader reader) throws IOException {
        if (this.indexedTermsArray == null) {
            Fields fields = reader.fields();
            if (fields == null) {
                return null;
            }
            Terms terms = fields.terms(this.field);
            if (terms == null) {
                return null;
            }
            return terms.iterator(null);
        }
        return new OrdWrappedTermsEnum(reader);
    }

    public int numTerms() {
        return this.numTermsInField;
    }

    public boolean isEmpty() {
        return this.index == null;
    }

    protected void visitTerm(TermsEnum te, int termNum) throws IOException {
    }

    protected void setActualDocFreq(int termNum, int df) throws IOException {
    }

    protected void uninvert(AtomicReader reader, Bits liveDocs, BytesRef termPrefix) throws IOException {
        BytesRef t;
        BytesRef seekStart;
        FieldInfo info = reader.getFieldInfos().fieldInfo(this.field);
        if (info != null && info.hasDocValues()) {
            throw new IllegalStateException("Type mismatch: " + this.field + " was indexed as " + (Object)((Object)info.getDocValuesType()));
        }
        long startTime = System.currentTimeMillis();
        this.prefix = termPrefix == null ? null : BytesRef.deepCopyOf(termPrefix);
        int maxDoc = reader.maxDoc();
        int[] index = new int[maxDoc];
        int[] lastTerm = new int[maxDoc];
        byte[][] bytes = new byte[maxDoc][];
        Fields fields = reader.fields();
        if (fields == null) {
            return;
        }
        Terms terms = fields.terms(this.field);
        if (terms == null) {
            return;
        }
        TermsEnum te = terms.iterator(null);
        BytesRef bytesRef = seekStart = termPrefix != null ? termPrefix : new BytesRef();
        if (te.seekCeil(seekStart) == TermsEnum.SeekStatus.END) {
            return;
        }
        ArrayList<BytesRef> indexedTerms = null;
        PagedBytes indexedTermsBytes = null;
        boolean testedOrd = false;
        byte[] tempArr = new byte[12];
        int termNum = 0;
        this.docsEnum = null;
        while ((t = te.term()) != null && (termPrefix == null || StringHelper.startsWith(t, termPrefix))) {
            int df;
            if (!testedOrd) {
                try {
                    this.ordBase = (int)te.ord();
                }
                catch (UnsupportedOperationException uoe) {
                    indexedTerms = new ArrayList<BytesRef>();
                    indexedTermsBytes = new PagedBytes(15);
                }
                testedOrd = true;
            }
            this.visitTerm(te, termNum);
            if (indexedTerms != null && (termNum & this.indexIntervalMask) == 0) {
                this.sizeOfIndexedStrings += (long)t.length;
                BytesRef indexedTerm = new BytesRef();
                indexedTermsBytes.copy(t, indexedTerm);
                indexedTerms.add(indexedTerm);
            }
            if ((df = te.docFreq()) <= this.maxTermDocFreq) {
                int doc;
                this.docsEnum = te.docs(liveDocs, this.docsEnum, 0);
                int actualDF = 0;
                while ((doc = this.docsEnum.nextDoc()) != Integer.MAX_VALUE) {
                    ++actualDF;
                    ++this.termInstances;
                    int delta = termNum - lastTerm[doc] + 2;
                    lastTerm[doc] = termNum;
                    int val = index[doc];
                    if ((val & 0xFF) == 1) {
                        byte[] arr;
                        int pos = val >>> 8;
                        int ilen = DocTermOrds.vIntSize(delta);
                        int newend = pos + ilen;
                        if (newend > (arr = bytes[doc]).length) {
                            int newLen = newend + 3 & 0xFFFFFFFC;
                            byte[] newarr = new byte[newLen];
                            System.arraycopy(arr, 0, newarr, 0, pos);
                            arr = newarr;
                            bytes[doc] = newarr;
                        }
                        pos = DocTermOrds.writeInt(delta, arr, pos);
                        index[doc] = pos << 8 | 1;
                        continue;
                    }
                    int ipos = val == 0 ? 0 : ((val & 0xFF80) == 0 ? 1 : ((val & 0xFF8000) == 0 ? 2 : ((val & 0xFF800000) == 0 ? 3 : 4)));
                    int endPos = DocTermOrds.writeInt(delta, tempArr, ipos);
                    if (endPos <= 4) {
                        for (int j = ipos; j < endPos; ++j) {
                            val |= (tempArr[j] & 0xFF) << (j << 3);
                        }
                        index[doc] = val;
                        continue;
                    }
                    for (int j = 0; j < ipos; ++j) {
                        tempArr[j] = (byte)val;
                        val >>>= 8;
                    }
                    index[doc] = endPos << 8 | 1;
                    bytes[doc] = tempArr;
                    tempArr = new byte[12];
                }
                this.setActualDocFreq(termNum, actualDF);
            }
            ++termNum;
            if (te.next() != null) continue;
            break;
        }
        this.numTermsInField = termNum;
        long midPoint = System.currentTimeMillis();
        if (this.termInstances == 0L) {
            this.tnums = null;
        } else {
            this.index = index;
            for (int pass = 0; pass < 256; ++pass) {
                byte[] target = this.tnums[pass];
                int pos = 0;
                if (target != null) {
                    pos = target.length;
                } else {
                    target = new byte[4096];
                }
                for (int docbase = pass << 16; docbase < maxDoc; docbase += 0x1000000) {
                    int lim = Math.min(docbase + 65536, maxDoc);
                    for (int doc = docbase; doc < lim; ++doc) {
                        int val = index[doc];
                        if ((val & 0xFF) != 1) continue;
                        int len = val >>> 8;
                        index[doc] = pos << 8 | 1;
                        if ((pos & 0xFF000000) != 0) {
                            throw new IllegalStateException("Too many values for UnInvertedField faceting on field " + this.field);
                        }
                        byte[] arr = bytes[doc];
                        bytes[doc] = null;
                        if (target.length <= pos + len) {
                            int newlen;
                            for (newlen = target.length; newlen <= pos + len; newlen <<= 1) {
                            }
                            byte[] newtarget = new byte[newlen];
                            System.arraycopy(target, 0, newtarget, 0, pos);
                            target = newtarget;
                        }
                        System.arraycopy(arr, 0, target, pos, len);
                        pos += len + 1;
                    }
                }
                if (pos < target.length) {
                    byte[] newtarget = new byte[pos];
                    System.arraycopy(target, 0, newtarget, 0, pos);
                    target = newtarget;
                }
                this.tnums[pass] = target;
                if (pass << 16 > maxDoc) break;
            }
        }
        if (indexedTerms != null) {
            this.indexedTermsArray = indexedTerms.toArray(new BytesRef[indexedTerms.size()]);
        }
        long endTime = System.currentTimeMillis();
        this.total_time = (int)(endTime - startTime);
        this.phase1_time = (int)(midPoint - startTime);
    }

    private static int vIntSize(int x) {
        if ((x & 0xFFFFFF80) == 0) {
            return 1;
        }
        if ((x & 0xFFFFC000) == 0) {
            return 2;
        }
        if ((x & 0xFFE00000) == 0) {
            return 3;
        }
        if ((x & 0xF0000000) == 0) {
            return 4;
        }
        return 5;
    }

    private static int writeInt(int x, byte[] arr, int pos) {
        int a = x >>> 28;
        if (a != 0) {
            arr[pos++] = (byte)(a | 0x80);
        }
        if ((a = x >>> 21) != 0) {
            arr[pos++] = (byte)(a | 0x80);
        }
        if ((a = x >>> 14) != 0) {
            arr[pos++] = (byte)(a | 0x80);
        }
        if ((a = x >>> 7) != 0) {
            arr[pos++] = (byte)(a | 0x80);
        }
        arr[pos++] = (byte)(x & 0x7F);
        return pos;
    }

    public BytesRef lookupTerm(TermsEnum termsEnum, int ord) throws IOException {
        termsEnum.seekExact(ord);
        return termsEnum.term();
    }

    public SortedSetDocValues iterator(AtomicReader reader) throws IOException {
        if (this.isEmpty()) {
            return SortedSetDocValues.EMPTY;
        }
        return new Iterator(reader);
    }

    private class Iterator
    extends SortedSetDocValues {
        final AtomicReader reader;
        final TermsEnum te;
        final int[] buffer = new int[5];
        int bufferUpto;
        int bufferLength;
        private int tnum;
        private int upto;
        private byte[] arr;

        Iterator(AtomicReader reader) throws IOException {
            this.reader = reader;
            this.te = this.termsEnum();
        }

        @Override
        public long nextOrd() {
            while (this.bufferUpto == this.bufferLength) {
                if (this.bufferLength < this.buffer.length) {
                    return -1L;
                }
                this.bufferLength = this.read(this.buffer);
                this.bufferUpto = 0;
            }
            return this.buffer[this.bufferUpto++];
        }

        int read(int[] buffer) {
            int bufferUpto;
            block6: {
                bufferUpto = 0;
                if (this.arr == null) {
                    int code = this.upto;
                    int delta = 0;
                    while (true) {
                        delta = delta << 7 | code & 0x7F;
                        if ((code & 0x80) == 0) {
                            if (delta == 0) break block6;
                            this.tnum += delta - 2;
                            buffer[bufferUpto++] = DocTermOrds.this.ordBase + this.tnum;
                            delta = 0;
                        }
                        code >>>= 8;
                    }
                }
                do {
                    byte b;
                    int delta = 0;
                    do {
                        b = this.arr[this.upto++];
                        delta = delta << 7 | b & 0x7F;
                    } while ((b & 0x80) != 0);
                    if (delta == 0) break;
                    this.tnum += delta - 2;
                    buffer[bufferUpto++] = DocTermOrds.this.ordBase + this.tnum;
                } while (bufferUpto != buffer.length);
            }
            return bufferUpto;
        }

        @Override
        public void setDocument(int docID) {
            this.tnum = 0;
            int code = DocTermOrds.this.index[docID];
            if ((code & 0xFF) == 1) {
                this.upto = code >>> 8;
                int whichArray = docID >>> 16 & 0xFF;
                this.arr = DocTermOrds.this.tnums[whichArray];
            } else {
                this.arr = null;
                this.upto = code;
            }
            this.bufferUpto = 0;
            this.bufferLength = this.read(this.buffer);
        }

        @Override
        public void lookupOrd(long ord, BytesRef result) {
            BytesRef ref = null;
            try {
                ref = DocTermOrds.this.lookupTerm(this.te, (int)ord);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            result.bytes = ref.bytes;
            result.offset = ref.offset;
            result.length = ref.length;
        }

        @Override
        public long getValueCount() {
            return DocTermOrds.this.numTerms();
        }

        @Override
        public long lookupTerm(BytesRef key) {
            try {
                if (this.te.seekCeil(key) == TermsEnum.SeekStatus.FOUND) {
                    return this.te.ord();
                }
                return -this.te.ord() - 1L;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public TermsEnum termsEnum() {
            try {
                return DocTermOrds.this.getOrdTermsEnum(this.reader);
            }
            catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }

    private final class OrdWrappedTermsEnum
    extends TermsEnum {
        private final TermsEnum termsEnum;
        private BytesRef term;
        private long ord;

        public OrdWrappedTermsEnum(AtomicReader reader) throws IOException {
            this.ord = -DocTermOrds.this.indexInterval - 1;
            assert (DocTermOrds.this.indexedTermsArray != null);
            this.termsEnum = reader.fields().terms(DocTermOrds.this.field).iterator(null);
        }

        @Override
        public Comparator<BytesRef> getComparator() {
            return this.termsEnum.getComparator();
        }

        @Override
        public DocsEnum docs(Bits liveDocs, DocsEnum reuse, int flags) throws IOException {
            return this.termsEnum.docs(liveDocs, reuse, flags);
        }

        @Override
        public DocsAndPositionsEnum docsAndPositions(Bits liveDocs, DocsAndPositionsEnum reuse, int flags) throws IOException {
            return this.termsEnum.docsAndPositions(liveDocs, reuse, flags);
        }

        @Override
        public BytesRef term() {
            return this.term;
        }

        @Override
        public BytesRef next() throws IOException {
            if (++this.ord < 0L) {
                this.ord = 0L;
            }
            if (this.termsEnum.next() == null) {
                this.term = null;
                return null;
            }
            return this.setTerm();
        }

        @Override
        public int docFreq() throws IOException {
            return this.termsEnum.docFreq();
        }

        @Override
        public long totalTermFreq() throws IOException {
            return this.termsEnum.totalTermFreq();
        }

        @Override
        public long ord() {
            return (long)DocTermOrds.this.ordBase + this.ord;
        }

        @Override
        public TermsEnum.SeekStatus seekCeil(BytesRef target, boolean useCache) throws IOException {
            if (this.term != null && this.term.equals(target)) {
                return TermsEnum.SeekStatus.FOUND;
            }
            int startIdx = Arrays.binarySearch(DocTermOrds.this.indexedTermsArray, target);
            if (startIdx >= 0) {
                TermsEnum.SeekStatus seekStatus = this.termsEnum.seekCeil(target);
                assert (seekStatus == TermsEnum.SeekStatus.FOUND);
                this.ord = startIdx << DocTermOrds.this.indexIntervalBits;
                this.setTerm();
                assert (this.term != null);
                return TermsEnum.SeekStatus.FOUND;
            }
            if ((startIdx = -startIdx - 1) == 0) {
                TermsEnum.SeekStatus seekStatus = this.termsEnum.seekCeil(target);
                assert (seekStatus == TermsEnum.SeekStatus.NOT_FOUND);
                this.ord = 0L;
                this.setTerm();
                assert (this.term != null);
                return TermsEnum.SeekStatus.NOT_FOUND;
            }
            if (this.ord >> DocTermOrds.this.indexIntervalBits != (long)(--startIdx) || this.term == null || this.term.compareTo(target) > 0) {
                TermsEnum.SeekStatus seekStatus = this.termsEnum.seekCeil(DocTermOrds.this.indexedTermsArray[startIdx]);
                assert (seekStatus == TermsEnum.SeekStatus.FOUND);
                this.ord = startIdx << DocTermOrds.this.indexIntervalBits;
                this.setTerm();
                assert (this.term != null);
            }
            while (this.term != null && this.term.compareTo(target) < 0) {
                this.next();
            }
            if (this.term == null) {
                return TermsEnum.SeekStatus.END;
            }
            if (this.term.compareTo(target) == 0) {
                return TermsEnum.SeekStatus.FOUND;
            }
            return TermsEnum.SeekStatus.NOT_FOUND;
        }

        @Override
        public void seekExact(long targetOrd) throws IOException {
            int delta = (int)(targetOrd - (long)DocTermOrds.this.ordBase - this.ord);
            if (delta < 0 || delta > DocTermOrds.this.indexInterval) {
                int idx = (int)(targetOrd >>> DocTermOrds.this.indexIntervalBits);
                BytesRef base = DocTermOrds.this.indexedTermsArray[idx];
                this.ord = idx << DocTermOrds.this.indexIntervalBits;
                delta = (int)(targetOrd - this.ord);
                TermsEnum.SeekStatus seekStatus = this.termsEnum.seekCeil(base, true);
                assert (seekStatus == TermsEnum.SeekStatus.FOUND);
            }
            while (--delta >= 0) {
                BytesRef br = this.termsEnum.next();
                if (br == null) {
                    assert (false);
                    return;
                }
                ++this.ord;
            }
            this.setTerm();
            assert (this.term != null);
        }

        private BytesRef setTerm() throws IOException {
            this.term = this.termsEnum.term();
            if (DocTermOrds.this.prefix != null && !StringHelper.startsWith(this.term, DocTermOrds.this.prefix)) {
                this.term = null;
            }
            return this.term;
        }
    }
}

