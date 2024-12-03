/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.FieldCache;
import com.atlassian.lucene36.search.ScoreCachingWrappingScorer;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.util.Bits;
import java.io.IOException;
import java.text.Collator;
import java.util.Locale;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class FieldComparator<T> {
    public abstract int compare(int var1, int var2);

    public abstract void setBottom(int var1);

    public abstract int compareBottom(int var1) throws IOException;

    public abstract void copy(int var1, int var2) throws IOException;

    public abstract void setNextReader(IndexReader var1, int var2) throws IOException;

    public void setScorer(Scorer scorer) {
    }

    public abstract T value(int var1);

    public int compareValues(T first, T second) {
        if (first == null) {
            if (second == null) {
                return 0;
            }
            return -1;
        }
        if (second == null) {
            return 1;
        }
        return ((Comparable)first).compareTo(second);
    }

    protected static final int binarySearch(String[] a, String key) {
        return FieldComparator.binarySearch(a, key, 0, a.length - 1);
    }

    protected static final int binarySearch(String[] a, String key, int low, int high) {
        while (low <= high) {
            int mid = low + high >>> 1;
            String midVal = a[mid];
            int cmp = midVal != null ? midVal.compareTo(key) : -1;
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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class StringValComparator
    extends FieldComparator<String> {
        private String[] values;
        private String[] currentReaderValues;
        private final String field;
        private String bottom;

        StringValComparator(int numHits, String field) {
            this.values = new String[numHits];
            this.field = field;
        }

        @Override
        public int compare(int slot1, int slot2) {
            String val1 = this.values[slot1];
            String val2 = this.values[slot2];
            if (val1 == null) {
                if (val2 == null) {
                    return 0;
                }
                return -1;
            }
            if (val2 == null) {
                return 1;
            }
            return val1.compareTo(val2);
        }

        @Override
        public int compareBottom(int doc) {
            String val2 = this.currentReaderValues[doc];
            if (this.bottom == null) {
                if (val2 == null) {
                    return 0;
                }
                return -1;
            }
            if (val2 == null) {
                return 1;
            }
            return this.bottom.compareTo(val2);
        }

        @Override
        public void copy(int slot, int doc) {
            this.values[slot] = this.currentReaderValues[doc];
        }

        @Override
        public void setNextReader(IndexReader reader, int docBase) throws IOException {
            this.currentReaderValues = FieldCache.DEFAULT.getStrings(reader, this.field);
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.values[bottom];
        }

        @Override
        public String value(int slot) {
            return this.values[slot];
        }

        @Override
        public int compareValues(String val1, String val2) {
            if (val1 == null) {
                if (val2 == null) {
                    return 0;
                }
                return -1;
            }
            if (val2 == null) {
                return 1;
            }
            return val1.compareTo(val2);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class StringOrdValComparator
    extends FieldComparator<String> {
        private final int[] ords;
        private final String[] values;
        private final int[] readerGen;
        private int currentReaderGen = -1;
        private String[] lookup;
        private int[] order;
        private final String field;
        private int bottomSlot = -1;
        private int bottomOrd;
        private boolean bottomSameReader;
        private String bottomValue;

        public StringOrdValComparator(int numHits, String field, int sortPos, boolean reversed) {
            this.ords = new int[numHits];
            this.values = new String[numHits];
            this.readerGen = new int[numHits];
            this.field = field;
        }

        @Override
        public int compare(int slot1, int slot2) {
            if (this.readerGen[slot1] == this.readerGen[slot2]) {
                return this.ords[slot1] - this.ords[slot2];
            }
            String val1 = this.values[slot1];
            String val2 = this.values[slot2];
            if (val1 == null) {
                if (val2 == null) {
                    return 0;
                }
                return -1;
            }
            if (val2 == null) {
                return 1;
            }
            return val1.compareTo(val2);
        }

        @Override
        public int compareBottom(int doc) {
            assert (this.bottomSlot != -1);
            int docOrd = this.order[doc];
            if (this.bottomSameReader) {
                return this.bottomOrd - docOrd;
            }
            if (this.bottomOrd >= docOrd) {
                return 1;
            }
            return -1;
        }

        @Override
        public void copy(int slot, int doc) {
            int ord;
            this.ords[slot] = ord = this.order[doc];
            assert (ord >= 0);
            this.values[slot] = this.lookup[ord];
            this.readerGen[slot] = this.currentReaderGen;
        }

        @Override
        public void setNextReader(IndexReader reader, int docBase) throws IOException {
            FieldCache.StringIndex currentReaderValues = FieldCache.DEFAULT.getStringIndex(reader, this.field);
            ++this.currentReaderGen;
            this.order = currentReaderValues.order;
            this.lookup = currentReaderValues.lookup;
            assert (this.lookup.length > 0);
            if (this.bottomSlot != -1) {
                this.setBottom(this.bottomSlot);
            }
        }

        @Override
        public void setBottom(int bottom) {
            this.bottomSlot = bottom;
            this.bottomValue = this.values[this.bottomSlot];
            if (this.currentReaderGen == this.readerGen[this.bottomSlot]) {
                this.bottomOrd = this.ords[this.bottomSlot];
                this.bottomSameReader = true;
            } else if (this.bottomValue == null) {
                this.ords[this.bottomSlot] = 0;
                this.bottomOrd = 0;
                this.bottomSameReader = true;
                this.readerGen[this.bottomSlot] = this.currentReaderGen;
            } else {
                int index = StringOrdValComparator.binarySearch(this.lookup, this.bottomValue);
                if (index < 0) {
                    this.bottomOrd = -index - 2;
                    this.bottomSameReader = false;
                } else {
                    this.bottomOrd = index;
                    this.bottomSameReader = true;
                    this.readerGen[this.bottomSlot] = this.currentReaderGen;
                    this.ords[this.bottomSlot] = this.bottomOrd;
                }
            }
        }

        @Override
        public String value(int slot) {
            return this.values[slot];
        }

        @Override
        public int compareValues(String val1, String val2) {
            if (val1 == null) {
                if (val2 == null) {
                    return 0;
                }
                return -1;
            }
            if (val2 == null) {
                return 1;
            }
            return val1.compareTo(val2);
        }

        public String[] getValues() {
            return this.values;
        }

        public int getBottomSlot() {
            return this.bottomSlot;
        }

        public String getField() {
            return this.field;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class StringComparatorLocale
    extends FieldComparator<String> {
        private final String[] values;
        private String[] currentReaderValues;
        private final String field;
        final Collator collator;
        private String bottom;

        StringComparatorLocale(int numHits, String field, Locale locale) {
            this.values = new String[numHits];
            this.field = field;
            this.collator = Collator.getInstance(locale);
        }

        @Override
        public int compare(int slot1, int slot2) {
            String val1 = this.values[slot1];
            String val2 = this.values[slot2];
            if (val1 == null) {
                if (val2 == null) {
                    return 0;
                }
                return -1;
            }
            if (val2 == null) {
                return 1;
            }
            return this.collator.compare(val1, val2);
        }

        @Override
        public int compareBottom(int doc) {
            String val2 = this.currentReaderValues[doc];
            if (this.bottom == null) {
                if (val2 == null) {
                    return 0;
                }
                return -1;
            }
            if (val2 == null) {
                return 1;
            }
            return this.collator.compare(this.bottom, val2);
        }

        @Override
        public void copy(int slot, int doc) {
            this.values[slot] = this.currentReaderValues[doc];
        }

        @Override
        public void setNextReader(IndexReader reader, int docBase) throws IOException {
            this.currentReaderValues = FieldCache.DEFAULT.getStrings(reader, this.field);
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.values[bottom];
        }

        @Override
        public String value(int slot) {
            return this.values[slot];
        }

        @Override
        public int compareValues(String val1, String val2) {
            if (val1 == null) {
                if (val2 == null) {
                    return 0;
                }
                return -1;
            }
            if (val2 == null) {
                return 1;
            }
            return this.collator.compare(val1, val2);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class ShortComparator
    extends NumericComparator<Short> {
        private final short[] values;
        private final FieldCache.ShortParser parser;
        private short[] currentReaderValues;
        private short bottom;

        ShortComparator(int numHits, String field, FieldCache.Parser parser, Short missingValue) {
            super(field, missingValue);
            this.values = new short[numHits];
            this.parser = (FieldCache.ShortParser)parser;
        }

        @Override
        public int compare(int slot1, int slot2) {
            return this.values[slot1] - this.values[slot2];
        }

        @Override
        public int compareBottom(int doc) {
            short v2 = this.currentReaderValues[doc];
            if (this.docsWithField != null && v2 == 0 && !this.docsWithField.get(doc)) {
                v2 = (Short)this.missingValue;
            }
            return this.bottom - v2;
        }

        @Override
        public void copy(int slot, int doc) {
            short v2 = this.currentReaderValues[doc];
            if (this.docsWithField != null && v2 == 0 && !this.docsWithField.get(doc)) {
                v2 = (Short)this.missingValue;
            }
            this.values[slot] = v2;
        }

        @Override
        public void setNextReader(IndexReader reader, int docBase) throws IOException {
            this.currentReaderValues = FieldCache.DEFAULT.getShorts(reader, this.field, this.parser, this.missingValue != null);
            super.setNextReader(reader, docBase);
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.values[bottom];
        }

        @Override
        public Short value(int slot) {
            return this.values[slot];
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class RelevanceComparator
    extends FieldComparator<Float> {
        private final float[] scores;
        private float bottom;
        private Scorer scorer;

        RelevanceComparator(int numHits) {
            this.scores = new float[numHits];
        }

        @Override
        public int compare(int slot1, int slot2) {
            float score1 = this.scores[slot1];
            float score2 = this.scores[slot2];
            return score1 > score2 ? -1 : (score1 < score2 ? 1 : 0);
        }

        @Override
        public int compareBottom(int doc) throws IOException {
            float score = this.scorer.score();
            return this.bottom > score ? -1 : (this.bottom < score ? 1 : 0);
        }

        @Override
        public void copy(int slot, int doc) throws IOException {
            this.scores[slot] = this.scorer.score();
        }

        @Override
        public void setNextReader(IndexReader reader, int docBase) {
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.scores[bottom];
        }

        @Override
        public void setScorer(Scorer scorer) {
            this.scorer = !(scorer instanceof ScoreCachingWrappingScorer) ? new ScoreCachingWrappingScorer(scorer) : scorer;
        }

        @Override
        public Float value(int slot) {
            return Float.valueOf(this.scores[slot]);
        }

        @Override
        public int compareValues(Float first, Float second) {
            return second.compareTo(first);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class LongComparator
    extends NumericComparator<Long> {
        private final long[] values;
        private final FieldCache.LongParser parser;
        private long[] currentReaderValues;
        private long bottom;

        LongComparator(int numHits, String field, FieldCache.Parser parser, Long missingValue) {
            super(field, missingValue);
            this.values = new long[numHits];
            this.parser = (FieldCache.LongParser)parser;
        }

        @Override
        public int compare(int slot1, int slot2) {
            long v1 = this.values[slot1];
            long v2 = this.values[slot2];
            if (v1 > v2) {
                return 1;
            }
            if (v1 < v2) {
                return -1;
            }
            return 0;
        }

        @Override
        public int compareBottom(int doc) {
            long v2 = this.currentReaderValues[doc];
            if (this.docsWithField != null && v2 == 0L && !this.docsWithField.get(doc)) {
                v2 = (Long)this.missingValue;
            }
            if (this.bottom > v2) {
                return 1;
            }
            if (this.bottom < v2) {
                return -1;
            }
            return 0;
        }

        @Override
        public void copy(int slot, int doc) {
            long v2 = this.currentReaderValues[doc];
            if (this.docsWithField != null && v2 == 0L && !this.docsWithField.get(doc)) {
                v2 = (Long)this.missingValue;
            }
            this.values[slot] = v2;
        }

        @Override
        public void setNextReader(IndexReader reader, int docBase) throws IOException {
            this.currentReaderValues = FieldCache.DEFAULT.getLongs(reader, this.field, this.parser, this.missingValue != null);
            super.setNextReader(reader, docBase);
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.values[bottom];
        }

        @Override
        public Long value(int slot) {
            return this.values[slot];
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class IntComparator
    extends NumericComparator<Integer> {
        private final int[] values;
        private final FieldCache.IntParser parser;
        private int[] currentReaderValues;
        private int bottom;

        IntComparator(int numHits, String field, FieldCache.Parser parser, Integer missingValue) {
            super(field, missingValue);
            this.values = new int[numHits];
            this.parser = (FieldCache.IntParser)parser;
        }

        @Override
        public int compare(int slot1, int slot2) {
            int v1 = this.values[slot1];
            int v2 = this.values[slot2];
            if (v1 > v2) {
                return 1;
            }
            if (v1 < v2) {
                return -1;
            }
            return 0;
        }

        @Override
        public int compareBottom(int doc) {
            int v2 = this.currentReaderValues[doc];
            if (this.docsWithField != null && v2 == 0 && !this.docsWithField.get(doc)) {
                v2 = (Integer)this.missingValue;
            }
            if (this.bottom > v2) {
                return 1;
            }
            if (this.bottom < v2) {
                return -1;
            }
            return 0;
        }

        @Override
        public void copy(int slot, int doc) {
            int v2 = this.currentReaderValues[doc];
            if (this.docsWithField != null && v2 == 0 && !this.docsWithField.get(doc)) {
                v2 = (Integer)this.missingValue;
            }
            this.values[slot] = v2;
        }

        @Override
        public void setNextReader(IndexReader reader, int docBase) throws IOException {
            this.currentReaderValues = FieldCache.DEFAULT.getInts(reader, this.field, this.parser, this.missingValue != null);
            super.setNextReader(reader, docBase);
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.values[bottom];
        }

        @Override
        public Integer value(int slot) {
            return this.values[slot];
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class FloatComparator
    extends NumericComparator<Float> {
        private final float[] values;
        private final FieldCache.FloatParser parser;
        private float[] currentReaderValues;
        private float bottom;

        FloatComparator(int numHits, String field, FieldCache.Parser parser, Float missingValue) {
            super(field, missingValue);
            this.values = new float[numHits];
            this.parser = (FieldCache.FloatParser)parser;
        }

        @Override
        public int compare(int slot1, int slot2) {
            float v1 = this.values[slot1];
            float v2 = this.values[slot2];
            if (v1 > v2) {
                return 1;
            }
            if (v1 < v2) {
                return -1;
            }
            return 0;
        }

        @Override
        public int compareBottom(int doc) {
            float v2 = this.currentReaderValues[doc];
            if (this.docsWithField != null && v2 == 0.0f && !this.docsWithField.get(doc)) {
                v2 = ((Float)this.missingValue).floatValue();
            }
            if (this.bottom > v2) {
                return 1;
            }
            if (this.bottom < v2) {
                return -1;
            }
            return 0;
        }

        @Override
        public void copy(int slot, int doc) {
            float v2 = this.currentReaderValues[doc];
            if (this.docsWithField != null && v2 == 0.0f && !this.docsWithField.get(doc)) {
                v2 = ((Float)this.missingValue).floatValue();
            }
            this.values[slot] = v2;
        }

        @Override
        public void setNextReader(IndexReader reader, int docBase) throws IOException {
            this.currentReaderValues = FieldCache.DEFAULT.getFloats(reader, this.field, this.parser, this.missingValue != null);
            super.setNextReader(reader, docBase);
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.values[bottom];
        }

        @Override
        public Float value(int slot) {
            return Float.valueOf(this.values[slot]);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class DoubleComparator
    extends NumericComparator<Double> {
        private final double[] values;
        private final FieldCache.DoubleParser parser;
        private double[] currentReaderValues;
        private double bottom;

        DoubleComparator(int numHits, String field, FieldCache.Parser parser, Double missingValue) {
            super(field, missingValue);
            this.values = new double[numHits];
            this.parser = (FieldCache.DoubleParser)parser;
        }

        @Override
        public int compare(int slot1, int slot2) {
            double v1 = this.values[slot1];
            double v2 = this.values[slot2];
            if (v1 > v2) {
                return 1;
            }
            if (v1 < v2) {
                return -1;
            }
            return 0;
        }

        @Override
        public int compareBottom(int doc) {
            double v2 = this.currentReaderValues[doc];
            if (this.docsWithField != null && v2 == 0.0 && !this.docsWithField.get(doc)) {
                v2 = (Double)this.missingValue;
            }
            if (this.bottom > v2) {
                return 1;
            }
            if (this.bottom < v2) {
                return -1;
            }
            return 0;
        }

        @Override
        public void copy(int slot, int doc) {
            double v2 = this.currentReaderValues[doc];
            if (this.docsWithField != null && v2 == 0.0 && !this.docsWithField.get(doc)) {
                v2 = (Double)this.missingValue;
            }
            this.values[slot] = v2;
        }

        @Override
        public void setNextReader(IndexReader reader, int docBase) throws IOException {
            this.currentReaderValues = FieldCache.DEFAULT.getDoubles(reader, this.field, this.parser, this.missingValue != null);
            super.setNextReader(reader, docBase);
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.values[bottom];
        }

        @Override
        public Double value(int slot) {
            return this.values[slot];
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class DocComparator
    extends FieldComparator<Integer> {
        private final int[] docIDs;
        private int docBase;
        private int bottom;

        DocComparator(int numHits) {
            this.docIDs = new int[numHits];
        }

        @Override
        public int compare(int slot1, int slot2) {
            return this.docIDs[slot1] - this.docIDs[slot2];
        }

        @Override
        public int compareBottom(int doc) {
            return this.bottom - (this.docBase + doc);
        }

        @Override
        public void copy(int slot, int doc) {
            this.docIDs[slot] = this.docBase + doc;
        }

        @Override
        public void setNextReader(IndexReader reader, int docBase) {
            this.docBase = docBase;
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.docIDs[bottom];
        }

        @Override
        public Integer value(int slot) {
            return this.docIDs[slot];
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class ByteComparator
    extends NumericComparator<Byte> {
        private final byte[] values;
        private final FieldCache.ByteParser parser;
        private byte[] currentReaderValues;
        private byte bottom;

        ByteComparator(int numHits, String field, FieldCache.Parser parser, Byte missingValue) {
            super(field, missingValue);
            this.values = new byte[numHits];
            this.parser = (FieldCache.ByteParser)parser;
        }

        @Override
        public int compare(int slot1, int slot2) {
            return this.values[slot1] - this.values[slot2];
        }

        @Override
        public int compareBottom(int doc) {
            byte v2 = this.currentReaderValues[doc];
            if (this.docsWithField != null && v2 == 0 && !this.docsWithField.get(doc)) {
                v2 = (Byte)this.missingValue;
            }
            return this.bottom - v2;
        }

        @Override
        public void copy(int slot, int doc) {
            byte v2 = this.currentReaderValues[doc];
            if (this.docsWithField != null && v2 == 0 && !this.docsWithField.get(doc)) {
                v2 = (Byte)this.missingValue;
            }
            this.values[slot] = v2;
        }

        @Override
        public void setNextReader(IndexReader reader, int docBase) throws IOException {
            this.currentReaderValues = FieldCache.DEFAULT.getBytes(reader, this.field, this.parser, this.missingValue != null);
            super.setNextReader(reader, docBase);
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.values[bottom];
        }

        @Override
        public Byte value(int slot) {
            return this.values[slot];
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class NumericComparator<T extends Number>
    extends FieldComparator<T> {
        protected final T missingValue;
        protected final String field;
        protected Bits docsWithField;

        public NumericComparator(String field, T missingValue) {
            this.field = field;
            this.missingValue = missingValue;
        }

        @Override
        public void setNextReader(IndexReader reader, int docBase) throws IOException {
            if (this.missingValue != null) {
                this.docsWithField = FieldCache.DEFAULT.getDocsWithField(reader, this.field);
                if (this.docsWithField instanceof Bits.MatchAllBits) {
                    this.docsWithField = null;
                }
            } else {
                this.docsWithField = null;
            }
        }
    }
}

