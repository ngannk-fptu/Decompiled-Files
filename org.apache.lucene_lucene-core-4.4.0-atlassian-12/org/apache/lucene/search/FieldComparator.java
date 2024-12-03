/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.ScoreCachingWrappingScorer;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

public abstract class FieldComparator<T> {
    public abstract int compare(int var1, int var2);

    public abstract void setBottom(int var1);

    public abstract int compareBottom(int var1) throws IOException;

    public abstract void copy(int var1, int var2) throws IOException;

    public abstract FieldComparator<T> setNextReader(AtomicReaderContext var1) throws IOException;

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

    public abstract int compareDocToValue(int var1, T var2) throws IOException;

    public static final class TermValComparator
    extends FieldComparator<BytesRef> {
        private BytesRef[] values;
        private BinaryDocValues docTerms;
        private final String field;
        private BytesRef bottom;
        private final BytesRef tempBR = new BytesRef();

        TermValComparator(int numHits, String field) {
            this.values = new BytesRef[numHits];
            this.field = field;
        }

        @Override
        public int compare(int slot1, int slot2) {
            BytesRef val1 = this.values[slot1];
            BytesRef val2 = this.values[slot2];
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
            this.docTerms.get(doc, this.tempBR);
            if (this.bottom.bytes == BinaryDocValues.MISSING) {
                if (this.tempBR.bytes == BinaryDocValues.MISSING) {
                    return 0;
                }
                return -1;
            }
            if (this.tempBR.bytes == BinaryDocValues.MISSING) {
                return 1;
            }
            return this.bottom.compareTo(this.tempBR);
        }

        @Override
        public void copy(int slot, int doc) {
            if (this.values[slot] == null) {
                this.values[slot] = new BytesRef();
            }
            this.docTerms.get(doc, this.values[slot]);
        }

        @Override
        public FieldComparator<BytesRef> setNextReader(AtomicReaderContext context) throws IOException {
            this.docTerms = FieldCache.DEFAULT.getTerms(context.reader(), this.field);
            return this;
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.values[bottom];
        }

        @Override
        public BytesRef value(int slot) {
            return this.values[slot];
        }

        @Override
        public int compareValues(BytesRef val1, BytesRef val2) {
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
        public int compareDocToValue(int doc, BytesRef value) {
            this.docTerms.get(doc, this.tempBR);
            return this.tempBR.compareTo(value);
        }
    }

    public static final class TermOrdValComparator
    extends FieldComparator<BytesRef> {
        final int[] ords;
        final BytesRef[] values;
        final int[] readerGen;
        int currentReaderGen = -1;
        SortedDocValues termsIndex;
        private final String field;
        int bottomSlot = -1;
        int bottomOrd;
        boolean bottomSameReader;
        BytesRef bottomValue;
        final BytesRef tempBR = new BytesRef();

        public TermOrdValComparator(int numHits, String field) {
            this.ords = new int[numHits];
            this.values = new BytesRef[numHits];
            this.readerGen = new int[numHits];
            this.field = field;
        }

        @Override
        public int compare(int slot1, int slot2) {
            if (this.readerGen[slot1] == this.readerGen[slot2]) {
                return this.ords[slot1] - this.ords[slot2];
            }
            BytesRef val1 = this.values[slot1];
            BytesRef val2 = this.values[slot2];
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
            throw new UnsupportedOperationException();
        }

        @Override
        public void copy(int slot, int doc) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareDocToValue(int doc, BytesRef value) {
            int ord = this.termsIndex.getOrd(doc);
            if (ord == -1) {
                if (value == null) {
                    return 0;
                }
                return -1;
            }
            if (value == null) {
                return 1;
            }
            this.termsIndex.lookupOrd(ord, this.tempBR);
            return this.tempBR.compareTo(value);
        }

        @Override
        public FieldComparator<BytesRef> setNextReader(AtomicReaderContext context) throws IOException {
            int docBase = context.docBase;
            this.termsIndex = FieldCache.DEFAULT.getTermsIndex(context.reader(), this.field);
            AnyOrdComparator perSegComp = new AnyOrdComparator(this.termsIndex, docBase);
            ++this.currentReaderGen;
            if (this.bottomSlot != -1) {
                ((FieldComparator)perSegComp).setBottom(this.bottomSlot);
            }
            return perSegComp;
        }

        @Override
        public void setBottom(int bottom) {
            this.bottomSlot = bottom;
            this.bottomValue = this.values[this.bottomSlot];
            if (this.currentReaderGen == this.readerGen[this.bottomSlot]) {
                this.bottomOrd = this.ords[this.bottomSlot];
                this.bottomSameReader = true;
            } else if (this.bottomValue == null) {
                assert (this.ords[this.bottomSlot] == -1);
                this.bottomOrd = -1;
                this.bottomSameReader = true;
                this.readerGen[this.bottomSlot] = this.currentReaderGen;
            } else {
                int index = this.termsIndex.lookupTerm(this.bottomValue);
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
        public BytesRef value(int slot) {
            return this.values[slot];
        }

        private final class AnyOrdComparator
        extends PerSegmentComparator {
            private final SortedDocValues termsIndex;
            private final int docBase;

            public AnyOrdComparator(SortedDocValues termsIndex, int docBase) {
                this.termsIndex = termsIndex;
                this.docBase = docBase;
            }

            @Override
            public int compareBottom(int doc) {
                assert (TermOrdValComparator.this.bottomSlot != -1);
                int docOrd = this.termsIndex.getOrd(doc);
                if (TermOrdValComparator.this.bottomSameReader) {
                    return TermOrdValComparator.this.bottomOrd - docOrd;
                }
                if (TermOrdValComparator.this.bottomOrd >= docOrd) {
                    return 1;
                }
                return -1;
            }

            @Override
            public void copy(int slot, int doc) {
                int ord;
                TermOrdValComparator.this.ords[slot] = ord = this.termsIndex.getOrd(doc);
                if (ord == -1) {
                    TermOrdValComparator.this.values[slot] = null;
                } else {
                    assert (ord >= 0);
                    if (TermOrdValComparator.this.values[slot] == null) {
                        TermOrdValComparator.this.values[slot] = new BytesRef();
                    }
                    this.termsIndex.lookupOrd(ord, TermOrdValComparator.this.values[slot]);
                }
                TermOrdValComparator.this.readerGen[slot] = TermOrdValComparator.this.currentReaderGen;
            }
        }

        abstract class PerSegmentComparator
        extends FieldComparator<BytesRef> {
            PerSegmentComparator() {
            }

            @Override
            public FieldComparator<BytesRef> setNextReader(AtomicReaderContext context) throws IOException {
                return TermOrdValComparator.this.setNextReader(context);
            }

            @Override
            public int compare(int slot1, int slot2) {
                return TermOrdValComparator.this.compare(slot1, slot2);
            }

            @Override
            public void setBottom(int bottom) {
                TermOrdValComparator.this.setBottom(bottom);
            }

            @Override
            public BytesRef value(int slot) {
                return TermOrdValComparator.this.value(slot);
            }

            @Override
            public int compareValues(BytesRef val1, BytesRef val2) {
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
            public int compareDocToValue(int doc, BytesRef value) {
                return TermOrdValComparator.this.compareDocToValue(doc, value);
            }
        }
    }

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
        public FieldComparator<Integer> setNextReader(AtomicReaderContext context) {
            this.docBase = context.docBase;
            return this;
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.docIDs[bottom];
        }

        @Override
        public Integer value(int slot) {
            return this.docIDs[slot];
        }

        @Override
        public int compareDocToValue(int doc, Integer valueObj) {
            int docValue = this.docBase + doc;
            int value = valueObj;
            if (docValue < value) {
                return -1;
            }
            if (docValue > value) {
                return 1;
            }
            return 0;
        }
    }

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
            return Float.compare(this.scores[slot2], this.scores[slot1]);
        }

        @Override
        public int compareBottom(int doc) throws IOException {
            float score = this.scorer.score();
            assert (!Float.isNaN(score));
            return Float.compare(score, this.bottom);
        }

        @Override
        public void copy(int slot, int doc) throws IOException {
            this.scores[slot] = this.scorer.score();
            assert (!Float.isNaN(this.scores[slot]));
        }

        @Override
        public FieldComparator<Float> setNextReader(AtomicReaderContext context) {
            return this;
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

        @Override
        public int compareDocToValue(int doc, Float valueObj) throws IOException {
            float value = valueObj.floatValue();
            float docValue = this.scorer.score();
            assert (!Float.isNaN(docValue));
            return Float.compare(value, docValue);
        }
    }

    public static final class LongComparator
    extends NumericComparator<Long> {
        private final long[] values;
        private final FieldCache.LongParser parser;
        private FieldCache.Longs currentReaderValues;
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
            long v2 = this.currentReaderValues.get(doc);
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
            long v2 = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && v2 == 0L && !this.docsWithField.get(doc)) {
                v2 = (Long)this.missingValue;
            }
            this.values[slot] = v2;
        }

        @Override
        public FieldComparator<Long> setNextReader(AtomicReaderContext context) throws IOException {
            this.currentReaderValues = FieldCache.DEFAULT.getLongs(context.reader(), this.field, this.parser, this.missingValue != null);
            return super.setNextReader(context);
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.values[bottom];
        }

        @Override
        public Long value(int slot) {
            return this.values[slot];
        }

        @Override
        public int compareDocToValue(int doc, Long valueObj) {
            long value = valueObj;
            long docValue = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && docValue == 0L && !this.docsWithField.get(doc)) {
                docValue = (Long)this.missingValue;
            }
            if (docValue < value) {
                return -1;
            }
            if (docValue > value) {
                return 1;
            }
            return 0;
        }
    }

    public static final class IntComparator
    extends NumericComparator<Integer> {
        private final int[] values;
        private final FieldCache.IntParser parser;
        private FieldCache.Ints currentReaderValues;
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
            int v2 = this.currentReaderValues.get(doc);
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
            int v2 = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && v2 == 0 && !this.docsWithField.get(doc)) {
                v2 = (Integer)this.missingValue;
            }
            this.values[slot] = v2;
        }

        @Override
        public FieldComparator<Integer> setNextReader(AtomicReaderContext context) throws IOException {
            this.currentReaderValues = FieldCache.DEFAULT.getInts(context.reader(), this.field, this.parser, this.missingValue != null);
            return super.setNextReader(context);
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.values[bottom];
        }

        @Override
        public Integer value(int slot) {
            return this.values[slot];
        }

        @Override
        public int compareDocToValue(int doc, Integer valueObj) {
            int value = valueObj;
            int docValue = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && docValue == 0 && !this.docsWithField.get(doc)) {
                docValue = (Integer)this.missingValue;
            }
            if (docValue < value) {
                return -1;
            }
            if (docValue > value) {
                return 1;
            }
            return 0;
        }
    }

    @Deprecated
    public static final class ShortComparator
    extends NumericComparator<Short> {
        private final short[] values;
        private final FieldCache.ShortParser parser;
        private FieldCache.Shorts currentReaderValues;
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
            short v2 = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && v2 == 0 && !this.docsWithField.get(doc)) {
                v2 = (Short)this.missingValue;
            }
            return this.bottom - v2;
        }

        @Override
        public void copy(int slot, int doc) {
            short v2 = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && v2 == 0 && !this.docsWithField.get(doc)) {
                v2 = (Short)this.missingValue;
            }
            this.values[slot] = v2;
        }

        @Override
        public FieldComparator<Short> setNextReader(AtomicReaderContext context) throws IOException {
            this.currentReaderValues = FieldCache.DEFAULT.getShorts(context.reader(), this.field, this.parser, this.missingValue != null);
            return super.setNextReader(context);
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.values[bottom];
        }

        @Override
        public Short value(int slot) {
            return this.values[slot];
        }

        @Override
        public int compareDocToValue(int doc, Short valueObj) {
            short value = valueObj;
            short docValue = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && docValue == 0 && !this.docsWithField.get(doc)) {
                docValue = (Short)this.missingValue;
            }
            return docValue - value;
        }
    }

    public static final class FloatComparator
    extends NumericComparator<Float> {
        private final float[] values;
        private final FieldCache.FloatParser parser;
        private FieldCache.Floats currentReaderValues;
        private float bottom;

        FloatComparator(int numHits, String field, FieldCache.Parser parser, Float missingValue) {
            super(field, missingValue);
            this.values = new float[numHits];
            this.parser = (FieldCache.FloatParser)parser;
        }

        @Override
        public int compare(int slot1, int slot2) {
            return Float.compare(this.values[slot1], this.values[slot2]);
        }

        @Override
        public int compareBottom(int doc) {
            float v2 = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && v2 == 0.0f && !this.docsWithField.get(doc)) {
                v2 = ((Float)this.missingValue).floatValue();
            }
            return Float.compare(this.bottom, v2);
        }

        @Override
        public void copy(int slot, int doc) {
            float v2 = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && v2 == 0.0f && !this.docsWithField.get(doc)) {
                v2 = ((Float)this.missingValue).floatValue();
            }
            this.values[slot] = v2;
        }

        @Override
        public FieldComparator<Float> setNextReader(AtomicReaderContext context) throws IOException {
            this.currentReaderValues = FieldCache.DEFAULT.getFloats(context.reader(), this.field, this.parser, this.missingValue != null);
            return super.setNextReader(context);
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.values[bottom];
        }

        @Override
        public Float value(int slot) {
            return Float.valueOf(this.values[slot]);
        }

        @Override
        public int compareDocToValue(int doc, Float valueObj) {
            float value = valueObj.floatValue();
            float docValue = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && docValue == 0.0f && !this.docsWithField.get(doc)) {
                docValue = ((Float)this.missingValue).floatValue();
            }
            return Float.compare(docValue, value);
        }
    }

    public static final class DoubleComparator
    extends NumericComparator<Double> {
        private final double[] values;
        private final FieldCache.DoubleParser parser;
        private FieldCache.Doubles currentReaderValues;
        private double bottom;

        DoubleComparator(int numHits, String field, FieldCache.Parser parser, Double missingValue) {
            super(field, missingValue);
            this.values = new double[numHits];
            this.parser = (FieldCache.DoubleParser)parser;
        }

        @Override
        public int compare(int slot1, int slot2) {
            return Double.compare(this.values[slot1], this.values[slot2]);
        }

        @Override
        public int compareBottom(int doc) {
            double v2 = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && v2 == 0.0 && !this.docsWithField.get(doc)) {
                v2 = (Double)this.missingValue;
            }
            return Double.compare(this.bottom, v2);
        }

        @Override
        public void copy(int slot, int doc) {
            double v2 = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && v2 == 0.0 && !this.docsWithField.get(doc)) {
                v2 = (Double)this.missingValue;
            }
            this.values[slot] = v2;
        }

        @Override
        public FieldComparator<Double> setNextReader(AtomicReaderContext context) throws IOException {
            this.currentReaderValues = FieldCache.DEFAULT.getDoubles(context.reader(), this.field, this.parser, this.missingValue != null);
            return super.setNextReader(context);
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.values[bottom];
        }

        @Override
        public Double value(int slot) {
            return this.values[slot];
        }

        @Override
        public int compareDocToValue(int doc, Double valueObj) {
            double value = valueObj;
            double docValue = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && docValue == 0.0 && !this.docsWithField.get(doc)) {
                docValue = (Double)this.missingValue;
            }
            return Double.compare(docValue, value);
        }
    }

    @Deprecated
    public static final class ByteComparator
    extends NumericComparator<Byte> {
        private final byte[] values;
        private final FieldCache.ByteParser parser;
        private FieldCache.Bytes currentReaderValues;
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
            byte v2 = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && v2 == 0 && !this.docsWithField.get(doc)) {
                v2 = (Byte)this.missingValue;
            }
            return this.bottom - v2;
        }

        @Override
        public void copy(int slot, int doc) {
            byte v2 = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && v2 == 0 && !this.docsWithField.get(doc)) {
                v2 = (Byte)this.missingValue;
            }
            this.values[slot] = v2;
        }

        @Override
        public FieldComparator<Byte> setNextReader(AtomicReaderContext context) throws IOException {
            this.currentReaderValues = FieldCache.DEFAULT.getBytes(context.reader(), this.field, this.parser, this.missingValue != null);
            return super.setNextReader(context);
        }

        @Override
        public void setBottom(int bottom) {
            this.bottom = this.values[bottom];
        }

        @Override
        public Byte value(int slot) {
            return this.values[slot];
        }

        @Override
        public int compareDocToValue(int doc, Byte value) {
            byte docValue = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && docValue == 0 && !this.docsWithField.get(doc)) {
                docValue = (Byte)this.missingValue;
            }
            return docValue - value;
        }
    }

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
        public FieldComparator<T> setNextReader(AtomicReaderContext context) throws IOException {
            if (this.missingValue != null) {
                this.docsWithField = FieldCache.DEFAULT.getDocsWithField(context.reader(), this.field);
                if (this.docsWithField instanceof Bits.MatchAllBits) {
                    this.docsWithField = null;
                }
            } else {
                this.docsWithField = null;
            }
            return this;
        }
    }
}

