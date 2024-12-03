/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.io.PrintStream;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.FieldCacheImpl;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.RamUsageEstimator;

public interface FieldCache {
    public static final SortedDocValues EMPTY_TERMSINDEX = new SortedDocValues(){

        @Override
        public int getOrd(int docID) {
            return -1;
        }

        @Override
        public void lookupOrd(int ord, BytesRef result) {
            result.bytes = MISSING;
            result.offset = 0;
            result.length = 0;
        }

        @Override
        public int getValueCount() {
            return 0;
        }
    };
    public static final FieldCache DEFAULT = new FieldCacheImpl();
    @Deprecated
    public static final ByteParser DEFAULT_BYTE_PARSER = new ByteParser(){

        @Override
        public byte parseByte(BytesRef term) {
            return Byte.parseByte(term.utf8ToString());
        }

        public String toString() {
            return FieldCache.class.getName() + ".DEFAULT_BYTE_PARSER";
        }

        @Override
        public TermsEnum termsEnum(Terms terms) throws IOException {
            return terms.iterator(null);
        }
    };
    @Deprecated
    public static final ShortParser DEFAULT_SHORT_PARSER = new ShortParser(){

        @Override
        public short parseShort(BytesRef term) {
            return Short.parseShort(term.utf8ToString());
        }

        public String toString() {
            return FieldCache.class.getName() + ".DEFAULT_SHORT_PARSER";
        }

        @Override
        public TermsEnum termsEnum(Terms terms) throws IOException {
            return terms.iterator(null);
        }
    };
    @Deprecated
    public static final IntParser DEFAULT_INT_PARSER = new IntParser(){

        @Override
        public int parseInt(BytesRef term) {
            return Integer.parseInt(term.utf8ToString());
        }

        @Override
        public TermsEnum termsEnum(Terms terms) throws IOException {
            return terms.iterator(null);
        }

        public String toString() {
            return FieldCache.class.getName() + ".DEFAULT_INT_PARSER";
        }
    };
    @Deprecated
    public static final FloatParser DEFAULT_FLOAT_PARSER = new FloatParser(){

        @Override
        public float parseFloat(BytesRef term) {
            return Float.parseFloat(term.utf8ToString());
        }

        @Override
        public TermsEnum termsEnum(Terms terms) throws IOException {
            return terms.iterator(null);
        }

        public String toString() {
            return FieldCache.class.getName() + ".DEFAULT_FLOAT_PARSER";
        }
    };
    @Deprecated
    public static final LongParser DEFAULT_LONG_PARSER = new LongParser(){

        @Override
        public long parseLong(BytesRef term) {
            return Long.parseLong(term.utf8ToString());
        }

        @Override
        public TermsEnum termsEnum(Terms terms) throws IOException {
            return terms.iterator(null);
        }

        public String toString() {
            return FieldCache.class.getName() + ".DEFAULT_LONG_PARSER";
        }
    };
    @Deprecated
    public static final DoubleParser DEFAULT_DOUBLE_PARSER = new DoubleParser(){

        @Override
        public double parseDouble(BytesRef term) {
            return Double.parseDouble(term.utf8ToString());
        }

        @Override
        public TermsEnum termsEnum(Terms terms) throws IOException {
            return terms.iterator(null);
        }

        public String toString() {
            return FieldCache.class.getName() + ".DEFAULT_DOUBLE_PARSER";
        }
    };
    public static final IntParser NUMERIC_UTILS_INT_PARSER = new IntParser(){

        @Override
        public int parseInt(BytesRef term) {
            return NumericUtils.prefixCodedToInt(term);
        }

        @Override
        public TermsEnum termsEnum(Terms terms) throws IOException {
            return NumericUtils.filterPrefixCodedInts(terms.iterator(null));
        }

        public String toString() {
            return FieldCache.class.getName() + ".NUMERIC_UTILS_INT_PARSER";
        }
    };
    public static final FloatParser NUMERIC_UTILS_FLOAT_PARSER = new FloatParser(){

        @Override
        public float parseFloat(BytesRef term) {
            return NumericUtils.sortableIntToFloat(NumericUtils.prefixCodedToInt(term));
        }

        public String toString() {
            return FieldCache.class.getName() + ".NUMERIC_UTILS_FLOAT_PARSER";
        }

        @Override
        public TermsEnum termsEnum(Terms terms) throws IOException {
            return NumericUtils.filterPrefixCodedInts(terms.iterator(null));
        }
    };
    public static final LongParser NUMERIC_UTILS_LONG_PARSER = new LongParser(){

        @Override
        public long parseLong(BytesRef term) {
            return NumericUtils.prefixCodedToLong(term);
        }

        public String toString() {
            return FieldCache.class.getName() + ".NUMERIC_UTILS_LONG_PARSER";
        }

        @Override
        public TermsEnum termsEnum(Terms terms) throws IOException {
            return NumericUtils.filterPrefixCodedLongs(terms.iterator(null));
        }
    };
    public static final DoubleParser NUMERIC_UTILS_DOUBLE_PARSER = new DoubleParser(){

        @Override
        public double parseDouble(BytesRef term) {
            return NumericUtils.sortableLongToDouble(NumericUtils.prefixCodedToLong(term));
        }

        public String toString() {
            return FieldCache.class.getName() + ".NUMERIC_UTILS_DOUBLE_PARSER";
        }

        @Override
        public TermsEnum termsEnum(Terms terms) throws IOException {
            return NumericUtils.filterPrefixCodedLongs(terms.iterator(null));
        }
    };

