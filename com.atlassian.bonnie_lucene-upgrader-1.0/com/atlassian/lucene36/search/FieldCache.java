/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.FieldCacheImpl;
import com.atlassian.lucene36.util.Bits;
import com.atlassian.lucene36.util.NumericUtils;
import com.atlassian.lucene36.util.RamUsageEstimator;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

public interface FieldCache {
    public static final int STRING_INDEX = -1;
    public static final FieldCache DEFAULT = new FieldCacheImpl();
    public static final ByteParser DEFAULT_BYTE_PARSER = new ByteParser(){

        public byte parseByte(String value) {
            return Byte.parseByte(value);
        }

        protected Object readResolve() {
            return DEFAULT_BYTE_PARSER;
        }

        public String toString() {
            return FieldCache.class.getName() + ".DEFAULT_BYTE_PARSER";
        }
    };
    public static final ShortParser DEFAULT_SHORT_PARSER = new ShortParser(){

        public short parseShort(String value) {
            return Short.parseShort(value);
        }

        protected Object readResolve() {
            return DEFAULT_SHORT_PARSER;
        }

        public String toString() {
            return FieldCache.class.getName() + ".DEFAULT_SHORT_PARSER";
        }
    };
    public static final IntParser DEFAULT_INT_PARSER = new IntParser(){

        public int parseInt(String value) {
            return Integer.parseInt(value);
        }

        protected Object readResolve() {
            return DEFAULT_INT_PARSER;
        }

        public String toString() {
            return FieldCache.class.getName() + ".DEFAULT_INT_PARSER";
        }
    };
    public static final FloatParser DEFAULT_FLOAT_PARSER = new FloatParser(){

        public float parseFloat(String value) {
            return Float.parseFloat(value);
        }

        protected Object readResolve() {
            return DEFAULT_FLOAT_PARSER;
        }

        public String toString() {
            return FieldCache.class.getName() + ".DEFAULT_FLOAT_PARSER";
        }
    };
    public static final LongParser DEFAULT_LONG_PARSER = new LongParser(){

        public long parseLong(String value) {
            return Long.parseLong(value);
        }

        protected Object readResolve() {
            return DEFAULT_LONG_PARSER;
        }

        public String toString() {
            return FieldCache.class.getName() + ".DEFAULT_LONG_PARSER";
        }
    };
    public static final DoubleParser DEFAULT_DOUBLE_PARSER = new DoubleParser(){

        public double parseDouble(String value) {
            return Double.parseDouble(value);
        }

        protected Object readResolve() {
            return DEFAULT_DOUBLE_PARSER;
        }

        public String toString() {
            return FieldCache.class.getName() + ".DEFAULT_DOUBLE_PARSER";
        }
    };
    public static final IntParser NUMERIC_UTILS_INT_PARSER = new IntParser(){

        public int parseInt(String val) {
            int shift = val.charAt(0) - 96;
            if (shift > 0 && shift <= 31) {
                throw new FieldCacheImpl.StopFillCacheException();
            }
            return NumericUtils.prefixCodedToInt(val);
        }

        protected Object readResolve() {
            return NUMERIC_UTILS_INT_PARSER;
        }

        public String toString() {
            return FieldCache.class.getName() + ".NUMERIC_UTILS_INT_PARSER";
        }
    };
    public static final FloatParser NUMERIC_UTILS_FLOAT_PARSER = new FloatParser(){

        public float parseFloat(String val) {
            int shift = val.charAt(0) - 96;
            if (shift > 0 && shift <= 31) {
                throw new FieldCacheImpl.StopFillCacheException();
            }
            return NumericUtils.sortableIntToFloat(NumericUtils.prefixCodedToInt(val));
        }

        protected Object readResolve() {
            return NUMERIC_UTILS_FLOAT_PARSER;
        }

        public String toString() {
            return FieldCache.class.getName() + ".NUMERIC_UTILS_FLOAT_PARSER";
        }
    };
    public static final LongParser NUMERIC_UTILS_LONG_PARSER = new LongParser(){

        public long parseLong(String val) {
            int shift = val.charAt(0) - 32;
            if (shift > 0 && shift <= 63) {
                throw new FieldCacheImpl.StopFillCacheException();
            }
            return NumericUtils.prefixCodedToLong(val);
        }

        protected Object readResolve() {
            return NUMERIC_UTILS_LONG_PARSER;
        }

        public String toString() {
            return FieldCache.class.getName() + ".NUMERIC_UTILS_LONG_PARSER";
        }
    };
    public static final DoubleParser NUMERIC_UTILS_DOUBLE_PARSER = new DoubleParser(){

        public double parseDouble(String val) {
            int shift = val.charAt(0) - 32;
            if (shift > 0 && shift <= 63) {
                throw new FieldCacheImpl.StopFillCacheException();
            }
            return NumericUtils.sortableLongToDouble(NumericUtils.prefixCodedToLong(val));
        }

        protected Object readResolve() {
            return NUMERIC_UTILS_DOUBLE_PARSER;
        }

        public String toString() {
            return FieldCache.class.getName() + ".NUMERIC_UTILS_DOUBLE_PARSER";
        }
    };

