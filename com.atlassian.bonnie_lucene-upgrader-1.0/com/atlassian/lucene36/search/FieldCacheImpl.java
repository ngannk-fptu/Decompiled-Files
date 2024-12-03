/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.SegmentReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermDocs;
import com.atlassian.lucene36.index.TermEnum;
import com.atlassian.lucene36.search.FieldCache;
import com.atlassian.lucene36.util.Bits;
import com.atlassian.lucene36.util.FieldCacheSanityChecker;
import com.atlassian.lucene36.util.FixedBitSet;
import com.atlassian.lucene36.util.StringHelper;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

class FieldCacheImpl
implements FieldCache {
    private Map<Class<?>, Cache> caches;
    final SegmentReader.CoreClosedListener purgeCore = new SegmentReader.CoreClosedListener(){

        public void onClose(SegmentReader owner) {
            FieldCacheImpl.this.purge(owner);
        }
    };
    final IndexReader.ReaderClosedListener purgeReader = new IndexReader.ReaderClosedListener(){

        public void onClose(IndexReader owner) {
            FieldCacheImpl.this.purge(owner);
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
        this.caches.put(String.class, new StringCache(this));
        this.caches.put(FieldCache.StringIndex.class, new StringIndexCache(this));
        this.caches.put(DocsWithFieldCache.class, new DocsWithFieldCache(this));
    }

    public synchronized void purgeAllCaches() {
        this.init();
    }

    public synchronized void purge(IndexReader r) {
        for (Cache c : this.caches.values()) {
            c.purge(r);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized FieldCache.CacheEntry[] getCacheEntries() {
        ArrayList<CacheEntryImpl> result = new ArrayList<CacheEntryImpl>(17);
        for (Map.Entry<Class<?>, Cache> cacheEntry : this.caches.entrySet()) {
            Cache cache = cacheEntry.getValue();
            Class<?> cacheType = cacheEntry.getKey();
            Map<Object, Map<Entry, Object>> map = cache.readerCache;
            synchronized (map) {
                for (Map.Entry<Object, Map<Entry, Object>> readerCacheEntry : cache.readerCache.entrySet()) {
                    Object readerKey = readerCacheEntry.getKey();
                    if (readerKey == null) continue;
                    Map<Entry, Object> innerCache = readerCacheEntry.getValue();
                    for (Map.Entry<Entry, Object> mapEntry : innerCache.entrySet()) {
                        Entry entry = mapEntry.getKey();
                        result.add(new CacheEntryImpl(readerKey, entry.field, cacheType, entry.custom, mapEntry.getValue()));
                    }
                }
            }
        }
        return result.toArray(new FieldCache.CacheEntry[result.size()]);
    }

    public byte[] getBytes(IndexReader reader, String field) throws IOException {
        return this.getBytes(reader, field, null, false);
    }

    public byte[] getBytes(IndexReader reader, String field, FieldCache.ByteParser parser) throws IOException {
        return this.getBytes(reader, field, parser, false);
    }

    public byte[] getBytes(IndexReader reader, String field, FieldCache.ByteParser parser, boolean setDocsWithField) throws IOException {
        return (byte[])this.caches.get(Byte.TYPE).get(reader, new Entry(field, parser), setDocsWithField);
    }

    public short[] getShorts(IndexReader reader, String field) throws IOException {
        return this.getShorts(reader, field, null, false);
    }

    public short[] getShorts(IndexReader reader, String field, FieldCache.ShortParser parser) throws IOException {
        return this.getShorts(reader, field, parser, false);
    }

    public short[] getShorts(IndexReader reader, String field, FieldCache.ShortParser parser, boolean setDocsWithField) throws IOException {
        return (short[])this.caches.get(Short.TYPE).get(reader, new Entry(field, parser), setDocsWithField);
    }

    void setDocsWithField(IndexReader reader, String field, Bits docsWithField) {
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
        this.caches.get(DocsWithFieldCache.class).put(reader, new Entry(field, null), bits);
    }

    public int[] getInts(IndexReader reader, String field) throws IOException {
        return this.getInts(reader, field, null);
    }

    public int[] getInts(IndexReader reader, String field, FieldCache.IntParser parser) throws IOException {
        return this.getInts(reader, field, parser, false);
    }

    public int[] getInts(IndexReader reader, String field, FieldCache.IntParser parser, boolean setDocsWithField) throws IOException {
        return (int[])this.caches.get(Integer.TYPE).get(reader, new Entry(field, parser), setDocsWithField);
    }

    public Bits getDocsWithField(IndexReader reader, String field) throws IOException {
        return (Bits)this.caches.get(DocsWithFieldCache.class).get(reader, new Entry(field, null), false);
    }

    public float[] getFloats(IndexReader reader, String field) throws IOException {
        return this.getFloats(reader, field, null, false);
    }

    public float[] getFloats(IndexReader reader, String field, FieldCache.FloatParser parser) throws IOException {
        return this.getFloats(reader, field, parser, false);
    }

    public float[] getFloats(IndexReader reader, String field, FieldCache.FloatParser parser, boolean setDocsWithField) throws IOException {
        return (float[])this.caches.get(Float.TYPE).get(reader, new Entry(field, parser), setDocsWithField);
    }

    public long[] getLongs(IndexReader reader, String field) throws IOException {
        return this.getLongs(reader, field, null, false);
    }

    public long[] getLongs(IndexReader reader, String field, FieldCache.LongParser parser) throws IOException {
        return this.getLongs(reader, field, parser, false);
    }

    public long[] getLongs(IndexReader reader, String field, FieldCache.LongParser parser, boolean setDocsWithField) throws IOException {
        return (long[])this.caches.get(Long.TYPE).get(reader, new Entry(field, parser), setDocsWithField);
    }

    public double[] getDoubles(IndexReader reader, String field) throws IOException {
        return this.getDoubles(reader, field, null, false);
    }

    public double[] getDoubles(IndexReader reader, String field, FieldCache.DoubleParser parser) throws IOException {
        return this.getDoubles(reader, field, parser, false);
    }

    public double[] getDoubles(IndexReader reader, String field, FieldCache.DoubleParser parser, boolean setDocsWithField) throws IOException {
        return (double[])this.caches.get(Double.TYPE).get(reader, new Entry(field, parser), setDocsWithField);
    }

    public String[] getStrings(IndexReader reader, String field) throws IOException {
        return (String[])this.caches.get(String.class).get(reader, new Entry(field, null), false);
    }

    public FieldCache.StringIndex getStringIndex(IndexReader reader, String field) throws IOException {
        return (FieldCache.StringIndex)this.caches.get(FieldCache.StringIndex.class).get(reader, new Entry(field, null), false);
    }

    public void setInfoStream(PrintStream stream) {
        this.infoStream = stream;
    }

    public PrintStream getInfoStream() {
        return this.infoStream;
    }

    static final class StringIndexCache
    extends Cache {
        StringIndexCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected Object createValue(IndexReader reader, Entry entryKey, boolean setDocsWithField) throws IOException {
            String field = StringHelper.intern(entryKey.field);
            int[] retArray = new int[reader.maxDoc()];
            String[] mterms = new String[reader.maxDoc() + 1];
            TermDocs termDocs = reader.termDocs();
            TermEnum termEnum = reader.terms(new Term(field));
            int t = 0;
            mterms[t++] = null;
            try {
                Term term;
                while ((term = termEnum.term()) != null && term.field() == field && t < mterms.length) {
                    mterms[t] = term.text();
                    termDocs.seek(termEnum);
                    while (termDocs.next()) {
                        retArray[termDocs.doc()] = t;
                    }
                    ++t;
                    if (termEnum.next()) continue;
                    break;
                }
                Object var12_11 = null;
            }
            catch (Throwable throwable) {
                Object var12_12 = null;
                termDocs.close();
                termEnum.close();
                throw throwable;
            }
            termDocs.close();
            termEnum.close();
            if (t == 0) {
                mterms = new String[1];
            } else if (t < mterms.length) {
                String[] terms = new String[t];
                System.arraycopy(mterms, 0, terms, 0, t);
                mterms = terms;
            }
            FieldCache.StringIndex value = new FieldCache.StringIndex(retArray, mterms);
            return value;
        }
    }

    static final class StringCache
    extends Cache {
        StringCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected Object createValue(IndexReader reader, Entry entryKey, boolean setDocsWithField) throws IOException {
            String field = StringHelper.intern(entryKey.field);
            String[] retArray = new String[reader.maxDoc()];
            TermDocs termDocs = reader.termDocs();
            TermEnum termEnum = reader.terms(new Term(field));
            int termCountHardLimit = reader.maxDoc();
            int termCount = 0;
            try {
                Term term;
                while (termCount++ != termCountHardLimit && (term = termEnum.term()) != null && term.field() == field) {
                    String termval = term.text();
                    termDocs.seek(termEnum);
                    while (termDocs.next()) {
                        retArray[termDocs.doc()] = termval;
                    }
                    if (termEnum.next()) continue;
                    break;
                }
                Object var13_12 = null;
            }
            catch (Throwable throwable) {
                Object var13_13 = null;
                termDocs.close();
                termEnum.close();
                throw throwable;
            }
            termDocs.close();
            termEnum.close();
            return retArray;
        }
    }

    static final class DoubleCache
    extends Cache {
        DoubleCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        protected Object createValue(IndexReader reader, Entry entryKey, boolean setDocsWithField) throws IOException {
            FixedBitSet docsWithField;
            double[] retArray;
            int maxDoc;
            String field;
            block13: {
                Term term;
                Entry entry = entryKey;
                field = entry.field;
                FieldCache.DoubleParser parser = (FieldCache.DoubleParser)entry.custom;
                if (parser == null) {
                    try {
                        return this.wrapper.getDoubles(reader, field, FieldCache.DEFAULT_DOUBLE_PARSER, setDocsWithField);
                    }
                    catch (NumberFormatException ne) {
                        return this.wrapper.getDoubles(reader, field, FieldCache.NUMERIC_UTILS_DOUBLE_PARSER, setDocsWithField);
                    }
                }
                maxDoc = reader.maxDoc();
                retArray = null;
                TermDocs termDocs = reader.termDocs();
                TermEnum termEnum = reader.terms(new Term(field));
                docsWithField = null;
                try {
                    try {}
                    catch (StopFillCacheException stop) {
                        Object var17_18 = null;
                        termDocs.close();
                        termEnum.close();
                        break block13;
                    }
                }
                catch (Throwable throwable) {
                    Object var17_19 = null;
                    termDocs.close();
                    termEnum.close();
                    throw throwable;
                }
                while ((term = termEnum.term()) != null && term.field() == field) {
                    double termval = parser.parseDouble(term.text());
                    if (retArray == null) {
                        retArray = new double[maxDoc];
                    }
                    termDocs.seek(termEnum);
                    while (termDocs.next()) {
                        int docID = termDocs.doc();
                        retArray[docID] = termval;
                        if (!setDocsWithField) continue;
                        if (docsWithField == null) {
                            docsWithField = new FixedBitSet(maxDoc);
                        }
                        docsWithField.set(docID);
                    }
                    if (termEnum.next()) continue;
                    break;
                }
                Object var17_17 = null;
                termDocs.close();
                termEnum.close();
            }
            if (setDocsWithField) {
                this.wrapper.setDocsWithField(reader, field, docsWithField);
            }
            if (retArray != null) return retArray;
            return new double[maxDoc];
        }
    }

    static final class LongCache
    extends Cache {
        LongCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        protected Object createValue(IndexReader reader, Entry entry, boolean setDocsWithField) throws IOException {
            FixedBitSet docsWithField;
            long[] retArray;
            int maxDoc;
            String field;
            block13: {
                Term term;
                field = entry.field;
                FieldCache.LongParser parser = (FieldCache.LongParser)entry.custom;
                if (parser == null) {
                    try {
                        return this.wrapper.getLongs(reader, field, FieldCache.DEFAULT_LONG_PARSER, setDocsWithField);
                    }
                    catch (NumberFormatException ne) {
                        return this.wrapper.getLongs(reader, field, FieldCache.NUMERIC_UTILS_LONG_PARSER, setDocsWithField);
                    }
                }
                maxDoc = reader.maxDoc();
                retArray = null;
                TermDocs termDocs = reader.termDocs();
                TermEnum termEnum = reader.terms(new Term(field));
                docsWithField = null;
                try {
                    try {}
                    catch (StopFillCacheException stop) {
                        Object var16_17 = null;
                        termDocs.close();
                        termEnum.close();
                        break block13;
                    }
                }
                catch (Throwable throwable) {
                    Object var16_18 = null;
                    termDocs.close();
                    termEnum.close();
                    throw throwable;
                }
                while ((term = termEnum.term()) != null && term.field() == field) {
                    long termval = parser.parseLong(term.text());
                    if (retArray == null) {
                        retArray = new long[maxDoc];
                    }
                    termDocs.seek(termEnum);
                    while (termDocs.next()) {
                        int docID = termDocs.doc();
                        retArray[docID] = termval;
                        if (!setDocsWithField) continue;
                        if (docsWithField == null) {
                            docsWithField = new FixedBitSet(maxDoc);
                        }
                        docsWithField.set(docID);
                    }
                    if (termEnum.next()) continue;
                    break;
                }
                Object var16_16 = null;
                termDocs.close();
                termEnum.close();
            }
            if (setDocsWithField) {
                this.wrapper.setDocsWithField(reader, field, docsWithField);
            }
            if (retArray != null) return retArray;
            return new long[maxDoc];
        }
    }

    static final class FloatCache
    extends Cache {
        FloatCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        protected Object createValue(IndexReader reader, Entry entryKey, boolean setDocsWithField) throws IOException {
            FixedBitSet docsWithField;
            float[] retArray;
            int maxDoc;
            String field;
            block13: {
                Term term;
                Entry entry = entryKey;
                field = entry.field;
                FieldCache.FloatParser parser = (FieldCache.FloatParser)entry.custom;
                if (parser == null) {
                    try {
                        return this.wrapper.getFloats(reader, field, FieldCache.DEFAULT_FLOAT_PARSER, setDocsWithField);
                    }
                    catch (NumberFormatException ne) {
                        return this.wrapper.getFloats(reader, field, FieldCache.NUMERIC_UTILS_FLOAT_PARSER, setDocsWithField);
                    }
                }
                maxDoc = reader.maxDoc();
                retArray = null;
                TermDocs termDocs = reader.termDocs();
                TermEnum termEnum = reader.terms(new Term(field));
                docsWithField = null;
                try {
                    try {}
                    catch (StopFillCacheException stop) {
                        Object var16_18 = null;
                        termDocs.close();
                        termEnum.close();
                        break block13;
                    }
                }
                catch (Throwable throwable) {
                    Object var16_19 = null;
                    termDocs.close();
                    termEnum.close();
                    throw throwable;
                }
                while ((term = termEnum.term()) != null && term.field() == field) {
                    float termval = parser.parseFloat(term.text());
                    if (retArray == null) {
                        retArray = new float[maxDoc];
                    }
                    termDocs.seek(termEnum);
                    while (termDocs.next()) {
                        int docID = termDocs.doc();
                        retArray[docID] = termval;
                        if (!setDocsWithField) continue;
                        if (docsWithField == null) {
                            docsWithField = new FixedBitSet(maxDoc);
                        }
                        docsWithField.set(docID);
                    }
                    if (termEnum.next()) continue;
                    break;
                }
                Object var16_17 = null;
                termDocs.close();
                termEnum.close();
            }
            if (setDocsWithField) {
                this.wrapper.setDocsWithField(reader, field, docsWithField);
            }
            if (retArray != null) return retArray;
            return new float[maxDoc];
        }
    }

    static final class DocsWithFieldCache
    extends Cache {
        DocsWithFieldCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected Object createValue(IndexReader reader, Entry entryKey, boolean setDocsWithField) throws IOException {
            Entry entry = entryKey;
            String field = entry.field;
            FixedBitSet res = null;
            TermDocs termDocs = reader.termDocs();
            TermEnum termEnum = reader.terms(new Term(field));
            try {
                Term term;
                while ((term = termEnum.term()) != null && term.field() == field) {
                    if (res == null) {
                        res = new FixedBitSet(reader.maxDoc());
                    }
                    termDocs.seek(termEnum);
                    while (termDocs.next()) {
                        res.set(termDocs.doc());
                    }
                    if (termEnum.next()) continue;
                    break;
                }
                Object var11_11 = null;
            }
            catch (Throwable throwable) {
                Object var11_12 = null;
                termDocs.close();
                termEnum.close();
                throw throwable;
            }
            termDocs.close();
            termEnum.close();
            if (res == null) {
                return new Bits.MatchNoBits(reader.maxDoc());
            }
            int numSet = res.cardinality();
            if (numSet >= reader.numDocs()) {
                assert (numSet == reader.numDocs());
                return new Bits.MatchAllBits(reader.maxDoc());
            }
            return res;
        }
    }

    static final class IntCache
    extends Cache {
        IntCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        protected Object createValue(IndexReader reader, Entry entryKey, boolean setDocsWithField) throws IOException {
            FixedBitSet docsWithField;
            int[] retArray;
            int maxDoc;
            String field;
            block13: {
                Term term;
                Entry entry = entryKey;
                field = entry.field;
                FieldCache.IntParser parser = (FieldCache.IntParser)entry.custom;
                if (parser == null) {
                    try {
                        return this.wrapper.getInts(reader, field, FieldCache.DEFAULT_INT_PARSER, setDocsWithField);
                    }
                    catch (NumberFormatException ne) {
                        return this.wrapper.getInts(reader, field, FieldCache.NUMERIC_UTILS_INT_PARSER, setDocsWithField);
                    }
                }
                maxDoc = reader.maxDoc();
                retArray = null;
                TermDocs termDocs = reader.termDocs();
                TermEnum termEnum = reader.terms(new Term(field));
                docsWithField = null;
                try {
                    try {}
                    catch (StopFillCacheException stop) {
                        Object var16_18 = null;
                        termDocs.close();
                        termEnum.close();
                        break block13;
                    }
                }
                catch (Throwable throwable) {
                    Object var16_19 = null;
                    termDocs.close();
                    termEnum.close();
                    throw throwable;
                }
                while ((term = termEnum.term()) != null && term.field() == field) {
                    int termval = parser.parseInt(term.text());
                    if (retArray == null) {
                        retArray = new int[maxDoc];
                    }
                    termDocs.seek(termEnum);
                    while (termDocs.next()) {
                        int docID = termDocs.doc();
                        retArray[docID] = termval;
                        if (!setDocsWithField) continue;
                        if (docsWithField == null) {
                            docsWithField = new FixedBitSet(maxDoc);
                        }
                        docsWithField.set(docID);
                    }
                    if (termEnum.next()) continue;
                    break;
                }
                Object var16_17 = null;
                termDocs.close();
                termEnum.close();
            }
            if (setDocsWithField) {
                this.wrapper.setDocsWithField(reader, field, docsWithField);
            }
            if (retArray != null) return retArray;
            return new int[maxDoc];
        }
    }

    static final class ShortCache
    extends Cache {
        ShortCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        protected Object createValue(IndexReader reader, Entry entryKey, boolean setDocsWithField) throws IOException {
            FixedBitSet docsWithField;
            short[] retArray;
            String field;
            block10: {
                Term term;
                Entry entry = entryKey;
                field = entry.field;
                FieldCache.ShortParser parser = (FieldCache.ShortParser)entry.custom;
                if (parser == null) {
                    return this.wrapper.getShorts(reader, field, FieldCache.DEFAULT_SHORT_PARSER, setDocsWithField);
                }
                int maxDoc = reader.maxDoc();
                retArray = new short[maxDoc];
                TermDocs termDocs = reader.termDocs();
                TermEnum termEnum = reader.terms(new Term(field));
                docsWithField = null;
                try {
                    try {}
                    catch (StopFillCacheException stop) {
                        Object var16_17 = null;
                        termDocs.close();
                        termEnum.close();
                        break block10;
                    }
                }
                catch (Throwable throwable) {
                    Object var16_18 = null;
                    termDocs.close();
                    termEnum.close();
                    throw throwable;
                }
                while ((term = termEnum.term()) != null && term.field() == field) {
                    short termval = parser.parseShort(term.text());
                    termDocs.seek(termEnum);
                    while (termDocs.next()) {
                        int docID = termDocs.doc();
                        retArray[docID] = termval;
                        if (!setDocsWithField) continue;
                        if (docsWithField == null) {
                            docsWithField = new FixedBitSet(maxDoc);
                        }
                        docsWithField.set(docID);
                    }
                    if (termEnum.next()) continue;
                    break;
                }
                Object var16_16 = null;
                termDocs.close();
                termEnum.close();
            }
            if (setDocsWithField) {
                this.wrapper.setDocsWithField(reader, field, docsWithField);
            }
            return retArray;
        }
    }

    static final class ByteCache
    extends Cache {
        ByteCache(FieldCacheImpl wrapper) {
            super(wrapper);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        protected Object createValue(IndexReader reader, Entry entryKey, boolean setDocsWithField) throws IOException {
            FixedBitSet docsWithField;
            byte[] retArray;
            String field;
            block10: {
                Term term;
                Entry entry = entryKey;
                field = entry.field;
                FieldCache.ByteParser parser = (FieldCache.ByteParser)entry.custom;
                if (parser == null) {
                    return this.wrapper.getBytes(reader, field, FieldCache.DEFAULT_BYTE_PARSER, setDocsWithField);
                }
                int maxDoc = reader.maxDoc();
                retArray = new byte[maxDoc];
                TermDocs termDocs = reader.termDocs();
                TermEnum termEnum = reader.terms(new Term(field));
                docsWithField = null;
                try {
                    try {}
                    catch (StopFillCacheException stop) {
                        Object var16_17 = null;
                        termDocs.close();
                        termEnum.close();
                        break block10;
                    }
                }
                catch (Throwable throwable) {
                    Object var16_18 = null;
                    termDocs.close();
                    termEnum.close();
                    throw throwable;
                }
                while ((term = termEnum.term()) != null && term.field() == field) {
                    byte termval = parser.parseByte(term.text());
                    termDocs.seek(termEnum);
                    while (termDocs.next()) {
                        int docID = termDocs.doc();
                        retArray[docID] = termval;
                        if (!setDocsWithField) continue;
                        if (docsWithField == null) {
                            docsWithField = new FixedBitSet(maxDoc);
                        }
                        docsWithField.set(docID);
                    }
                    if (termEnum.next()) continue;
                    break;
                }
                Object var16_16 = null;
                termDocs.close();
                termEnum.close();
            }
            if (setDocsWithField) {
                this.wrapper.setDocsWithField(reader, field, docsWithField);
            }
            return retArray;
        }
    }

    static class Entry {
        final String field;
        final Object custom;

        Entry(String field, Object custom) {
            this.field = StringHelper.intern(field);
            this.custom = custom;
        }

        public boolean equals(Object o) {
            if (o instanceof Entry) {
                Entry other = (Entry)o;
                if (other.field == this.field && (other.custom == null ? this.custom == null : other.custom.equals(this.custom))) {
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
        final Map<Object, Map<Entry, Object>> readerCache = new WeakHashMap<Object, Map<Entry, Object>>();

        Cache() {
            this.wrapper = null;
        }

        Cache(FieldCacheImpl wrapper) {
            this.wrapper = wrapper;
        }

        protected abstract Object createValue(IndexReader var1, Entry var2, boolean var3) throws IOException;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void purge(IndexReader r) {
            Object readerKey = r.getCoreCacheKey();
            Map<Object, Map<Entry, Object>> map = this.readerCache;
            synchronized (map) {
                this.readerCache.remove(readerKey);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void put(IndexReader reader, Entry key, Object value) {
            Object readerKey = reader.getCoreCacheKey();
            Map<Object, Map<Entry, Object>> map = this.readerCache;
            synchronized (map) {
                Map<Entry, Object> innerCache = this.readerCache.get(readerKey);
                if (innerCache == null) {
                    innerCache = new HashMap<Entry, Object>();
                    this.readerCache.put(readerKey, innerCache);
                    if (reader instanceof SegmentReader) {
                        ((SegmentReader)reader).addCoreClosedListener(this.wrapper.purgeCore);
                    } else {
                        reader.addReaderClosedListener(this.wrapper.purgeReader);
                    }
                }
                if (innerCache.get(key) == null) {
                    innerCache.put(key, value);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Object get(IndexReader reader, Entry key, boolean setDocsWithField) throws IOException {
            Object value;
            Map<Entry, Object> innerCache;
            Object readerKey = reader.getCoreCacheKey();
            Object object = this.readerCache;
            synchronized (object) {
                innerCache = this.readerCache.get(readerKey);
                if (innerCache == null) {
                    innerCache = new HashMap<Entry, Object>();
                    this.readerCache.put(readerKey, innerCache);
                    if (reader instanceof SegmentReader) {
                        ((SegmentReader)reader).addCoreClosedListener(this.wrapper.purgeCore);
                    } else {
                        reader.addReaderClosedListener(this.wrapper.purgeReader);
                    }
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
                        Map<Object, Map<Entry, Object>> map = this.readerCache;
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

    static final class StopFillCacheException
    extends RuntimeException {
        StopFillCacheException() {
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class CacheEntryImpl
    extends FieldCache.CacheEntry {
        private final Object readerKey;
        private final String fieldName;
        private final Class<?> cacheType;
        private final Object custom;
        private final Object value;

        CacheEntryImpl(Object readerKey, String fieldName, Class<?> cacheType, Object custom, Object value) {
            this.readerKey = readerKey;
            this.fieldName = fieldName;
            this.cacheType = cacheType;
            this.custom = custom;
            this.value = value;
        }

        @Override
        public Object getReaderKey() {
            return this.readerKey;
        }

        @Override
        public String getFieldName() {
            return this.fieldName;
        }

        @Override
        public Class<?> getCacheType() {
            return this.cacheType;
        }

        @Override
        public Object getCustom() {
            return this.custom;
        }

        @Override
        public Object getValue() {
            return this.value;
        }
    }
}