    public Bits getDocsWithField(AtomicReader var1, String var2) throws IOException;

    @Deprecated
    public Bytes getBytes(AtomicReader var1, String var2, boolean var3) throws IOException;

    @Deprecated
    public Bytes getBytes(AtomicReader var1, String var2, ByteParser var3, boolean var4) throws IOException;

    @Deprecated
    public Shorts getShorts(AtomicReader var1, String var2, boolean var3) throws IOException;

    @Deprecated
    public Shorts getShorts(AtomicReader var1, String var2, ShortParser var3, boolean var4) throws IOException;

    public Ints getInts(AtomicReader var1, String var2, boolean var3) throws IOException;

    public Ints getInts(AtomicReader var1, String var2, IntParser var3, boolean var4) throws IOException;

    public Floats getFloats(AtomicReader var1, String var2, boolean var3) throws IOException;

    public Floats getFloats(AtomicReader var1, String var2, FloatParser var3, boolean var4) throws IOException;

    public Longs getLongs(AtomicReader var1, String var2, boolean var3) throws IOException;

    public Longs getLongs(AtomicReader var1, String var2, LongParser var3, boolean var4) throws IOException;

    public Doubles getDoubles(AtomicReader var1, String var2, boolean var3) throws IOException;

    public Doubles getDoubles(AtomicReader var1, String var2, DoubleParser var3, boolean var4) throws IOException;

    public BinaryDocValues getTerms(AtomicReader var1, String var2) throws IOException;

    public BinaryDocValues getTerms(AtomicReader var1, String var2, float var3) throws IOException;

    public SortedDocValues getTermsIndex(AtomicReader var1, String var2) throws IOException;

    public SortedDocValues getTermsIndex(AtomicReader var1, String var2, float var3) throws IOException;

    public SortedSetDocValues getDocTermOrds(AtomicReader var1, String var2) throws IOException;

    public CacheEntry[] getCacheEntries();

    public void purgeAllCaches();

    public void purge(AtomicReader var1);

    public void setInfoStream(PrintStream var1);

    public PrintStream getInfoStream();

    public static final class CacheEntry {
        private final Object readerKey;
        private final String fieldName;
        private final Class<?> cacheType;
        private final Object custom;
        private final Object value;
        private String size;

        public CacheEntry(Object readerKey, String fieldName, Class<?> cacheType, Object custom, Object value) {
            this.readerKey = readerKey;
            this.fieldName = fieldName;
            this.cacheType = cacheType;
            this.custom = custom;
            this.value = value;
        }

        public Object getReaderKey() {
            return this.readerKey;
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public Class<?> getCacheType() {
            return this.cacheType;
        }

        public Object getCustom() {
            return this.custom;
        }

        public Object getValue() {
            return this.value;
        }

        public void estimateSize() {
            long bytesUsed = RamUsageEstimator.sizeOf(this.getValue());
            this.size = RamUsageEstimator.humanReadableUnits(bytesUsed);
        }

        public String getEstimatedSize() {
            return this.size;
        }

        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("'").append(this.getReaderKey()).append("'=>");
            b.append("'").append(this.getFieldName()).append("',");
            b.append(this.getCacheType()).append(",").append(this.getCustom());
            b.append("=>").append(this.getValue().getClass().getName()).append("#");
            b.append(System.identityHashCode(this.getValue()));
            String s = this.getEstimatedSize();
            if (null != s) {
                b.append(" (size =~ ").append(s).append(')');
            }
            return b.toString();
        }
    }

    public static interface DoubleParser
    extends Parser {
        public double parseDouble(BytesRef var1);
    }

    public static interface LongParser
    extends Parser {
        public long parseLong(BytesRef var1);
    }

    public static interface FloatParser
    extends Parser {
        public float parseFloat(BytesRef var1);
    }

    public static interface IntParser
    extends Parser {
        public int parseInt(BytesRef var1);
    }

    @Deprecated
    public static interface ShortParser
    extends Parser {
        public short parseShort(BytesRef var1);
    }

    @Deprecated
    public static interface ByteParser
    extends Parser {
        public byte parseByte(BytesRef var1);
    }

    public static interface Parser {
        public TermsEnum termsEnum(Terms var1) throws IOException;
    }

    public static final class CreationPlaceholder {
        Object value;
    }

    public static abstract class Doubles {
        public static final Doubles EMPTY = new Doubles(){

            @Override
            public double get(int docID) {
                return 0.0;
            }
        };

        public abstract double get(int var1);
    }

    public static abstract class Floats {
        public static final Floats EMPTY = new Floats(){

            @Override
            public float get(int docID) {
                return 0.0f;
            }
        };

        public abstract float get(int var1);
    }

    public static abstract class Longs {
        public static final Longs EMPTY = new Longs(){

            @Override
            public long get(int docID) {
                return 0L;
            }
        };

        public abstract long get(int var1);
    }

    public static abstract class Ints {
        public static final Ints EMPTY = new Ints(){

            @Override
            public int get(int docID) {
                return 0;
            }
        };

        public abstract int get(int var1);
    }

    public static abstract class Shorts {
        public static final Shorts EMPTY = new Shorts(){

            @Override
            public short get(int docID) {
                return 0;
            }
        };

        public abstract short get(int var1);
    }

    public static abstract class Bytes {
        public static final Bytes EMPTY = new Bytes(){

            @Override
            public byte get(int docID) {
                return 0;
            }
        };

        public abstract byte get(int var1);
    }
}

