/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.BinaryDocValues
 *  org.apache.lucene.index.DocsAndPositionsEnum
 *  org.apache.lucene.index.DocsEnum
 *  org.apache.lucene.index.FieldInfo
 *  org.apache.lucene.index.FieldInfo$IndexOptions
 *  org.apache.lucene.index.FieldInfos
 *  org.apache.lucene.index.FieldInvertState
 *  org.apache.lucene.index.Fields
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.NumericDocValues
 *  org.apache.lucene.index.OrdTermState
 *  org.apache.lucene.index.SortedDocValues
 *  org.apache.lucene.index.SortedSetDocValues
 *  org.apache.lucene.index.StoredFieldVisitor
 *  org.apache.lucene.index.TermState
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.index.TermsEnum$SeekStatus
 *  org.apache.lucene.search.Collector
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.Scorer
 *  org.apache.lucene.search.similarities.Similarity
 *  org.apache.lucene.util.ArrayUtil
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.ByteBlockPool
 *  org.apache.lucene.util.ByteBlockPool$Allocator
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.BytesRefHash
 *  org.apache.lucene.util.BytesRefHash$BytesStartArray
 *  org.apache.lucene.util.BytesRefHash$DirectBytesStartArray
 *  org.apache.lucene.util.Counter
 *  org.apache.lucene.util.IntBlockPool
 *  org.apache.lucene.util.IntBlockPool$Allocator
 *  org.apache.lucene.util.IntBlockPool$SliceReader
 *  org.apache.lucene.util.IntBlockPool$SliceWriter
 *  org.apache.lucene.util.RamUsageEstimator
 *  org.apache.lucene.util.RecyclingByteBlockAllocator
 *  org.apache.lucene.util.RecyclingIntBlockAllocator
 */
package org.apache.lucene.index.memory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.OrdTermState;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.memory.MemoryIndexNormDocValues;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.IntBlockPool;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.RecyclingByteBlockAllocator;
import org.apache.lucene.util.RecyclingIntBlockAllocator;

