/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.codecs.BlockTermState;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.PostingsReaderBase;
import org.apache.lucene.codecs.lucene40.Lucene40SkipListReader;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.TermState;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;

@Deprecated
public class Lucene40PostingsReader
extends PostingsReaderBase {
    static final String TERMS_CODEC = "Lucene40PostingsWriterTerms";
    static final String FRQ_CODEC = "Lucene40PostingsWriterFrq";
    static final String PRX_CODEC = "Lucene40PostingsWriterPrx";
    static final int VERSION_START = 0;
    static final int VERSION_LONG_SKIP = 1;
    static final int VERSION_CURRENT = 1;
    private final IndexInput freqIn;
    private final IndexInput proxIn;
    int skipInterval;
    int maxSkipLevels;
    int skipMinimum;
    static final int BUFFERSIZE = 64;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Lucene40PostingsReader(Directory dir, FieldInfos fieldInfos, SegmentInfo segmentInfo, IOContext ioContext, String segmentSuffix) throws IOException {
        boolean success = false;
        IndexInput freqIn = null;
        IndexInput proxIn = null;
        try {
            freqIn = dir.openInput(IndexFileNames.segmentFileName(segmentInfo.name, segmentSuffix, "frq"), ioContext);
            CodecUtil.checkHeader(freqIn, FRQ_CODEC, 0, 1);
            if (fieldInfos.hasProx()) {
                proxIn = dir.openInput(IndexFileNames.segmentFileName(segmentInfo.name, segmentSuffix, "prx"), ioContext);
                CodecUtil.checkHeader(proxIn, PRX_CODEC, 0, 1);
            } else {
                proxIn = null;
            }
            this.freqIn = freqIn;
            this.proxIn = proxIn;
            return;
        }
        catch (Throwable throwable) {
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(freqIn, proxIn);
            throw throwable;
        }
    }

    @Override
    public void init(IndexInput termsIn) throws IOException {
        CodecUtil.checkHeader(termsIn, TERMS_CODEC, 0, 1);
        this.skipInterval = termsIn.readInt();
        this.maxSkipLevels = termsIn.readInt();
        this.skipMinimum = termsIn.readInt();
    }

    @Override
    public BlockTermState newTermState() {
        return new StandardTermState();
    }

    @Override
    public void close() throws IOException {
        try {
            if (this.freqIn != null) {
                this.freqIn.close();
            }
        }
        finally {
            if (this.proxIn != null) {
                this.proxIn.close();
            }
        }
    }

    @Override
    public void readTermsBlock(IndexInput termsIn, FieldInfo fieldInfo, BlockTermState _termState) throws IOException {
        StandardTermState termState = (StandardTermState)_termState;
        int len = termsIn.readVInt();
        if (termState.bytes == null) {
            termState.bytes = new byte[ArrayUtil.oversize(len, 1)];
            termState.bytesReader = new ByteArrayDataInput();
        } else if (termState.bytes.length < len) {
            termState.bytes = new byte[ArrayUtil.oversize(len, 1)];
        }
        termsIn.readBytes(termState.bytes, 0, len);
        termState.bytesReader.reset(termState.bytes, 0, len);
    }

    @Override
    public void nextTerm(FieldInfo fieldInfo, BlockTermState _termState) throws IOException {
        boolean isFirstTerm;
        StandardTermState termState = (StandardTermState)_termState;
        boolean bl = isFirstTerm = termState.termBlockOrd == 0;
        termState.freqOffset = isFirstTerm ? termState.bytesReader.readVLong() : (termState.freqOffset += termState.bytesReader.readVLong());
        assert (termState.freqOffset < this.freqIn.length());
        if (termState.docFreq >= this.skipMinimum) {
            termState.skipOffset = termState.bytesReader.readVLong();
            assert (termState.freqOffset + termState.skipOffset < this.freqIn.length());
        }
        if (fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0) {
            termState.proxOffset = isFirstTerm ? termState.bytesReader.readVLong() : (termState.proxOffset += termState.bytesReader.readVLong());
        }
    }

    @Override
    public DocsEnum docs(FieldInfo fieldInfo, BlockTermState termState, Bits liveDocs, DocsEnum reuse, int flags) throws IOException {
        if (this.canReuse(reuse, liveDocs)) {
            return ((SegmentDocsEnumBase)reuse).reset(fieldInfo, (StandardTermState)termState);
        }
        return this.newDocsEnum(liveDocs, fieldInfo, (StandardTermState)termState);
    }

    private boolean canReuse(DocsEnum reuse, Bits liveDocs) {
        if (reuse != null && reuse instanceof SegmentDocsEnumBase) {
            SegmentDocsEnumBase docsEnum = (SegmentDocsEnumBase)reuse;
            if (docsEnum.startFreqIn == this.freqIn) {
                return liveDocs == docsEnum.liveDocs;
            }
        }
        return false;
    }

    private DocsEnum newDocsEnum(Bits liveDocs, FieldInfo fieldInfo, StandardTermState termState) throws IOException {
        if (liveDocs == null) {
            return new AllDocsSegmentDocsEnum(this.freqIn).reset(fieldInfo, termState);
        }
        return new LiveDocsSegmentDocsEnum(this.freqIn, liveDocs).reset(fieldInfo, termState);
    }

    @Override
    public DocsAndPositionsEnum docsAndPositions(FieldInfo fieldInfo, BlockTermState termState, Bits liveDocs, DocsAndPositionsEnum reuse, int flags) throws IOException {
        SegmentDocsAndPositionsEnum docsEnum;
        boolean hasOffsets;
        boolean bl = hasOffsets = fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
        if (fieldInfo.hasPayloads() || hasOffsets) {
            SegmentFullPositionsEnum docsEnum2;
            if (reuse == null || !(reuse instanceof SegmentFullPositionsEnum)) {
                docsEnum2 = new SegmentFullPositionsEnum(this.freqIn, this.proxIn);
            } else {
                docsEnum2 = (SegmentFullPositionsEnum)reuse;
                if (docsEnum2.startFreqIn != this.freqIn) {
                    docsEnum2 = new SegmentFullPositionsEnum(this.freqIn, this.proxIn);
                }
            }
            return docsEnum2.reset(fieldInfo, (StandardTermState)termState, liveDocs);
        }
        if (reuse == null || !(reuse instanceof SegmentDocsAndPositionsEnum)) {
            docsEnum = new SegmentDocsAndPositionsEnum(this.freqIn, this.proxIn);
        } else {
            docsEnum = (SegmentDocsAndPositionsEnum)reuse;
            if (docsEnum.startFreqIn != this.freqIn) {
                docsEnum = new SegmentDocsAndPositionsEnum(this.freqIn, this.proxIn);
            }
        }
        return docsEnum.reset(fieldInfo, (StandardTermState)termState, liveDocs);
    }

    private class SegmentFullPositionsEnum
    extends DocsAndPositionsEnum {
        final IndexInput startFreqIn;
        private final IndexInput freqIn;
        private final IndexInput proxIn;
        int limit;
        int ord;
        int doc = -1;
        int accum;
        int freq;
        int position;
        Bits liveDocs;
        long freqOffset;
        long skipOffset;
        long proxOffset;
        int posPendingCount;
        int payloadLength;
        boolean payloadPending;
        boolean skipped;
        Lucene40SkipListReader skipper;
        private BytesRef payload;
        private long lazyProxPointer;
        boolean storePayloads;
        boolean storeOffsets;
        int offsetLength;
        int startOffset;

        public SegmentFullPositionsEnum(IndexInput freqIn, IndexInput proxIn) {
            this.startFreqIn = freqIn;
            this.freqIn = freqIn.clone();
            this.proxIn = proxIn.clone();
        }

        public SegmentFullPositionsEnum reset(FieldInfo fieldInfo, StandardTermState termState, Bits liveDocs) throws IOException {
            this.storeOffsets = fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
            this.storePayloads = fieldInfo.hasPayloads();
            assert (fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0);
            assert (this.storePayloads || this.storeOffsets);
            if (this.payload == null) {
                this.payload = new BytesRef();
                this.payload.bytes = new byte[1];
            }
            this.liveDocs = liveDocs;
            this.freqIn.seek(termState.freqOffset);
            this.lazyProxPointer = termState.proxOffset;
            this.limit = termState.docFreq;
            this.ord = 0;
            this.doc = -1;
            this.accum = 0;
            this.position = 0;
            this.startOffset = 0;
            this.skipped = false;
            this.posPendingCount = 0;
            this.payloadPending = false;
            this.freqOffset = termState.freqOffset;
            this.proxOffset = termState.proxOffset;
            this.skipOffset = termState.skipOffset;
            return this;
        }

        @Override
        public int nextDoc() throws IOException {
            do {
                if (this.ord == this.limit) {
                    this.doc = Integer.MAX_VALUE;
                    return Integer.MAX_VALUE;
                }
                ++this.ord;
                int code = this.freqIn.readVInt();
                this.accum += code >>> 1;
                this.freq = (code & 1) != 0 ? 1 : this.freqIn.readVInt();
                this.posPendingCount += this.freq;
            } while (this.liveDocs != null && !this.liveDocs.get(this.accum));
            this.position = 0;
            this.startOffset = 0;
            this.doc = this.accum;
            return this.doc;
        }

        @Override
        public int docID() {
            return this.doc;
        }

        @Override
        public int freq() throws IOException {
            return this.freq;
        }

        @Override
        public int advance(int target) throws IOException {
            if (target - Lucene40PostingsReader.this.skipInterval >= this.doc && this.limit >= Lucene40PostingsReader.this.skipMinimum) {
                int newOrd;
                if (this.skipper == null) {
                    this.skipper = new Lucene40SkipListReader(this.freqIn.clone(), Lucene40PostingsReader.this.maxSkipLevels, Lucene40PostingsReader.this.skipInterval);
                }
                if (!this.skipped) {
                    this.skipper.init(this.freqOffset + this.skipOffset, this.freqOffset, this.proxOffset, this.limit, this.storePayloads, this.storeOffsets);
                    this.skipped = true;
                }
                if ((newOrd = this.skipper.skipTo(target)) > this.ord) {
                    this.ord = newOrd;
                    this.doc = this.accum = this.skipper.getDoc();
                    this.freqIn.seek(this.skipper.getFreqPointer());
                    this.lazyProxPointer = this.skipper.getProxPointer();
                    this.posPendingCount = 0;
                    this.position = 0;
                    this.startOffset = 0;
                    this.payloadPending = false;
                    this.payloadLength = this.skipper.getPayloadLength();
                    this.offsetLength = this.skipper.getOffsetLength();
                }
            }
            do {
                this.nextDoc();
            } while (target > this.doc);
            return this.doc;
        }

        @Override
        public int nextPosition() throws IOException {
            int code;
            if (this.lazyProxPointer != -1L) {
                this.proxIn.seek(this.lazyProxPointer);
                this.lazyProxPointer = -1L;
            }
            if (this.payloadPending && this.payloadLength > 0) {
                this.proxIn.seek(this.proxIn.getFilePointer() + (long)this.payloadLength);
                this.payloadPending = false;
            }
            while (this.posPendingCount > this.freq) {
                code = this.proxIn.readVInt();
                if (this.storePayloads) {
                    if ((code & 1) != 0) {
                        this.payloadLength = this.proxIn.readVInt();
                        assert (this.payloadLength >= 0);
                    }
                    assert (this.payloadLength != -1);
                }
                if (this.storeOffsets && (this.proxIn.readVInt() & 1) != 0) {
                    this.offsetLength = this.proxIn.readVInt();
                }
                if (this.storePayloads) {
                    this.proxIn.seek(this.proxIn.getFilePointer() + (long)this.payloadLength);
                }
                --this.posPendingCount;
                this.position = 0;
                this.startOffset = 0;
                this.payloadPending = false;
            }
            if (this.payloadPending && this.payloadLength > 0) {
                this.proxIn.seek(this.proxIn.getFilePointer() + (long)this.payloadLength);
            }
            code = this.proxIn.readVInt();
            if (this.storePayloads) {
                if ((code & 1) != 0) {
                    this.payloadLength = this.proxIn.readVInt();
                    assert (this.payloadLength >= 0);
                }
                assert (this.payloadLength != -1);
                this.payloadPending = true;
                code >>>= 1;
            }
            this.position += code;
            if (this.storeOffsets) {
                int offsetCode = this.proxIn.readVInt();
                if ((offsetCode & 1) != 0) {
                    this.offsetLength = this.proxIn.readVInt();
                }
                this.startOffset += offsetCode >>> 1;
            }
            --this.posPendingCount;
            assert (this.posPendingCount >= 0) : "nextPosition() was called too many times (more than freq() times) posPendingCount=" + this.posPendingCount;
            return this.position;
        }

        @Override
        public int startOffset() throws IOException {
            return this.storeOffsets ? this.startOffset : -1;
        }

        @Override
        public int endOffset() throws IOException {
            return this.storeOffsets ? this.startOffset + this.offsetLength : -1;
        }

        @Override
        public BytesRef getPayload() throws IOException {
            if (this.storePayloads) {
                if (this.payloadLength <= 0) {
                    return null;
                }
                assert (this.lazyProxPointer == -1L);
                assert (this.posPendingCount < this.freq);
                if (this.payloadPending) {
                    if (this.payloadLength > this.payload.bytes.length) {
                        this.payload.grow(this.payloadLength);
                    }
                    this.proxIn.readBytes(this.payload.bytes, 0, this.payloadLength);
                    this.payload.length = this.payloadLength;
                    this.payloadPending = false;
                }
                return this.payload;
            }
            return null;
        }

        @Override
        public long cost() {
            return this.limit;
        }
    }

    private final class SegmentDocsAndPositionsEnum
    extends DocsAndPositionsEnum {
        final IndexInput startFreqIn;
        private final IndexInput freqIn;
        private final IndexInput proxIn;
        int limit;
        int ord;
        int doc = -1;
        int accum;
        int freq;
        int position;
        Bits liveDocs;
        long freqOffset;
        long skipOffset;
        long proxOffset;
        int posPendingCount;
        boolean skipped;
        Lucene40SkipListReader skipper;
        private long lazyProxPointer;

        public SegmentDocsAndPositionsEnum(IndexInput freqIn, IndexInput proxIn) {
            this.startFreqIn = freqIn;
            this.freqIn = freqIn.clone();
            this.proxIn = proxIn.clone();
        }

        public SegmentDocsAndPositionsEnum reset(FieldInfo fieldInfo, StandardTermState termState, Bits liveDocs) throws IOException {
            assert (fieldInfo.getIndexOptions() == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
            assert (!fieldInfo.hasPayloads());
            this.liveDocs = liveDocs;
            this.freqIn.seek(termState.freqOffset);
            this.lazyProxPointer = termState.proxOffset;
            this.limit = termState.docFreq;
            assert (this.limit > 0);
            this.ord = 0;
            this.doc = -1;
            this.accum = 0;
            this.position = 0;
            this.skipped = false;
            this.posPendingCount = 0;
            this.freqOffset = termState.freqOffset;
            this.proxOffset = termState.proxOffset;
            this.skipOffset = termState.skipOffset;
            return this;
        }

        @Override
        public int nextDoc() throws IOException {
            do {
                if (this.ord == this.limit) {
                    this.doc = Integer.MAX_VALUE;
                    return Integer.MAX_VALUE;
                }
                ++this.ord;
                int code = this.freqIn.readVInt();
                this.accum += code >>> 1;
                this.freq = (code & 1) != 0 ? 1 : this.freqIn.readVInt();
                this.posPendingCount += this.freq;
            } while (this.liveDocs != null && !this.liveDocs.get(this.accum));
            this.position = 0;
            this.doc = this.accum;
            return this.doc;
        }

        @Override
        public int docID() {
            return this.doc;
        }

        @Override
        public int freq() {
            return this.freq;
        }

        @Override
        public int advance(int target) throws IOException {
            if (target - Lucene40PostingsReader.this.skipInterval >= this.doc && this.limit >= Lucene40PostingsReader.this.skipMinimum) {
                int newOrd;
                if (this.skipper == null) {
                    this.skipper = new Lucene40SkipListReader(this.freqIn.clone(), Lucene40PostingsReader.this.maxSkipLevels, Lucene40PostingsReader.this.skipInterval);
                }
                if (!this.skipped) {
                    this.skipper.init(this.freqOffset + this.skipOffset, this.freqOffset, this.proxOffset, this.limit, false, false);
                    this.skipped = true;
                }
                if ((newOrd = this.skipper.skipTo(target)) > this.ord) {
                    this.ord = newOrd;
                    this.doc = this.accum = this.skipper.getDoc();
                    this.freqIn.seek(this.skipper.getFreqPointer());
                    this.lazyProxPointer = this.skipper.getProxPointer();
                    this.posPendingCount = 0;
                    this.position = 0;
                }
            }
            do {
                this.nextDoc();
            } while (target > this.doc);
            return this.doc;
        }

        @Override
        public int nextPosition() throws IOException {
            if (this.lazyProxPointer != -1L) {
                this.proxIn.seek(this.lazyProxPointer);
                this.lazyProxPointer = -1L;
            }
            if (this.posPendingCount > this.freq) {
                this.position = 0;
                while (this.posPendingCount != this.freq) {
                    if ((this.proxIn.readByte() & 0x80) != 0) continue;
                    --this.posPendingCount;
                }
            }
            this.position += this.proxIn.readVInt();
            --this.posPendingCount;
            assert (this.posPendingCount >= 0) : "nextPosition() was called too many times (more than freq() times) posPendingCount=" + this.posPendingCount;
            return this.position;
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
        public BytesRef getPayload() throws IOException {
            return null;
        }

        @Override
        public long cost() {
            return this.limit;
        }
    }

    private final class LiveDocsSegmentDocsEnum
    extends SegmentDocsEnumBase {
        LiveDocsSegmentDocsEnum(IndexInput startFreqIn, Bits liveDocs) {
            super(startFreqIn, liveDocs);
            assert (liveDocs != null);
        }

        @Override
        public final int nextDoc() throws IOException {
            Bits liveDocs = this.liveDocs;
            for (int i = this.start + 1; i < this.count; ++i) {
                int d = this.docs[i];
                if (!liveDocs.get(d)) continue;
                this.start = i;
                this.freq = this.freqs[i];
                this.doc = d;
                return this.doc;
            }
            this.start = this.count;
            this.doc = this.refill();
            return this.doc;
        }

        @Override
        protected final int linearScan(int scanTo) throws IOException {
            int[] docs = this.docs;
            int upTo = this.count;
            Bits liveDocs = this.liveDocs;
            for (int i = this.start; i < upTo; ++i) {
                int d = docs[i];
                if (scanTo > d || !liveDocs.get(d)) continue;
                this.start = i;
                this.freq = this.freqs[i];
                this.doc = docs[i];
                return this.doc;
            }
            this.doc = this.refill();
            return this.doc;
        }

        @Override
        protected int scanTo(int target) throws IOException {
            int docAcc = this.accum;
            int frq = 1;
            IndexInput freqIn = this.freqIn;
            boolean omitTF = this.indexOmitsTF;
            int loopLimit = this.limit;
            Bits liveDocs = this.liveDocs;
            for (int i = this.ord; i < loopLimit; ++i) {
                int code = freqIn.readVInt();
                if (omitTF) {
                    docAcc += code;
                } else {
                    docAcc += code >>> 1;
                    frq = this.readFreq(freqIn, code);
                }
                if (docAcc < target || !liveDocs.get(docAcc)) continue;
                this.freq = frq;
                this.ord = i + 1;
                this.accum = docAcc;
                return this.accum;
            }
            this.ord = this.limit;
            this.freq = frq;
            this.accum = docAcc;
            return Integer.MAX_VALUE;
        }

        @Override
        protected final int nextUnreadDoc() throws IOException {
            int docAcc = this.accum;
            int frq = 1;
            IndexInput freqIn = this.freqIn;
            boolean omitTF = this.indexOmitsTF;
            int loopLimit = this.limit;
            Bits liveDocs = this.liveDocs;
            for (int i = this.ord; i < loopLimit; ++i) {
                int code = freqIn.readVInt();
                if (omitTF) {
                    docAcc += code;
                } else {
                    docAcc += code >>> 1;
                    frq = this.readFreq(freqIn, code);
                }
                if (!liveDocs.get(docAcc)) continue;
                this.freq = frq;
                this.ord = i + 1;
                this.accum = docAcc;
                return this.accum;
            }
            this.ord = this.limit;
            this.freq = frq;
            this.accum = docAcc;
            return Integer.MAX_VALUE;
        }
    }

    private final class AllDocsSegmentDocsEnum
    extends SegmentDocsEnumBase {
        AllDocsSegmentDocsEnum(IndexInput startFreqIn) {
            super(startFreqIn, null);
            assert (this.liveDocs == null);
        }

        @Override
        public final int nextDoc() throws IOException {
            if (++this.start < this.count) {
                this.freq = this.freqs[this.start];
                this.doc = this.docs[this.start];
                return this.doc;
            }
            this.doc = this.refill();
            return this.doc;
        }

        @Override
        protected final int linearScan(int scanTo) throws IOException {
            int[] docs = this.docs;
            int upTo = this.count;
            for (int i = this.start; i < upTo; ++i) {
                int d = docs[i];
                if (scanTo > d) continue;
                this.start = i;
                this.freq = this.freqs[i];
                this.doc = docs[i];
                return this.doc;
            }
            this.doc = this.refill();
            return this.doc;
        }

        @Override
        protected int scanTo(int target) throws IOException {
            int docAcc = this.accum;
            int frq = 1;
            IndexInput freqIn = this.freqIn;
            boolean omitTF = this.indexOmitsTF;
            int loopLimit = this.limit;
            for (int i = this.ord; i < loopLimit; ++i) {
                int code = freqIn.readVInt();
                if (omitTF) {
                    docAcc += code;
                } else {
                    docAcc += code >>> 1;
                    frq = this.readFreq(freqIn, code);
                }
                if (docAcc < target) continue;
                this.freq = frq;
                this.ord = i + 1;
                this.accum = docAcc;
                return this.accum;
            }
            this.ord = this.limit;
            this.freq = frq;
            this.accum = docAcc;
            return Integer.MAX_VALUE;
        }

        @Override
        protected final int nextUnreadDoc() throws IOException {
            if (this.ord++ < this.limit) {
                int code = this.freqIn.readVInt();
                if (this.indexOmitsTF) {
                    this.accum += code;
                } else {
                    this.accum += code >>> 1;
                    this.freq = this.readFreq(this.freqIn, code);
                }
                return this.accum;
            }
            return Integer.MAX_VALUE;
        }
    }

    private abstract class SegmentDocsEnumBase
    extends DocsEnum {
        protected final int[] docs = new int[64];
        protected final int[] freqs = new int[64];
        final IndexInput freqIn;
        final IndexInput startFreqIn;
        Lucene40SkipListReader skipper;
        protected boolean indexOmitsTF;
        protected boolean storePayloads;
        protected boolean storeOffsets;
        protected int limit;
        protected int ord;
        protected int doc;
        protected int accum;
        protected int freq;
        protected int maxBufferedDocId;
        protected int start;
        protected int count;
        protected long freqOffset;
        protected long skipOffset;
        protected boolean skipped;
        protected final Bits liveDocs;

        SegmentDocsEnumBase(IndexInput startFreqIn, Bits liveDocs) {
            this.startFreqIn = startFreqIn;
            this.freqIn = startFreqIn.clone();
            this.liveDocs = liveDocs;
        }

        DocsEnum reset(FieldInfo fieldInfo, StandardTermState termState) throws IOException {
            this.indexOmitsTF = fieldInfo.getIndexOptions() == FieldInfo.IndexOptions.DOCS_ONLY;
            this.storePayloads = fieldInfo.hasPayloads();
            this.storeOffsets = fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
            this.freqOffset = termState.freqOffset;
            this.skipOffset = termState.skipOffset;
            this.freqIn.seek(termState.freqOffset);
            this.limit = termState.docFreq;
            assert (this.limit > 0);
            this.ord = 0;
            this.doc = -1;
            this.accum = 0;
            this.skipped = false;
            this.start = -1;
            this.count = 0;
            this.freq = 1;
            if (this.indexOmitsTF) {
                Arrays.fill(this.freqs, 1);
            }
            this.maxBufferedDocId = -1;
            return this;
        }

        @Override
        public final int freq() {
            return this.freq;
        }

        @Override
        public final int docID() {
            return this.doc;
        }

        @Override
        public final int advance(int target) throws IOException {
            if (++this.start < this.count && this.maxBufferedDocId >= target) {
                if (this.count - this.start > 32) {
                    this.start = this.binarySearch(this.count - 1, this.start, target, this.docs);
                    return this.nextDoc();
                }
                return this.linearScan(target);
            }
            this.start = this.count;
            this.doc = this.skipTo(target);
            return this.doc;
        }

        private final int binarySearch(int hi, int low, int target, int[] docs) {
            while (low <= hi) {
                int mid = hi + low >>> 1;
                int doc = docs[mid];
                if (doc < target) {
                    low = mid + 1;
                    continue;
                }
                if (doc > target) {
                    hi = mid - 1;
                    continue;
                }
                low = mid;
                break;
            }
            return low - 1;
        }

        final int readFreq(IndexInput freqIn, int code) throws IOException {
            if ((code & 1) != 0) {
                return 1;
            }
            return freqIn.readVInt();
        }

        protected abstract int linearScan(int var1) throws IOException;

        protected abstract int scanTo(int var1) throws IOException;

        protected final int refill() throws IOException {
            int doc = this.nextUnreadDoc();
            this.count = 0;
            this.start = -1;
            if (doc == Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            int numDocs = Math.min(this.docs.length, this.limit - this.ord);
            this.ord += numDocs;
            this.count = this.indexOmitsTF ? this.fillDocs(numDocs) : this.fillDocsAndFreqs(numDocs);
            this.maxBufferedDocId = this.count > 0 ? this.docs[this.count - 1] : Integer.MAX_VALUE;
            return doc;
        }

        protected abstract int nextUnreadDoc() throws IOException;

        private final int fillDocs(int size) throws IOException {
            IndexInput freqIn = this.freqIn;
            int[] docs = this.docs;
            int docAc = this.accum;
            for (int i = 0; i < size; ++i) {
                docs[i] = docAc += freqIn.readVInt();
            }
            this.accum = docAc;
            return size;
        }

        private final int fillDocsAndFreqs(int size) throws IOException {
            IndexInput freqIn = this.freqIn;
            int[] docs = this.docs;
            int[] freqs = this.freqs;
            int docAc = this.accum;
            for (int i = 0; i < size; ++i) {
                int code = freqIn.readVInt();
                freqs[i] = this.readFreq(freqIn, code);
                docs[i] = docAc += code >>> 1;
            }
            this.accum = docAc;
            return size;
        }

        private final int skipTo(int target) throws IOException {
            if (target - Lucene40PostingsReader.this.skipInterval >= this.accum && this.limit >= Lucene40PostingsReader.this.skipMinimum) {
                int newOrd;
                if (this.skipper == null) {
                    this.skipper = new Lucene40SkipListReader(this.freqIn.clone(), Lucene40PostingsReader.this.maxSkipLevels, Lucene40PostingsReader.this.skipInterval);
                }
                if (!this.skipped) {
                    this.skipper.init(this.freqOffset + this.skipOffset, this.freqOffset, 0L, this.limit, this.storePayloads, this.storeOffsets);
                    this.skipped = true;
                }
                if ((newOrd = this.skipper.skipTo(target)) > this.ord) {
                    this.ord = newOrd;
                    this.accum = this.skipper.getDoc();
                    this.freqIn.seek(this.skipper.getFreqPointer());
                }
            }
            return this.scanTo(target);
        }

        @Override
        public long cost() {
            return this.limit;
        }
    }

    private static final class StandardTermState
    extends BlockTermState {
        long freqOffset;
        long proxOffset;
        long skipOffset;
        ByteArrayDataInput bytesReader;
        byte[] bytes;

        private StandardTermState() {
        }

        @Override
        public StandardTermState clone() {
            StandardTermState other = new StandardTermState();
            other.copyFrom(this);
            return other;
        }

        @Override
        public void copyFrom(TermState _other) {
            super.copyFrom(_other);
            StandardTermState other = (StandardTermState)_other;
            this.freqOffset = other.freqOffset;
            this.proxOffset = other.proxOffset;
            this.skipOffset = other.skipOffset;
        }

        @Override
        public String toString() {
            return super.toString() + " freqFP=" + this.freqOffset + " proxFP=" + this.proxOffset + " skipOffset=" + this.skipOffset;
        }
    }
}

