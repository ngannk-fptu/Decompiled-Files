/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.DocTermOrds;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.index.SingletonSortedSetDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FieldCacheSanityChecker;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.PagedBytes;
import org.apache.lucene.util.packed.GrowableWriter;
import org.apache.lucene.util.packed.MonotonicAppendingLongBuffer;
import org.apache.lucene.util.packed.PackedInts;

class FieldCacheImpl
implements FieldCache {
    private Map<Class<?>, Cache> caches;
    final SegmentReader.CoreClosedListener purgeCore = new SegmentReader.CoreClosedListener(){

        @Override
        public void onClose(SegmentReader owner) {
            FieldCacheImpl.this.purge(owner);
        }
    };
    final IndexReader.ReaderClosedListener purgeReader = new IndexReader.ReaderClosedListener(){

        @Override
        public void onClose(IndexReader owner) {
            assert (owner instanceof AtomicReader);
            FieldCacheImpl.this.purge((AtomicReader)owner);
        }
    };
    private volatile PrintStream infoStream;

    FieldCacheImpl() {
        this.init();
    }

    private synchronized void init() {
        this.caches = new HashMap(9);
        this.caches.put(Byte.TYPE, new ByteCache(this));
        this.caches.put(Short.TYPE, new ShortCache(this));
        this.caches.put(Integer.TYPE, new IntCache(this));
        this.caches.put(Float.TYPE, new FloatCache(this));
        this.caches.put(Long.TYPE, new LongCache(this));
        this.caches.put(Double.TYPE, new DoubleCache(this));
        this.caches.put(BinaryDocValues.class, new BinaryDocValuesCache(this));
        this.caches.put(SortedDocValues.class, new SortedDocValuesCache(this));
        this.caches.put(DocTermOrds.class, new DocTermOrdsCache(this));
        this.caches.put(DocsWithFieldCache.class, new DocsWithFieldCache(this));
    }

    @Override
    public synchronized void purgeAllCaches() {
        this.init();
    }

    @Override
    public synchronized void purge(AtomicReader r) {
        for (Cache c : this.caches.values()) {
            c.purge(r);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized FieldCache.CacheEntry[] getCacheEntries() {
        ArrayList<FieldCache.CacheEntry> result = new ArrayList<FieldCache.CacheEntry>(17);
        for (Map.Entry<Class<?>, Cache> cacheEntry : this.caches.entrySet()) {
            Cache cache = cacheEntry.getValue();
            Class<?> cacheType = cacheEntry.getKey();
            Map<Object, Map<CacheKey, Object>> map = cache.readerCache;
            synchronized (map) {
                for (Map.Entry<Object, Map<CacheKey, Object>> readerCacheEntry : cache.readerCache.entrySet()) {
                    Object readerKey = readerCacheEntry.getKey();
                    if (readerKey == null) continue;
                    Map<CacheKey, Object> innerCache = readerCacheEntry.getValue();
                    for (Map.Entry<CacheKey, Object> mapEntry : innerCache.entrySet()) {
                        CacheKey entry = mapEntry.getKey();
                        result.add(new FieldCache.CacheEntry(readerKey, entry.field, cacheType, entry.custom, mapEntry.getValue()));
                    }
                }
            }
        }
        return result.toArray(new FieldCache.CacheEntry[result.size()]);
    }

    private void initReader(AtomicReader reader) {
        if (reader instanceof SegmentReader) {
            ((SegmentReader)reader).addCoreClosedListener(this.purgeCore);
        } else {
            Object key = reader.getCoreCacheKey();
            if (key instanceof AtomicReader) {
                ((AtomicReader)key).addReaderClosedListener(this.purgeReader);
            } else {
                reader.addReaderClosedListener(this.purgeReader);
            }
        }
    }

    void setDocsWithField(AtomicReader reader, String field, Bits docsWithField) {
        Bits bits;
        int maxDoc = reader.maxDoc();
        if (docsWithField == null) {
            bits = new Bits.MatchNoBits(maxDoc);
        } else if (docsWithField instanceof FixedBitSet) {
            int numSet = ((FixedBitSet)docsWithField).cardinality();
            if (numSet >= maxDoc) {
                assert (numSet == maxDoc);
                bits = new Bits.MatchAllBits(maxDoc);
            } else {
                bits = docsWithField;
            }
        } else {
            bits = docsWithField;
        }
        this.caches.get(DocsWithFieldCache.class).put(reader, new CacheKey(field, null), bits);
    }

    @Override
    public FieldCache.Bytes getBytes(AtomicReader reader, String field, boolean setDocsWithField) throws IOException {
        return this.getBytes(reader, field, null, setDocsWithField);
    }

    @Override
    public FieldCache.Bytes getBytes(AtomicReader reader, String field, FieldCache.ByteParser parser, boolean setDocsWithField) throws IOException {
        final NumericDocValues valuesIn = reader.getNumericDocValues(field);
        if (valuesIn != null) {
            return new FieldCache.Bytes(){

                @Override
                public byte get(int docID) {
                    return (byte)valuesIn.get(docID);
                }
            };
        }
        FieldInfo info = reader.getFieldInfos().fieldInfo(field);
        if (info == null) {
            return FieldCache.Bytes.EMPTY;
        }
        if (info.hasDocValues()) {
            throw new IllegalStateException("Type mismatch: " + field + " was indexed as " + (Object)((Object)info.getDocValuesType()));
        }
        if (!info.isIndexed()) {
            return FieldCache.Bytes.EMPTY;
        }
        return (FieldCache.Bytes)this.caches.get(Byte.TYPE).get(reader, new CacheKey(field, parser), setDocsWithField);
    }

    @Override
    public FieldCache.Shorts getShorts(AtomicReader reader, String field, boolean setDocsWithField) throws IOException {
        return this.getShorts(reader, field, null, setDocsWithField);
    }

    @Override
    public FieldCache.Shorts getShorts(AtomicReader reader, String field, FieldCache.ShortParser parser, boolean setDocsWithField) throws IOException {
        final NumericDocValues valuesIn = reader.getNumericDocValues(field);
        if (valuesIn != null) {
            return new FieldCache.Shorts(){

                @Override
                public short get(int docID) {
                    return (short)valuesIn.get(docID);
                }
            };
        }
        FieldInfo info = reader.getFieldInfos().fieldInfo(field);
        if (info == null) {
            return FieldCache.Shorts.EMPTY;
        }
        if (info.hasDocValues()) {
            throw new IllegalStateException("Type mismatch: " + field + " was indexed as " + (Object)((Object)info.getDocValuesType()));
        }
        if (!info.isIndexed()) {
            return FieldCache.Shorts.EMPTY;
        }
        return (FieldCache.Shorts)this.caches.get(Short.TYPE).get(reader, new CacheKey(field, parser), setDocsWithField);
    }

    @Override
    public FieldCache.Ints getInts(AtomicReader reader, String field, boolean setDocsWithField) throws IOException {
        return this.getInts(reader, field, null, setDocsWithField);
    }

    @Override
    public FieldCache.Ints getInts(AtomicReader reader, String field, FieldCache.IntParser parser, boolean setDocsWithField) throws IOException {
        final NumericDocValues valuesIn = reader.getNumericDocValues(field);
        if (valuesIn != null) {
            return new FieldCache.Ints(){

                @Override
                public int get(int docID) {
                    return (int)valuesIn.get(docID);
                }
            };
        }
        FieldInfo info = reader.getFieldInfos().fieldInfo(field);
        if (info == null) {
            return FieldCache.Ints.EMPTY;
        }
        if (info.hasDocValues()) {
            throw new IllegalStateException("Type mismatch: " + field + " was indexed as " + (Object)((Object)info.getDocValuesType()));
        }
        if (!info.isIndexed()) {
            return FieldCache.Ints.EMPTY;
        }
        return (FieldCache.Ints)this.caches.get(Integer.TYPE).get(reader, new CacheKey(field, parser), setDocsWithField);
    }

    @Override
    public Bits getDocsWithField(AtomicReader reader, String field) throws IOException {
        FieldInfo fieldInfo = reader.getFieldInfos().fieldInfo(field);
        if (fieldInfo == null) {
            return new Bits.MatchNoBits(reader.maxDoc());
        }
        if (fieldInfo.hasDocValues()) {
            return new Bits.MatchAllBits(reader.maxDoc());
        }
        if (!fieldInfo.isIndexed()) {
            return new Bits.MatchNoBits(reader.maxDoc());
        }
        return (Bits)this.caches.get(DocsWithFieldCache.class).get(reader, new CacheKey(field, null), false);
    }

    @Override
    public FieldCache.Floats getFloats(AtomicReader reader, String field, boolean setDocsWithField) throws IOException {
        return this.getFloats(reader, field, null, setDocsWithField);
    }

    @Override
    public FieldCache.Floats getFloats(AtomicReader reader, String field, FieldCache.FloatParser parser, boolean setDocsWithField) throws IOException {
        final NumericDocValues valuesIn = reader.getNumericDocValues(field);
        if (valuesIn != null) {
            return new FieldCache.Floats(){

                @Override
                public float get(int docID) {
                    return Float.intBitsToFloat((int)valuesIn.get(docID));
                }
            };
        }
        FieldInfo info = reader.getFieldInfos().fieldInfo(field);
        if (info == null) {
            return FieldCache.Floats.EMPTY;
        }
        if (info.hasDocValues()) {
            throw new IllegalStateException("Type mismatch: " + field + " was indexed as " + (Object)((Object)info.getDocValuesType()));
        }
        if (!info.isIndexed()) {
            return FieldCache.Floats.EMPTY;
        }
        return (FieldCache.Floats)this.caches.get(Float.TYPE).get(reader, new CacheKey(field, parser), setDocsWithField);
    }

    @Override
    public FieldCache.Longs getLongs(AtomicReader reader, String field, boolean setDocsWithField) throws IOException {
        return this.getLongs(reader, field, null, setDocsWithField);
    }

    @Override
    public FieldCache.Longs getLongs(AtomicReader reader, String field, FieldCache.LongParser parser, boolean setDocsWithField) throws IOException {
        final NumericDocValues valuesIn = reader.getNumericDocValues(field);
        if (valuesIn != null) {
            return new FieldCache.Longs(){

                @Override
                public long get(int docID) {
                    return valuesIn.get(docID);
                }
            };
        }
        FieldInfo info = reader.getFieldInfos().fieldInfo(field);
        if (info == null) {
            return FieldCache.Longs.EMPTY;
        }
        if (info.hasDocValues()) {
            throw new IllegalStateException("Type mismatch: " + field + " was indexed as " + (Object)((Object)info.getDocValuesType()));
        }
        if (!info.isIndexed()) {
            return FieldCache.Longs.EMPTY;
        }
        return (FieldCache.Longs)this.caches.get(Long.TYPE).get(reader, new CacheKey(field, parser), setDocsWithField);
    }

    @Override
    public FieldCache.Doubles getDoubles(AtomicReader reader, String field, boolean setDocsWithField) throws IOException {
        return this.getDoubles(reader, field, null, setDocsWithField);
    }

    @Override
    public FieldCache.Doubles getDoubles(AtomicReader reader, String field, FieldCache.DoubleParser parser, boolean setDocsWithField) throws IOException {
        final NumericDocValues valuesIn = reader.getNumericDocValues(field);
        if (valuesIn != null) {
            return new FieldCache.Doubles(){

                @Override
                public double get(int docID) {
                    return Double.longBitsToDouble(valuesIn.get(docID));
                }
            };
        }
        FieldInfo info = reader.getFieldInfos().fieldInfo(field);
        if (info == null) {
            return FieldCache.Doubles.EMPTY;
        }
        if (info.hasDocValues()) {
            throw new IllegalStateException("Type mismatch: " + field + " was indexed as " + (Object)((Object)info.getDocValuesType()));
        }
        if (!info.isIndexed()) {
            return FieldCache.Doubles.EMPTY;
        }
        return (FieldCache.Doubles)this.caches.get(Double.TYPE).get(reader, new CacheKey(field, parser), setDocsWithField);
    }

    @Override
    public SortedDocValues getTermsIndex(AtomicReader reader, String field) throws IOException {
        return this.getTermsIndex(reader, field, 0.5f);
    }

    @Override
    public SortedDocValues getTermsIndex(AtomicReader reader, String field, float acceptableOverheadRatio) throws IOException {
        SortedDocValues valuesIn = reader.getSortedDocValues(field);
        if (valuesIn != null) {
            return valuesIn;
        }
        FieldInfo info = reader.getFieldInfos().fieldInfo(field);
        if (info == null) {
            return EMPTY_TERMSINDEX;
        }
        if (info.hasDocValues()) {
            throw new IllegalStateException("Type mismatch: " + field + " was indexed as " + (Object)((Object)info.getDocValuesType()));
        }
        if (!info.isIndexed()) {
            return EMPTY_TERMSINDEX;
        }
        return (SortedDocValues)this.caches.get(SortedDocValues.class).get(reader, new CacheKey(field, Float.valueOf(acceptableOverheadRatio)), false);
    }

    @Override
    public BinaryDocValues getTerms(AtomicReader reader, String field) throws IOException {
        return this.getTerms(reader, field, 0.5f);
    }

    @Override
    public BinaryDocValues getTerms(AtomicReader reader, String field, float acceptableOverheadRatio) throws IOException {
        BinaryDocValues valuesIn = reader.getBinaryDocValues(field);
        if (valuesIn == null) {
            valuesIn = reader.getSortedDocValues(field);
        }
        if (valuesIn != null) {
            return valuesIn;
        }
        FieldInfo info = reader.getFieldInfos().fieldInfo(field);
        if (info == null) {
            return BinaryDocValues.EMPTY;
        }
        if (info.hasDocValues()) {
            throw new IllegalStateException("Type mismatch: " + field + " was indexed as " + (Object)((Object)info.getDocValuesType()));
        }
        if (!info.isIndexed()) {
            return BinaryDocValues.EMPTY;
        }
        return (BinaryDocValues)this.caches.get(BinaryDocValues.class).get(reader, new CacheKey(field, Float.valueOf(acceptableOverheadRatio)), false);
    }

    @Override
    public SortedSetDocValues getDocTermOrds(AtomicReader reader, String field) throws IOException {
        SortedSetDocValues dv = reader.getSortedSetDocValues(field);
        if (dv != null) {
            return dv;
        }
        SortedDocValues sdv = reader.getSortedDocValues(field);
        if (sdv != null) {
            return new SingletonSortedSetDocValues(sdv);
        }
        FieldInfo info = reader.getFieldInfos().fieldInfo(field);
        if (info == null) {
            return SortedSetDocValues.EMPTY;
        }
        if (info.hasDocValues()) {
            throw new IllegalStateException("Type mismatch: " + field + " was indexed as " + (Object)((Object)info.getDocValuesType()));
        }
        if (!info.isIndexed()) {
            return SortedSetDocValues.EMPTY;
        }
        DocTermOrds dto = (DocTermOrds)this.caches.get(DocTermOrds.class).get(reader, new CacheKey(field, null), false);
        return dto.iterator(reader);
    }

    @Override
    public void setInfoStream(PrintStream stream) {
        this.infoStream = stream;
    }

    @Override
    public PrintStream getInfoStream() {
        return this.infoStream;
    }

    static final class DocTermOrdsCache
    extends Cache {
        DocTermOrdsCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        @Override
        protected Object createValue(AtomicReader reader, CacheKey key, boolean setDocsWithField) throws IOException {
            return new DocTermOrds(reader, null, key.field);
        }
    }

    static final class BinaryDocValuesCache
    extends Cache {
        BinaryDocValuesCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        @Override
        protected Object createValue(AtomicReader reader, CacheKey key, boolean setDocsWithField) throws IOException {
            int startBPV;
            int maxDoc = reader.maxDoc();
            Terms terms = reader.terms(key.field);
            float acceptableOverheadRatio = ((Float)key.custom).floatValue();
            int termCountHardLimit = maxDoc;
            PagedBytes bytes = new PagedBytes(15);
            if (terms != null) {
                long numUniqueTerms = terms.size();
                if (numUniqueTerms != -1L) {
                    if (numUniqueTerms > (long)termCountHardLimit) {
                        numUniqueTerms = termCountHardLimit;
                    }
                    startBPV = PackedInts.bitsRequired(numUniqueTerms * 4L);
                } else {
                    startBPV = 1;
                }
            } else {
                startBPV = 1;
            }
            GrowableWriter docToOffset = new GrowableWriter(startBPV, maxDoc, acceptableOverheadRatio);
            bytes.copyUsingLengthPrefix(new BytesRef());
            if (terms != null) {
                BytesRef term;
                int termCount = 0;
                TermsEnum termsEnum = terms.iterator(null);
                DocsEnum docs = null;
                while (termCount++ != termCountHardLimit && (term = termsEnum.next()) != null) {
                    int docID;
                    long pointer = bytes.copyUsingLengthPrefix(term);
                    docs = termsEnum.docs(null, docs, 0);
                    while ((docID = docs.nextDoc()) != Integer.MAX_VALUE) {
                        docToOffset.set(docID, pointer);
                    }
                }
            }
            return new BinaryDocValuesImpl(bytes.freeze(true), docToOffset.getMutable());
        }
    }

    private static class BinaryDocValuesImpl
    extends BinaryDocValues {
        private final PagedBytes.Reader bytes;
        private final PackedInts.Reader docToOffset;

        public BinaryDocValuesImpl(PagedBytes.Reader bytes, PackedInts.Reader docToOffset) {
            this.bytes = bytes;
            this.docToOffset = docToOffset;
        }

        @Override
        public void get(int docID, BytesRef ret) {
            int pointer = (int)this.docToOffset.get(docID);
            if (pointer == 0) {
                ret.bytes = MISSING;
                ret.offset = 0;
                ret.length = 0;
            } else {
                this.bytes.fill(ret, pointer);
            }
        }
    }

    static class SortedDocValuesCache
    extends Cache {
        SortedDocValuesCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        @Override
        protected Object createValue(AtomicReader reader, CacheKey key, boolean setDocsWithField) throws IOException {
            int termOrd;
            int startTermsBPV;
            int maxDoc = reader.maxDoc();
            Terms terms = reader.terms(key.field);
            float acceptableOverheadRatio = ((Float)key.custom).floatValue();
            PagedBytes bytes = new PagedBytes(15);
            int termCountHardLimit = maxDoc == Integer.MAX_VALUE ? Integer.MAX_VALUE : maxDoc + 1;
            if (terms != null) {
                long numUniqueTerms = terms.size();
                if (numUniqueTerms != -1L) {
                    if (numUniqueTerms > (long)termCountHardLimit) {
                        numUniqueTerms = termCountHardLimit;
                    }
                    startTermsBPV = PackedInts.bitsRequired(numUniqueTerms);
                } else {
                    startTermsBPV = 1;
                }
            } else {
                startTermsBPV = 1;
            }
            MonotonicAppendingLongBuffer termOrdToBytesOffset = new MonotonicAppendingLongBuffer();
            GrowableWriter docToTermOrd = new GrowableWriter(startTermsBPV, maxDoc, acceptableOverheadRatio);
            if (terms != null) {
                BytesRef term;
                TermsEnum termsEnum = terms.iterator(null);
                DocsEnum docs = null;
                for (termOrd = 0; (term = termsEnum.next()) != null && termOrd < termCountHardLimit; ++termOrd) {
                    int docID;
                    termOrdToBytesOffset.add(bytes.copyUsingLengthPrefix(term));
                    docs = termsEnum.docs(null, docs, 0);
                    while ((docID = docs.nextDoc()) != Integer.MAX_VALUE) {
                        docToTermOrd.set(docID, 1 + termOrd);
                    }
                }
            }
            return new SortedDocValuesImpl(bytes.freeze(true), termOrdToBytesOffset, docToTermOrd.getMutable(), termOrd);
        }
    }

    public static class SortedDocValuesImpl
    extends SortedDocValues {
        private final PagedBytes.Reader bytes;
        private final MonotonicAppendingLongBuffer termOrdToBytesOffset;
        private final PackedInts.Reader docToTermOrd;
        private final int numOrd;

        public SortedDocValuesImpl(PagedBytes.Reader bytes, MonotonicAppendingLongBuffer termOrdToBytesOffset, PackedInts.Reader docToTermOrd, int numOrd) {
            this.bytes = bytes;
            this.docToTermOrd = docToTermOrd;
            this.termOrdToBytesOffset = termOrdToBytesOffset;
            this.numOrd = numOrd;
        }

        @Override
        public int getValueCount() {
            return this.numOrd;
        }

        @Override
        public int getOrd(int docID) {
            return (int)this.docToTermOrd.get(docID) - 1;
        }

        @Override
        public void lookupOrd(int ord, BytesRef ret) {
            if (ord < 0) {
                throw new IllegalArgumentException("ord must be >=0 (got ord=" + ord + ")");
            }
            this.bytes.fill(ret, this.termOrdToBytesOffset.get(ord));
        }
    }

    static final class DoubleCache
    extends Cache {
        DoubleCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        @Override
        protected Object createValue(final AtomicReader reader, CacheKey key, boolean setDocsWithField) throws IOException {
            double[] values;
            final FieldCache.DoubleParser parser = (FieldCache.DoubleParser)key.custom;
            if (parser == null) {
                try {
                    return this.wrapper.getDoubles(reader, key.field, FieldCache.DEFAULT_DOUBLE_PARSER, setDocsWithField);
                }
                catch (NumberFormatException ne) {
                    return this.wrapper.getDoubles(reader, key.field, FieldCache.NUMERIC_UTILS_DOUBLE_PARSER, setDocsWithField);
                }
            }
            final HoldsOneThing valuesRef = new HoldsOneThing();
            Uninvert u = new Uninvert(){
                private double currentValue;
                private double[] values;

                @Override
                public void visitTerm(BytesRef term) {
                    this.currentValue = parser.parseDouble(term);
                    if (this.values == null) {
                        this.values = new double[reader.maxDoc()];
                        valuesRef.set(this.values);
                    }
                }

                @Override
                public void visitDoc(int docID) {
                    this.values[docID] = this.currentValue;
                }

                @Override
                protected TermsEnum termsEnum(Terms terms) throws IOException {
                    return parser.termsEnum(terms);
                }
            };
            u.uninvert(reader, key.field, setDocsWithField);
            if (setDocsWithField) {
                this.wrapper.setDocsWithField(reader, key.field, u.docsWithField);
            }
            if ((values = (double[])valuesRef.get()) == null) {
                values = new double[reader.maxDoc()];
            }
            return new DoublesFromArray(values);
        }
    }

    static class DoublesFromArray
    extends FieldCache.Doubles {
        private final double[] values;

        public DoublesFromArray(double[] values) {
            this.values = values;
        }

        @Override
        public double get(int docID) {
            return this.values[docID];
        }
    }

    static final class LongCache
    extends Cache {
        LongCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        @Override
        protected Object createValue(final AtomicReader reader, CacheKey key, boolean setDocsWithField) throws IOException {
            GrowableWriterAndMinValue values;
            final FieldCache.LongParser parser = (FieldCache.LongParser)key.custom;
            if (parser == null) {
                try {
                    return this.wrapper.getLongs(reader, key.field, FieldCache.DEFAULT_LONG_PARSER, setDocsWithField);
                }
                catch (NumberFormatException ne) {
                    return this.wrapper.getLongs(reader, key.field, FieldCache.NUMERIC_UTILS_LONG_PARSER, setDocsWithField);
                }
            }
            final HoldsOneThing valuesRef = new HoldsOneThing();
            Uninvert u = new Uninvert(){
                private long minValue;
                private long currentValue;
                private GrowableWriter values;

                @Override
                public void visitTerm(BytesRef term) {
                    this.currentValue = parser.parseLong(term);
                    if (this.values == null) {
                        int startBitsPerValue;
                        if (this.currentValue < 0L) {
                            this.minValue = this.currentValue;
                            startBitsPerValue = this.minValue == Long.MIN_VALUE ? 64 : PackedInts.bitsRequired(-this.minValue);
                        } else {
                            this.minValue = 0L;
                            startBitsPerValue = PackedInts.bitsRequired(this.currentValue);
                        }
                        this.values = new GrowableWriter(startBitsPerValue, reader.maxDoc(), 0.5f);
                        if (this.minValue != 0L) {
                            this.values.fill(0, this.values.size(), -this.minValue);
                        }
                        valuesRef.set(new GrowableWriterAndMinValue(this.values, this.minValue));
                    }
                }

                @Override
                public void visitDoc(int docID) {
                    this.values.set(docID, this.currentValue - this.minValue);
                }

                @Override
                protected TermsEnum termsEnum(Terms terms) throws IOException {
                    return parser.termsEnum(terms);
                }
            };
            u.uninvert(reader, key.field, setDocsWithField);
            if (setDocsWithField) {
                this.wrapper.setDocsWithField(reader, key.field, u.docsWithField);
            }
            if ((values = (GrowableWriterAndMinValue)valuesRef.get()) == null) {
                return new LongsFromArray(new PackedInts.NullReader(reader.maxDoc()), 0L);
            }
            return new LongsFromArray(values.writer.getMutable(), values.minValue);
        }
    }

    static class LongsFromArray
    extends FieldCache.Longs {
        private final PackedInts.Reader values;
        private final long minValue;

        public LongsFromArray(PackedInts.Reader values, long minValue) {
            this.values = values;
            this.minValue = minValue;
        }

        @Override
        public long get(int docID) {
            return this.minValue + this.values.get(docID);
        }
    }

    static final class FloatCache
    extends Cache {
        FloatCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        @Override
        protected Object createValue(final AtomicReader reader, CacheKey key, boolean setDocsWithField) throws IOException {
            float[] values;
            final FieldCache.FloatParser parser = (FieldCache.FloatParser)key.custom;
            if (parser == null) {
                try {
                    return this.wrapper.getFloats(reader, key.field, FieldCache.DEFAULT_FLOAT_PARSER, setDocsWithField);
                }
                catch (NumberFormatException ne) {
                    return this.wrapper.getFloats(reader, key.field, FieldCache.NUMERIC_UTILS_FLOAT_PARSER, setDocsWithField);
                }
            }
            final HoldsOneThing valuesRef = new HoldsOneThing();
            Uninvert u = new Uninvert(){
                private float currentValue;
                private float[] values;

                @Override
                public void visitTerm(BytesRef term) {
                    this.currentValue = parser.parseFloat(term);
                    if (this.values == null) {
                        this.values = new float[reader.maxDoc()];
                        valuesRef.set(this.values);
                    }
                }

                @Override
                public void visitDoc(int docID) {
                    this.values[docID] = this.currentValue;
                }

                @Override
                protected TermsEnum termsEnum(Terms terms) throws IOException {
                    return parser.termsEnum(terms);
                }
            };
            u.uninvert(reader, key.field, setDocsWithField);
            if (setDocsWithField) {
                this.wrapper.setDocsWithField(reader, key.field, u.docsWithField);
            }
            if ((values = (float[])valuesRef.get()) == null) {
                values = new float[reader.maxDoc()];
            }
            return new FloatsFromArray(values);
        }
    }

    static class FloatsFromArray
    extends FieldCache.Floats {
        private final float[] values;

        public FloatsFromArray(float[] values) {
            this.values = values;
        }

        @Override
        public float get(int docID) {
            return this.values[docID];
        }
    }

    static final class DocsWithFieldCache
    extends Cache {
        DocsWithFieldCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        @Override
        protected Object createValue(AtomicReader reader, CacheKey key, boolean setDocsWithField) throws IOException {
            String field = key.field;
            int maxDoc = reader.maxDoc();
            FixedBitSet res = null;
            Terms terms = reader.terms(field);
            if (terms != null) {
                BytesRef term;
                int termsDocCount = terms.getDocCount();
                assert (termsDocCount <= maxDoc);
                if (termsDocCount == maxDoc) {
                    return new Bits.MatchAllBits(maxDoc);
                }
                TermsEnum termsEnum = terms.iterator(null);
                DocsEnum docs = null;
                while ((term = termsEnum.next()) != null) {
                    int docID;
                    if (res == null) {
                        res = new FixedBitSet(maxDoc);
                    }
                    docs = termsEnum.docs(null, docs, 0);
                    while ((docID = docs.nextDoc()) != Integer.MAX_VALUE) {
                        res.set(docID);
                    }
                }
            }
            if (res == null) {
                return new Bits.MatchNoBits(maxDoc);
            }
            int numSet = res.cardinality();
            if (numSet >= maxDoc) {
                assert (numSet == maxDoc);
                return new Bits.MatchAllBits(maxDoc);
            }
            return res;
        }
    }

    static final class IntCache
    extends Cache {
        IntCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        @Override
        protected Object createValue(final AtomicReader reader, CacheKey key, boolean setDocsWithField) throws IOException {
            GrowableWriterAndMinValue values;
            final FieldCache.IntParser parser = (FieldCache.IntParser)key.custom;
            if (parser == null) {
                try {
                    return this.wrapper.getInts(reader, key.field, FieldCache.DEFAULT_INT_PARSER, setDocsWithField);
                }
                catch (NumberFormatException ne) {
                    return this.wrapper.getInts(reader, key.field, FieldCache.NUMERIC_UTILS_INT_PARSER, setDocsWithField);
                }
            }
            final HoldsOneThing valuesRef = new HoldsOneThing();
            Uninvert u = new Uninvert(){
                private int minValue;
                private int currentValue;
                private GrowableWriter values;

                @Override
                public void visitTerm(BytesRef term) {
                    this.currentValue = parser.parseInt(term);
                    if (this.values == null) {
                        int startBitsPerValue;
                        if (this.currentValue < 0) {
                            this.minValue = this.currentValue;
                            startBitsPerValue = PackedInts.bitsRequired((long)(-this.minValue) & 0xFFFFFFFFL);
                        } else {
                            this.minValue = 0;
                            startBitsPerValue = PackedInts.bitsRequired(this.currentValue);
                        }
                        this.values = new GrowableWriter(startBitsPerValue, reader.maxDoc(), 0.5f);
                        if (this.minValue != 0) {
                            this.values.fill(0, this.values.size(), (long)(-this.minValue) & 0xFFFFFFFFL);
                        }
                        valuesRef.set(new GrowableWriterAndMinValue(this.values, this.minValue));
                    }
                }

                @Override
                public void visitDoc(int docID) {
                    this.values.set(docID, (long)(this.currentValue - this.minValue) & 0xFFFFFFFFL);
                }

                @Override
                protected TermsEnum termsEnum(Terms terms) throws IOException {
                    return parser.termsEnum(terms);
                }
            };
            u.uninvert(reader, key.field, setDocsWithField);
            if (setDocsWithField) {
                this.wrapper.setDocsWithField(reader, key.field, u.docsWithField);
            }
            if ((values = (GrowableWriterAndMinValue)valuesRef.get()) == null) {
                return new IntsFromArray(new PackedInts.NullReader(reader.maxDoc()), 0);
            }
            return new IntsFromArray(values.writer.getMutable(), (int)values.minValue);
        }
    }

    private static class GrowableWriterAndMinValue {
        public GrowableWriter writer;
        public long minValue;

        GrowableWriterAndMinValue(GrowableWriter array, long minValue) {
            this.writer = array;
            this.minValue = minValue;
        }
    }

    private static class HoldsOneThing<T> {
        private T it;

        private HoldsOneThing() {
        }

        public void set(T it) {
            this.it = it;
        }

        public T get() {
            return this.it;
        }
    }

    static class IntsFromArray
    extends FieldCache.Ints {
        private final PackedInts.Reader values;
        private final int minValue;

        public IntsFromArray(PackedInts.Reader values, int minValue) {
            assert (values.getBitsPerValue() <= 32);
            this.values = values;
            this.minValue = minValue;
        }

        @Override
        public int get(int docID) {
            long delta = this.values.get(docID);
            return this.minValue + (int)delta;
        }
    }

    static final class ShortCache
    extends Cache {
        ShortCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        @Override
        protected Object createValue(AtomicReader reader, CacheKey key, boolean setDocsWithField) throws IOException {
            int maxDoc = reader.maxDoc();
            final FieldCache.ShortParser parser = (FieldCache.ShortParser)key.custom;
            if (parser == null) {
                return this.wrapper.getShorts(reader, key.field, FieldCache.DEFAULT_SHORT_PARSER, setDocsWithField);
            }
            final short[] values = new short[maxDoc];
            Uninvert u = new Uninvert(){
                private short currentValue;

                @Override
                public void visitTerm(BytesRef term) {
                    this.currentValue = parser.parseShort(term);
                }

                @Override
                public void visitDoc(int docID) {
                    values[docID] = this.currentValue;
                }

                @Override
                protected TermsEnum termsEnum(Terms terms) throws IOException {
                    return parser.termsEnum(terms);
                }
            };
            u.uninvert(reader, key.field, setDocsWithField);
            if (setDocsWithField) {
                this.wrapper.setDocsWithField(reader, key.field, u.docsWithField);
            }
            return new ShortsFromArray(values);
        }
    }

    static class ShortsFromArray
    extends FieldCache.Shorts {
        private final short[] values;

        public ShortsFromArray(short[] values) {
            this.values = values;
        }

        @Override
        public short get(int docID) {
            return this.values[docID];
        }
    }

    static final class ByteCache
    extends Cache {
        ByteCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        @Override
        protected Object createValue(AtomicReader reader, CacheKey key, boolean setDocsWithField) throws IOException {
            int maxDoc = reader.maxDoc();
            final FieldCache.ByteParser parser = (FieldCache.ByteParser)key.custom;
            if (parser == null) {
                return this.wrapper.getBytes(reader, key.field, FieldCache.DEFAULT_BYTE_PARSER, setDocsWithField);
            }
            final byte[] values = new byte[maxDoc];
            Uninvert u = new Uninvert(){
                private byte currentValue;

                @Override
                public void visitTerm(BytesRef term) {
                    this.currentValue = parser.parseByte(term);
                }

                @Override
                public void visitDoc(int docID) {
                    values[docID] = this.currentValue;
                }

                @Override
                protected TermsEnum termsEnum(Terms terms) throws IOException {
                    return parser.termsEnum(terms);
                }
            };
            u.uninvert(reader, key.field, setDocsWithField);
            if (setDocsWithField) {
                this.wrapper.setDocsWithField(reader, key.field, u.docsWithField);
            }
            return new BytesFromArray(values);
        }
    }

    static class BytesFromArray
    extends FieldCache.Bytes {
        private final byte[] values;

        public BytesFromArray(byte[] values) {
            this.values = values;
        }

        @Override
        public byte get(int docID) {
            return this.values[docID];
        }
    }

    private static abstract class Uninvert {
        public Bits docsWithField;

        private Uninvert() {
        }

        public void uninvert(AtomicReader reader, String field, boolean setDocsWithField) throws IOException {
            int maxDoc = reader.maxDoc();
            Terms terms = reader.terms(field);
            if (terms != null) {
                BytesRef term;
                if (setDocsWithField) {
                    int termsDocCount = terms.getDocCount();
                    assert (termsDocCount <= maxDoc);
                    if (termsDocCount == maxDoc) {
                        this.docsWithField = new Bits.MatchAllBits(maxDoc);
                        setDocsWithField = false;
                    }
                }
                TermsEnum termsEnum = this.termsEnum(terms);
                DocsEnum docs = null;
                FixedBitSet docsWithField = null;
                while ((term = termsEnum.next()) != null) {
                    int docID;
                    this.visitTerm(term);
                    docs = termsEnum.docs(null, docs, 0);
                    while ((docID = docs.nextDoc()) != Integer.MAX_VALUE) {
                        this.visitDoc(docID);
                        if (!setDocsWithField) continue;
                        if (docsWithField == null) {
                            docsWithField = new FixedBitSet(maxDoc);
                            this.docsWithField = docsWithField;
                        }
                        docsWithField.set(docID);
                    }
                }
            }
        }

        protected abstract TermsEnum termsEnum(Terms var1) throws IOException;

        protected abstract void visitTerm(BytesRef var1);

        protected abstract void visitDoc(int var1);
    }

    static class CacheKey {
        final String field;
        final Object custom;

        CacheKey(String field, Object custom) {
            this.field = field;
            this.custom = custom;
        }

        public boolean equals(Object o) {
            if (o instanceof CacheKey) {
                CacheKey other = (CacheKey)o;
                if (other.field.equals(this.field) && (other.custom == null ? this.custom == null : other.custom.equals(this.custom))) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            return this.field.hashCode() ^ (this.custom == null ? 0 : this.custom.hashCode());
        }
    }

    static abstract class Cache {
        final FieldCacheImpl wrapper;
        final Map<Object, Map<CacheKey, Object>> readerCache = new WeakHashMap<Object, Map<CacheKey, Object>>();

        Cache(FieldCacheImpl wrapper) {
            this.wrapper = wrapper;
        }

        protected abstract Object createValue(AtomicReader var1, CacheKey var2, boolean var3) throws IOException;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void purge(AtomicReader r) {
            Object readerKey = r.getCoreCacheKey();
            Map<Object, Map<CacheKey, Object>> map = this.readerCache;
            synchronized (map) {
                this.readerCache.remove(readerKey);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void put(AtomicReader reader, CacheKey key, Object value) {
            Object readerKey = reader.getCoreCacheKey();
            Map<Object, Map<CacheKey, Object>> map = this.readerCache;
            synchronized (map) {
                Map<CacheKey, Object> innerCache = this.readerCache.get(readerKey);
                if (innerCache == null) {
                    innerCache = new HashMap<CacheKey, Object>();
                    this.readerCache.put(readerKey, innerCache);
                    this.wrapper.initReader(reader);
                }
                if (innerCache.get(key) == null) {
                    innerCache.put(key, value);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Object get(AtomicReader reader, CacheKey key, boolean setDocsWithField) throws IOException {
            Object value;
            Map<CacheKey, Object> innerCache;
            Object readerKey = reader.getCoreCacheKey();
            Object object = this.readerCache;
            synchronized (object) {
                innerCache = this.readerCache.get(readerKey);
                if (innerCache == null) {
                    innerCache = new HashMap<CacheKey, Object>();
                    this.readerCache.put(readerKey, innerCache);
                    this.wrapper.initReader(reader);
                    value = null;
                } else {
                    value = innerCache.get(key);
                }
                if (value == null) {
                    value = new FieldCache.CreationPlaceholder();
                    innerCache.put(key, value);
                }
            }
            if (value instanceof FieldCache.CreationPlaceholder) {
                object = value;
                synchronized (object) {
                    FieldCache.CreationPlaceholder progress = (FieldCache.CreationPlaceholder)value;
                    if (progress.value == null) {
                        PrintStream infoStream;
                        progress.value = this.createValue(reader, key, setDocsWithField);
                        Map<Object, Map<CacheKey, Object>> map = this.readerCache;
                        synchronized (map) {
                            innerCache.put(key, progress.value);
                        }
                        if (key.custom != null && this.wrapper != null && (infoStream = this.wrapper.getInfoStream()) != null) {
                            this.printNewInsanity(infoStream, progress.value);
                        }
                    }
                    return progress.value;
                }
            }
            return value;
        }

        private void printNewInsanity(PrintStream infoStream, Object value) {
            FieldCacheSanityChecker.Insanity[] insanities = FieldCacheSanityChecker.checkSanity(this.wrapper);
            block0: for (int i = 0; i < insanities.length; ++i) {
                FieldCacheSanityChecker.Insanity insanity = insanities[i];
                FieldCache.CacheEntry[] entries = insanity.getCacheEntries();
                for (int j = 0; j < entries.length; ++j) {
                    if (entries[j].getValue() != value) continue;
                    infoStream.println("WARNING: new FieldCache insanity created\nDetails: " + insanity.toString());
                    infoStream.println("\nStack:\n");
                    new Throwable().printStackTrace(infoStream);
                    continue block0;
                }
            }
        }
    }
}