public class MemoryIndex {
    private final HashMap<String, Info> fields = new HashMap();
    private transient Map.Entry<String, Info>[] sortedFields;
    private final boolean storeOffsets;
    private static final boolean DEBUG = false;
    private final ByteBlockPool byteBlockPool;
    private final IntBlockPool intBlockPool;
    private final IntBlockPool.SliceWriter postingsWriter;
    private HashMap<String, FieldInfo> fieldInfos = new HashMap();
    private Counter bytesUsed;
    private static final Comparator<Object> termComparator = new Comparator<Object>(){

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof Map.Entry) {
                o1 = ((Map.Entry)o1).getKey();
            }
            if (o2 instanceof Map.Entry) {
                o2 = ((Map.Entry)o2).getKey();
            }
            if (o1 == o2) {
                return 0;
            }
            return ((Comparable)o1).compareTo((Comparable)o2);
        }
    };

    public MemoryIndex() {
        this(false);
    }

    public MemoryIndex(boolean storeOffsets) {
        this(storeOffsets, 0L);
    }

    MemoryIndex(boolean storeOffsets, long maxReusedBytes) {
        this.storeOffsets = storeOffsets;
        this.bytesUsed = Counter.newCounter();
        int maxBufferedByteBlocks = (int)(maxReusedBytes / 2L / 32768L);
        int maxBufferedIntBlocks = (int)((maxReusedBytes - (long)(maxBufferedByteBlocks * 32768)) / 32768L);
        assert ((long)(maxBufferedByteBlocks * 32768 + maxBufferedIntBlocks * 8192 * 4) <= maxReusedBytes);
        this.byteBlockPool = new ByteBlockPool((ByteBlockPool.Allocator)new RecyclingByteBlockAllocator(32768, maxBufferedByteBlocks, this.bytesUsed));
        this.intBlockPool = new IntBlockPool((IntBlockPool.Allocator)new RecyclingIntBlockAllocator(8192, maxBufferedIntBlocks, this.bytesUsed));
        this.postingsWriter = new IntBlockPool.SliceWriter(this.intBlockPool);
    }

    public void addField(String fieldName, String text, Analyzer analyzer) {
        TokenStream stream;
        if (fieldName == null) {
            throw new IllegalArgumentException("fieldName must not be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }
        if (analyzer == null) {
            throw new IllegalArgumentException("analyzer must not be null");
        }
        try {
            stream = analyzer.tokenStream(fieldName, text);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        this.addField(fieldName, stream, 1.0f, analyzer.getPositionIncrementGap(fieldName));
    }

    public <T> TokenStream keywordTokenStream(final Collection<T> keywords) {
        if (keywords == null) {
            throw new IllegalArgumentException("keywords must not be null");
        }
        return new TokenStream(){
            private Iterator<T> iter;
            private int start;
            private final CharTermAttribute termAtt;
            private final OffsetAttribute offsetAtt;
            {
                this.iter = keywords.iterator();
                this.start = 0;
                this.termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
                this.offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
            }

            public boolean incrementToken() {
                if (!this.iter.hasNext()) {
                    return false;
                }
                Object obj = this.iter.next();
                if (obj == null) {
                    throw new IllegalArgumentException("keyword must not be null");
                }
                String term = obj.toString();
                this.clearAttributes();
                this.termAtt.setEmpty().append(term);
                this.offsetAtt.setOffset(this.start, this.start + this.termAtt.length());
                this.start += term.length() + 1;
                return true;
            }
        };
    }

    public void addField(String fieldName, TokenStream stream) {
        this.addField(fieldName, stream, 1.0f);
    }

    public void addField(String fieldName, TokenStream stream, float boost) {
        this.addField(fieldName, stream, boost, 0);
    }

    public void addField(String fieldName, TokenStream stream, float boost, int positionIncrementGap) {
        try {
            SliceByteStartArray sliceArray;
            BytesRefHash terms;
            if (fieldName == null) {
                throw new IllegalArgumentException("fieldName must not be null");
            }
            if (stream == null) {
                throw new IllegalArgumentException("token stream must not be null");
            }
            if (boost <= 0.0f) {
                throw new IllegalArgumentException("boost factor must be greater than 0.0");
            }
            int numTokens = 0;
            int numOverlapTokens = 0;
            int pos = -1;
            Info info = null;
            long sumTotalTermFreq = 0L;
            info = this.fields.get(fieldName);
            if (info != null) {
                numTokens = info.numTokens;
                numOverlapTokens = info.numOverlapTokens;
                pos = info.lastPosition + positionIncrementGap;
                terms = info.terms;
                boost *= info.boost;
                sliceArray = info.sliceArray;
                sumTotalTermFreq = info.sumTotalTermFreq;
            } else {
                sliceArray = new SliceByteStartArray(16);
                terms = new BytesRefHash(this.byteBlockPool, 16, (BytesRefHash.BytesStartArray)sliceArray);
            }
            if (!this.fieldInfos.containsKey(fieldName)) {
                this.fieldInfos.put(fieldName, new FieldInfo(fieldName, true, this.fieldInfos.size(), false, false, false, this.storeOffsets ? FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS : FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS, null, null, null));
            }
            TermToBytesRefAttribute termAtt = (TermToBytesRefAttribute)stream.getAttribute(TermToBytesRefAttribute.class);
            PositionIncrementAttribute posIncrAttribute = (PositionIncrementAttribute)stream.addAttribute(PositionIncrementAttribute.class);
            OffsetAttribute offsetAtt = (OffsetAttribute)stream.addAttribute(OffsetAttribute.class);
            BytesRef ref = termAtt.getBytesRef();
            stream.reset();
            while (stream.incrementToken()) {
                termAtt.fillBytesRef();
                ++numTokens;
                int posIncr = posIncrAttribute.getPositionIncrement();
                if (posIncr == 0) {
                    ++numOverlapTokens;
                }
                pos += posIncr;
                int ord = terms.add(ref);
                if (ord < 0) {
                    ord = -ord - 1;
                    this.postingsWriter.reset(sliceArray.end[ord]);
                } else {
                    sliceArray.start[ord] = this.postingsWriter.startNewSlice();
                }
                int n = ord;
                sliceArray.freq[n] = sliceArray.freq[n] + 1;
                ++sumTotalTermFreq;
                if (!this.storeOffsets) {
                    this.postingsWriter.writeInt(pos);
                } else {
                    this.postingsWriter.writeInt(pos);
                    this.postingsWriter.writeInt(offsetAtt.startOffset());
                    this.postingsWriter.writeInt(offsetAtt.endOffset());
                }
                sliceArray.end[ord] = this.postingsWriter.getCurrentOffset();
            }
            stream.end();
            if (numTokens > 0) {
                this.fields.put(fieldName, new Info(terms, sliceArray, numTokens, numOverlapTokens, boost, pos, sumTotalTermFreq));
                this.sortedFields = null;
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    public IndexSearcher createSearcher() {
        MemoryIndexReader reader = new MemoryIndexReader();
        IndexSearcher searcher = new IndexSearcher((IndexReader)reader);
        reader.setSearcher(searcher);
        return searcher;
    }

    public float search(Query query) {
        if (query == null) {
            throw new IllegalArgumentException("query must not be null");
        }
        IndexSearcher searcher = this.createSearcher();
        try {
            float score;
            final float[] scores = new float[1];
            searcher.search(query, new Collector(){
                private Scorer scorer;

                public void collect(int doc) throws IOException {
                    scores[0] = this.scorer.score();
                }

                public void setScorer(Scorer scorer) {
                    this.scorer = scorer;
                }

                public boolean acceptsDocsOutOfOrder() {
                    return true;
                }

                public void setNextReader(AtomicReaderContext context) {
                }
            });
            float f = score = scores[0];
            return f;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long getMemorySize() {
        return RamUsageEstimator.sizeOf((Object)this);
    }

    private void sortFields() {
        if (this.sortedFields == null) {
            this.sortedFields = MemoryIndex.sort(this.fields);
        }
    }

    private static <K, V> Map.Entry<K, V>[] sort(HashMap<K, V> map) {
        int size = map.size();
        Object[] entries = new Map.Entry[size];
        Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
        for (int i = 0; i < size; ++i) {
            entries[i] = iter.next();
        }
        if (size > 1) {
            ArrayUtil.introSort((Object[])entries, termComparator);
        }
        return entries;
    }

    public String toString() {
        StringBuilder result = new StringBuilder(256);
        this.sortFields();
        int sumPositions = 0;
        int sumTerms = 0;
        BytesRef spare = new BytesRef();
        for (int i = 0; i < this.sortedFields.length; ++i) {
            Map.Entry<String, Info> entry = this.sortedFields[i];
            String fieldName = entry.getKey();
            Info info = entry.getValue();
            info.sortTerms();
            result.append(fieldName + ":\n");
            SliceByteStartArray sliceArray = info.sliceArray;
            int numPositions = 0;
            IntBlockPool.SliceReader postingsReader = new IntBlockPool.SliceReader(this.intBlockPool);
            for (int j = 0; j < info.terms.size(); ++j) {
                int iters;
                int ord = info.sortedTerms[j];
                info.terms.get(ord, spare);
                int freq = sliceArray.freq[ord];
                result.append("\t'" + spare + "':" + freq + ":");
                postingsReader.reset(sliceArray.start[ord], sliceArray.end[ord]);
                result.append(" [");
                int n = iters = this.storeOffsets ? 3 : 1;
                while (!postingsReader.endOfSlice()) {
                    result.append("(");
                    for (int k = 0; k < iters; ++k) {
                        result.append(postingsReader.readInt());
                        if (k >= iters - 1) continue;
                        result.append(", ");
                    }
                    result.append(")");
                    if (postingsReader.endOfSlice()) continue;
                    result.append(",");
                }
                result.append("]");
                result.append("\n");
                numPositions += freq;
            }
            result.append("\tterms=" + info.terms.size());
            result.append(", positions=" + numPositions);
            result.append(", memory=" + RamUsageEstimator.humanReadableUnits((long)RamUsageEstimator.sizeOf((Object)info)));
            result.append("\n");
            sumPositions += numPositions;
            sumTerms += info.terms.size();
        }
        result.append("\nfields=" + this.sortedFields.length);
        result.append(", terms=" + sumTerms);
        result.append(", positions=" + sumPositions);
        result.append(", memory=" + RamUsageEstimator.humanReadableUnits((long)this.getMemorySize()));
        return result.toString();
    }

    public void reset() {
        this.fieldInfos.clear();
        this.fields.clear();
        this.sortedFields = null;
        this.byteBlockPool.reset(false, false);
        this.intBlockPool.reset(true, false);
    }

    private static final class SliceByteStartArray
    extends BytesRefHash.DirectBytesStartArray {
        int[] start;
        int[] end;
        int[] freq;

        public SliceByteStartArray(int initSize) {
            super(initSize);
        }

        public int[] init() {
            int[] ord = super.init();
            this.start = new int[ArrayUtil.oversize((int)ord.length, (int)4)];
            this.end = new int[ArrayUtil.oversize((int)ord.length, (int)4)];
            this.freq = new int[ArrayUtil.oversize((int)ord.length, (int)4)];
            assert (this.start.length >= ord.length);
            assert (this.end.length >= ord.length);
            assert (this.freq.length >= ord.length);
            return ord;
        }

        public int[] grow() {
            int[] ord = super.grow();
            if (this.start.length < ord.length) {
                this.start = ArrayUtil.grow((int[])this.start, (int)ord.length);
                this.end = ArrayUtil.grow((int[])this.end, (int)ord.length);
                this.freq = ArrayUtil.grow((int[])this.freq, (int)ord.length);
            }
            assert (this.start.length >= ord.length);
            assert (this.end.length >= ord.length);
            assert (this.freq.length >= ord.length);
            return ord;
        }

        public int[] clear() {
            this.end = null;
            this.start = null;
            return super.clear();
        }
    }

    private final class MemoryIndexReader
    extends AtomicReader {
        private IndexSearcher searcher;
        private NumericDocValues cachedNormValues;
        private String cachedFieldName;
        private Similarity cachedSimilarity;

        private MemoryIndexReader() {
        }

        private Info getInfo(String fieldName) {
            return (Info)MemoryIndex.this.fields.get(fieldName);
        }

        private Info getInfo(int pos) {
            return (Info)MemoryIndex.this.sortedFields[pos].getValue();
        }

        public Bits getLiveDocs() {
            return null;
        }

        public FieldInfos getFieldInfos() {
            return new FieldInfos(MemoryIndex.this.fieldInfos.values().toArray(new FieldInfo[MemoryIndex.this.fieldInfos.size()]));
        }

        public NumericDocValues getNumericDocValues(String field) {
            return null;
        }

        public BinaryDocValues getBinaryDocValues(String field) {
            return null;
        }

        public SortedDocValues getSortedDocValues(String field) {
            return null;
        }

        public SortedSetDocValues getSortedSetDocValues(String field) {
            return null;
        }

        public Fields fields() {
            MemoryIndex.this.sortFields();
            return new MemoryFields();
        }

        public Fields getTermVectors(int docID) {
            if (docID == 0) {
                return this.fields();
            }
            return null;
        }

        private Similarity getSimilarity() {
            if (this.searcher != null) {
                return this.searcher.getSimilarity();
            }
            return IndexSearcher.getDefaultSimilarity();
        }

        private void setSearcher(IndexSearcher searcher) {
            this.searcher = searcher;
        }

        public int numDocs() {
            return 1;
        }

        public int maxDoc() {
            return 1;
        }

        public void document(int docID, StoredFieldVisitor visitor) {
        }

        protected void doClose() {
        }

        public NumericDocValues getNormValues(String field) {
            FieldInfo fieldInfo = (FieldInfo)MemoryIndex.this.fieldInfos.get(field);
            if (fieldInfo == null || fieldInfo.omitsNorms()) {
                return null;
            }
            NumericDocValues norms = this.cachedNormValues;
            Similarity sim = this.getSimilarity();
            if (!field.equals(this.cachedFieldName) || sim != this.cachedSimilarity) {
                Info info = this.getInfo(field);
                int numTokens = info != null ? info.numTokens : 0;
                int numOverlapTokens = info != null ? info.numOverlapTokens : 0;
                float boost = info != null ? info.getBoost() : 1.0f;
                FieldInvertState invertState = new FieldInvertState(field, 0, numTokens, numOverlapTokens, 0, boost);
                long value = sim.computeNorm(invertState);
                this.cachedNormValues = norms = new MemoryIndexNormDocValues(value);
                this.cachedFieldName = field;
                this.cachedSimilarity = sim;
            }
            return norms;
        }

        private class MemoryDocsAndPositionsEnum
        extends DocsAndPositionsEnum {
            private int posUpto;
            private boolean hasNext;
            private Bits liveDocs;
            private int doc = -1;
            private IntBlockPool.SliceReader sliceReader;
            private int freq;
            private int startOffset;
            private int endOffset;

            public MemoryDocsAndPositionsEnum() {
                this.sliceReader = new IntBlockPool.SliceReader(MemoryIndex.this.intBlockPool);
            }

            public DocsAndPositionsEnum reset(Bits liveDocs, int start, int end, int freq) {
                this.liveDocs = liveDocs;
                this.sliceReader.reset(start, end);
                this.posUpto = 0;
                this.hasNext = true;
                this.doc = -1;
                this.freq = freq;
                return this;
            }

            public int docID() {
                return this.doc;
            }

            public int nextDoc() {
                if (this.hasNext && (this.liveDocs == null || this.liveDocs.get(0))) {
                    this.hasNext = false;
                    this.doc = 0;
                    return 0;
                }
                this.doc = Integer.MAX_VALUE;
                return Integer.MAX_VALUE;
            }

            public int advance(int target) throws IOException {
                return this.slowAdvance(target);
            }

            public int freq() throws IOException {
                return this.freq;
            }

            public int nextPosition() {
                assert (this.posUpto++ < this.freq);
                assert (!this.sliceReader.endOfSlice()) : " stores offsets : " + this.startOffset;
                if (MemoryIndex.this.storeOffsets) {
                    int pos = this.sliceReader.readInt();
                    this.startOffset = this.sliceReader.readInt();
                    this.endOffset = this.sliceReader.readInt();
                    return pos;
                }
                return this.sliceReader.readInt();
            }

            public int startOffset() {
                return this.startOffset;
            }

            public int endOffset() {
                return this.endOffset;
            }

            public BytesRef getPayload() {
                return null;
            }

            public long cost() {
                return 1L;
            }
        }

        private class MemoryDocsEnum
        extends DocsEnum {
            private boolean hasNext;
            private Bits liveDocs;
            private int doc = -1;
            private int freq;

            private MemoryDocsEnum() {
            }

            public DocsEnum reset(Bits liveDocs, int freq) {
                this.liveDocs = liveDocs;
                this.hasNext = true;
                this.doc = -1;
                this.freq = freq;
                return this;
            }

            public int docID() {
                return this.doc;
            }

            public int nextDoc() {
                if (this.hasNext && (this.liveDocs == null || this.liveDocs.get(0))) {
                    this.hasNext = false;
                    this.doc = 0;
                    return 0;
                }
                this.doc = Integer.MAX_VALUE;
                return Integer.MAX_VALUE;
            }

            public int advance(int target) throws IOException {
                return this.slowAdvance(target);
            }

            public int freq() throws IOException {
                return this.freq;
            }

            public long cost() {
                return 1L;
            }
        }

        private class MemoryTermsEnum
        extends TermsEnum {
            private final Info info;
            private final BytesRef br = new BytesRef();
            int termUpto = -1;

            public MemoryTermsEnum(Info info) {
                this.info = info;
                info.sortTerms();
            }

            private final int binarySearch(BytesRef b, BytesRef bytesRef, int low, int high, BytesRefHash hash, int[] ords, Comparator<BytesRef> comparator) {
                int mid = 0;
                while (low <= high) {
                    mid = low + high >>> 1;
                    hash.get(ords[mid], bytesRef);
                    int cmp = comparator.compare(bytesRef, b);
                    if (cmp < 0) {
                        low = mid + 1;
                        continue;
                    }
                    if (cmp > 0) {
                        high = mid - 1;
                        continue;
                    }
                    return mid;
                }
                assert (comparator.compare(bytesRef, b) != 0);
                return -(low + 1);
            }

            public boolean seekExact(BytesRef text, boolean useCache) {
                this.termUpto = this.binarySearch(text, this.br, 0, this.info.terms.size() - 1, this.info.terms, this.info.sortedTerms, BytesRef.getUTF8SortedAsUnicodeComparator());
                return this.termUpto >= 0;
            }

            public TermsEnum.SeekStatus seekCeil(BytesRef text, boolean useCache) {
                this.termUpto = this.binarySearch(text, this.br, 0, this.info.terms.size() - 1, this.info.terms, this.info.sortedTerms, BytesRef.getUTF8SortedAsUnicodeComparator());
                if (this.termUpto < 0) {
                    this.termUpto = -this.termUpto - 1;
                    if (this.termUpto >= this.info.terms.size()) {
                        return TermsEnum.SeekStatus.END;
                    }
                    this.info.terms.get(this.info.sortedTerms[this.termUpto], this.br);
                    return TermsEnum.SeekStatus.NOT_FOUND;
                }
                return TermsEnum.SeekStatus.FOUND;
            }

            public void seekExact(long ord) {
                assert (ord < (long)this.info.terms.size());
                this.termUpto = (int)ord;
            }

            public BytesRef next() {
                ++this.termUpto;
                if (this.termUpto >= this.info.terms.size()) {
                    return null;
                }
                this.info.terms.get(this.info.sortedTerms[this.termUpto], this.br);
                return this.br;
            }

            public BytesRef term() {
                return this.br;
            }

            public long ord() {
                return this.termUpto;
            }

            public int docFreq() {
                return 1;
            }

            public long totalTermFreq() {
                return ((Info)this.info).sliceArray.freq[this.info.sortedTerms[this.termUpto]];
            }

            public DocsEnum docs(Bits liveDocs, DocsEnum reuse, int flags) {
                if (reuse == null || !(reuse instanceof MemoryDocsEnum)) {
                    reuse = new MemoryDocsEnum();
                }
                return ((MemoryDocsEnum)reuse).reset(liveDocs, ((Info)this.info).sliceArray.freq[this.info.sortedTerms[this.termUpto]]);
            }

            public DocsAndPositionsEnum docsAndPositions(Bits liveDocs, DocsAndPositionsEnum reuse, int flags) {
                if (reuse == null || !(reuse instanceof MemoryDocsAndPositionsEnum)) {
                    reuse = new MemoryDocsAndPositionsEnum();
                }
                int ord = this.info.sortedTerms[this.termUpto];
                return ((MemoryDocsAndPositionsEnum)reuse).reset(liveDocs, ((Info)this.info).sliceArray.start[ord], ((Info)this.info).sliceArray.end[ord], ((Info)this.info).sliceArray.freq[ord]);
            }

            public Comparator<BytesRef> getComparator() {
                return BytesRef.getUTF8SortedAsUnicodeComparator();
            }

            public void seekExact(BytesRef term, TermState state) throws IOException {
                assert (state != null);
                this.seekExact(((OrdTermState)state).ord);
            }

            public TermState termState() throws IOException {
                OrdTermState ts = new OrdTermState();
                ts.ord = this.termUpto;
                return ts;
            }
        }

        private class MemoryFields
        extends Fields {
            private MemoryFields() {
            }

            public Iterator<String> iterator() {
                return new Iterator<String>(){
                    int upto = -1;

                    @Override
                    public String next() {
                        ++this.upto;
                        if (this.upto >= MemoryIndex.this.sortedFields.length) {
                            throw new NoSuchElementException();
                        }
                        return (String)MemoryIndex.this.sortedFields[this.upto].getKey();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.upto + 1 < MemoryIndex.this.sortedFields.length;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            public Terms terms(String field) {
                int i = Arrays.binarySearch(MemoryIndex.this.sortedFields, field, termComparator);
                if (i < 0) {
                    return null;
                }
                final Info info = MemoryIndexReader.this.getInfo(i);
                info.sortTerms();
                return new Terms(){

                    public TermsEnum iterator(TermsEnum reuse) {
                        return new MemoryTermsEnum(info);
                    }

                    public Comparator<BytesRef> getComparator() {
                        return BytesRef.getUTF8SortedAsUnicodeComparator();
                    }

                    public long size() {
                        return info.terms.size();
                    }

                    public long getSumTotalTermFreq() {
                        return info.getSumTotalTermFreq();
                    }

                    public long getSumDocFreq() {
                        return info.terms.size();
                    }

                    public int getDocCount() {
                        return info.terms.size() > 0 ? 1 : 0;
                    }

                    public boolean hasOffsets() {
                        return MemoryIndex.this.storeOffsets;
                    }

                    public boolean hasPositions() {
                        return true;
                    }

                    public boolean hasPayloads() {
                        return false;
                    }
                };
            }

            public int size() {
                return MemoryIndex.this.sortedFields.length;
            }
        }
    }

    private static final class Info {
        private final BytesRefHash terms;
        private final SliceByteStartArray sliceArray;
        private transient int[] sortedTerms;
        private final int numTokens;
        private final int numOverlapTokens;
        private final float boost;
        private final long sumTotalTermFreq;
        private int lastPosition;

        public Info(BytesRefHash terms, SliceByteStartArray sliceArray, int numTokens, int numOverlapTokens, float boost, int lastPosition, long sumTotalTermFreq) {
            this.terms = terms;
            this.sliceArray = sliceArray;
            this.numTokens = numTokens;
            this.numOverlapTokens = numOverlapTokens;
            this.boost = boost;
            this.sumTotalTermFreq = sumTotalTermFreq;
            this.lastPosition = lastPosition;
        }

        public long getSumTotalTermFreq() {
            return this.sumTotalTermFreq;
        }

        public void sortTerms() {
            if (this.sortedTerms == null) {
                this.sortedTerms = this.terms.sort(BytesRef.getUTF8SortedAsUnicodeComparator());
            }
        }

        public float getBoost() {
            return this.boost;
        }
    }
}