    public Bits getDocsWithField(IndexReader var1, String var2) throws IOException;

    public byte[] getBytes(IndexReader var1, String var2) throws IOException;

    public byte[] getBytes(IndexReader var1, String var2, ByteParser var3) throws IOException;

    public byte[] getBytes(IndexReader var1, String var2, ByteParser var3, boolean var4) throws IOException;

    public short[] getShorts(IndexReader var1, String var2) throws IOException;

    public short[] getShorts(IndexReader var1, String var2, ShortParser var3) throws IOException;

    public short[] getShorts(IndexReader var1, String var2, ShortParser var3, boolean var4) throws IOException;

    public int[] getInts(IndexReader var1, String var2) throws IOException;

    public int[] getInts(IndexReader var1, String var2, IntParser var3) throws IOException;

    public int[] getInts(IndexReader var1, String var2, IntParser var3, boolean var4) throws IOException;

    public float[] getFloats(IndexReader var1, String var2) throws IOException;

    public float[] getFloats(IndexReader var1, String var2, FloatParser var3) throws IOException;

    public float[] getFloats(IndexReader var1, String var2, FloatParser var3, boolean var4) throws IOException;

    public long[] getLongs(IndexReader var1, String var2) throws IOException;

    public long[] getLongs(IndexReader var1, String var2, LongParser var3) throws IOException;

    public long[] getLongs(IndexReader var1, String var2, LongParser var3, boolean var4) throws IOException;

    public double[] getDoubles(IndexReader var1, String var2) throws IOException;

    public double[] getDoubles(IndexReader var1, String var2, DoubleParser var3) throws IOException;

    public double[] getDoubles(IndexReader var1, String var2, DoubleParser var3, boolean var4) throws IOException;

    public String[] getStrings(IndexReader var1, String var2) throws IOException;

    public StringIndex getStringIndex(IndexReader var1, String var2) throws IOException;

    public CacheEntry[] getCacheEntries();

    public void purgeAllCaches();

    public void purge(IndexReader var1);

    public void setInfoStream(PrintStream var1);

    public PrintStream getInfoStream();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class CacheEntry {
        private String size = null;

        public abstract Object getReaderKey();

        public abstract String getFieldName();

        public abstract Class<?> getCacheType();

        public abstract Object getCustom();

        public abstract Object getValue();

        protected final void setEstimatedSize(String size) {
            this.size = size;
        }

        public void estimateSize() {
            long size = RamUsageEstimator.sizeOf(this.getValue());
            this.setEstimatedSize(RamUsageEstimator.humanReadableUnits(size));
        }

        public final String getEstimatedSize() {
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
        public double parseDouble(String var1);
    }

    public static interface LongParser
    extends Parser {
        public long parseLong(String var1);
    }

    public static interface FloatParser
    extends Parser {
        public float parseFloat(String var1);
    }

    public static interface IntParser
    extends Parser {
        public int parseInt(String var1);
    }

    public static interface ShortParser
    extends Parser {
        public short parseShort(String var1);
    }

    public static interface ByteParser
    extends Parser {
        public byte parseByte(String var1);
    }

    public static interface Parser
    extends Serializable {
    }

    public static class StringIndex {
        public final String[] lookup;
        public final int[] order;

        public int binarySearchLookup(String key) {
            if (key == null) {
                return 0;
            }
            int low = 1;
            int high = this.lookup.length - 1;
            while (low <= high) {
                int mid = low + high >>> 1;
                int cmp = this.lookup[mid].compareTo(key);
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
            return -(low + 1);
        }

        public StringIndex(int[] values, String[] lookup) {
            this.order = values;
            this.lookup = lookup;
        }
    }

    public static final class CreationPlaceholder {
        Object value;
    }
}

