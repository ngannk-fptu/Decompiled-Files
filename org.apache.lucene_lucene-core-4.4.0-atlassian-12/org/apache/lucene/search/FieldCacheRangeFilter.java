/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldCacheDocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

public abstract class FieldCacheRangeFilter<T>
extends Filter {
    final String field;
    final FieldCache.Parser parser;
    final T lowerVal;
    final T upperVal;
    final boolean includeLower;
    final boolean includeUpper;

    private FieldCacheRangeFilter(String field, FieldCache.Parser parser, T lowerVal, T upperVal, boolean includeLower, boolean includeUpper) {
        this.field = field;
        this.parser = parser;
        this.lowerVal = lowerVal;
        this.upperVal = upperVal;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
    }

    @Override
    public abstract DocIdSet getDocIdSet(AtomicReaderContext var1, Bits var2) throws IOException;

    public static FieldCacheRangeFilter<String> newStringRange(String field, String lowerVal, String upperVal, boolean includeLower, boolean includeUpper) {
        return new FieldCacheRangeFilter<String>(field, null, lowerVal, upperVal, includeLower, includeUpper){

            @Override
            public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
                int upperPoint;
                final SortedDocValues fcsi = FieldCache.DEFAULT.getTermsIndex(context.reader(), this.field);
                int lowerPoint = this.lowerVal == null ? -1 : fcsi.lookupTerm(new BytesRef((CharSequence)this.lowerVal));
                int n = upperPoint = this.upperVal == null ? -1 : fcsi.lookupTerm(new BytesRef((CharSequence)this.upperVal));
                final int inclusiveLowerPoint = lowerPoint == -1 && this.lowerVal == null ? 0 : (this.includeLower && lowerPoint >= 0 ? lowerPoint : (lowerPoint >= 0 ? lowerPoint + 1 : Math.max(0, -lowerPoint - 1)));
                final int inclusiveUpperPoint = upperPoint == -1 && this.upperVal == null ? Integer.MAX_VALUE : (this.includeUpper && upperPoint >= 0 ? upperPoint : (upperPoint >= 0 ? upperPoint - 1 : -upperPoint - 2));
                if (inclusiveUpperPoint < 0 || inclusiveLowerPoint > inclusiveUpperPoint) {
                    return null;
                }
                assert (inclusiveLowerPoint >= 0 && inclusiveUpperPoint >= 0);
                return new FieldCacheDocIdSet(context.reader().maxDoc(), acceptDocs){

                    @Override
                    protected final boolean matchDoc(int doc) {
                        int docOrd = fcsi.getOrd(doc);
                        return docOrd >= inclusiveLowerPoint && docOrd <= inclusiveUpperPoint;
                    }
                };
            }
        };
    }

    public static FieldCacheRangeFilter<BytesRef> newBytesRefRange(String field, BytesRef lowerVal, BytesRef upperVal, boolean includeLower, boolean includeUpper) {
        return new FieldCacheRangeFilter<BytesRef>(field, null, lowerVal, upperVal, includeLower, includeUpper){

            @Override
            public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
                int upperPoint;
                final SortedDocValues fcsi = FieldCache.DEFAULT.getTermsIndex(context.reader(), this.field);
                int lowerPoint = this.lowerVal == null ? -1 : fcsi.lookupTerm((BytesRef)this.lowerVal);
                int n = upperPoint = this.upperVal == null ? -1 : fcsi.lookupTerm((BytesRef)this.upperVal);
                final int inclusiveLowerPoint = lowerPoint == -1 && this.lowerVal == null ? 0 : (this.includeLower && lowerPoint >= 0 ? lowerPoint : (lowerPoint >= 0 ? lowerPoint + 1 : Math.max(0, -lowerPoint - 1)));
                final int inclusiveUpperPoint = upperPoint == -1 && this.upperVal == null ? Integer.MAX_VALUE : (this.includeUpper && upperPoint >= 0 ? upperPoint : (upperPoint >= 0 ? upperPoint - 1 : -upperPoint - 2));
                if (inclusiveUpperPoint < 0 || inclusiveLowerPoint > inclusiveUpperPoint) {
                    return null;
                }
                assert (inclusiveLowerPoint >= 0 && inclusiveUpperPoint >= 0);
                return new FieldCacheDocIdSet(context.reader().maxDoc(), acceptDocs){

                    @Override
                    protected final boolean matchDoc(int doc) {
                        int docOrd = fcsi.getOrd(doc);
                        return docOrd >= inclusiveLowerPoint && docOrd <= inclusiveUpperPoint;
                    }
                };
            }
        };
    }

    @Deprecated
    public static FieldCacheRangeFilter<Byte> newByteRange(String field, Byte lowerVal, Byte upperVal, boolean includeLower, boolean includeUpper) {
        return FieldCacheRangeFilter.newByteRange(field, null, lowerVal, upperVal, includeLower, includeUpper);
    }

    @Deprecated
    public static FieldCacheRangeFilter<Byte> newByteRange(String field, FieldCache.ByteParser parser, Byte lowerVal, Byte upperVal, boolean includeLower, boolean includeUpper) {
        return new FieldCacheRangeFilter<Byte>(field, (FieldCache.Parser)parser, lowerVal, upperVal, includeLower, includeUpper){

            @Override
            public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
                byte inclusiveUpperPoint;
                byte inclusiveLowerPoint;
                int i;
                if (this.lowerVal != null) {
                    i = ((Byte)this.lowerVal).byteValue();
                    if (!this.includeLower && i == 127) {
                        return null;
                    }
                    inclusiveLowerPoint = (byte)(this.includeLower ? i : i + 1);
                } else {
                    inclusiveLowerPoint = -128;
                }
                if (this.upperVal != null) {
                    i = ((Byte)this.upperVal).byteValue();
                    if (!this.includeUpper && i == -128) {
                        return null;
                    }
                    inclusiveUpperPoint = (byte)(this.includeUpper ? i : i - 1);
                } else {
                    inclusiveUpperPoint = 127;
                }
                if (inclusiveLowerPoint > inclusiveUpperPoint) {
                    return null;
                }
                final FieldCache.Bytes values = FieldCache.DEFAULT.getBytes(context.reader(), this.field, (FieldCache.ByteParser)this.parser, false);
                return new FieldCacheDocIdSet(context.reader().maxDoc(), acceptDocs){

                    @Override
                    protected boolean matchDoc(int doc) {
                        byte value = values.get(doc);
                        return value >= inclusiveLowerPoint && value <= inclusiveUpperPoint;
                    }
                };
            }
        };
    }

    @Deprecated
    public static FieldCacheRangeFilter<Short> newShortRange(String field, Short lowerVal, Short upperVal, boolean includeLower, boolean includeUpper) {
        return FieldCacheRangeFilter.newShortRange(field, null, lowerVal, upperVal, includeLower, includeUpper);
    }

    @Deprecated
    public static FieldCacheRangeFilter<Short> newShortRange(String field, FieldCache.ShortParser parser, Short lowerVal, Short upperVal, boolean includeLower, boolean includeUpper) {
        return new FieldCacheRangeFilter<Short>(field, (FieldCache.Parser)parser, lowerVal, upperVal, includeLower, includeUpper){

            @Override
            public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
                short inclusiveUpperPoint;
                short inclusiveLowerPoint;
                int i;
                if (this.lowerVal != null) {
                    i = ((Short)this.lowerVal).shortValue();
                    if (!this.includeLower && i == Short.MAX_VALUE) {
                        return null;
                    }
                    inclusiveLowerPoint = (short)(this.includeLower ? i : i + 1);
                } else {
                    inclusiveLowerPoint = Short.MIN_VALUE;
                }
                if (this.upperVal != null) {
                    i = ((Short)this.upperVal).shortValue();
                    if (!this.includeUpper && i == Short.MIN_VALUE) {
                        return null;
                    }
                    inclusiveUpperPoint = (short)(this.includeUpper ? i : i - 1);
                } else {
                    inclusiveUpperPoint = Short.MAX_VALUE;
                }
                if (inclusiveLowerPoint > inclusiveUpperPoint) {
                    return null;
                }
                final FieldCache.Shorts values = FieldCache.DEFAULT.getShorts(context.reader(), this.field, (FieldCache.ShortParser)this.parser, false);
                return new FieldCacheDocIdSet(context.reader().maxDoc(), acceptDocs){

                    @Override
                    protected boolean matchDoc(int doc) {
                        short value = values.get(doc);
                        return value >= inclusiveLowerPoint && value <= inclusiveUpperPoint;
                    }
                };
            }
        };
    }

    public static FieldCacheRangeFilter<Integer> newIntRange(String field, Integer lowerVal, Integer upperVal, boolean includeLower, boolean includeUpper) {
        return FieldCacheRangeFilter.newIntRange(field, null, lowerVal, upperVal, includeLower, includeUpper);
    }

    public static FieldCacheRangeFilter<Integer> newIntRange(String field, FieldCache.IntParser parser, Integer lowerVal, Integer upperVal, boolean includeLower, boolean includeUpper) {
        return new FieldCacheRangeFilter<Integer>(field, (FieldCache.Parser)parser, lowerVal, upperVal, includeLower, includeUpper){

            @Override
            public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
                int inclusiveUpperPoint;
                int inclusiveLowerPoint;
                int i;
                if (this.lowerVal != null) {
                    i = (Integer)this.lowerVal;
                    if (!this.includeLower && i == Integer.MAX_VALUE) {
                        return null;
                    }
                    inclusiveLowerPoint = this.includeLower ? i : i + 1;
                } else {
                    inclusiveLowerPoint = Integer.MIN_VALUE;
                }
                if (this.upperVal != null) {
                    i = (Integer)this.upperVal;
                    if (!this.includeUpper && i == Integer.MIN_VALUE) {
                        return null;
                    }
                    inclusiveUpperPoint = this.includeUpper ? i : i - 1;
                } else {
                    inclusiveUpperPoint = Integer.MAX_VALUE;
                }
                if (inclusiveLowerPoint > inclusiveUpperPoint) {
                    return null;
                }
                final FieldCache.Ints values = FieldCache.DEFAULT.getInts(context.reader(), this.field, (FieldCache.IntParser)this.parser, false);
                return new FieldCacheDocIdSet(context.reader().maxDoc(), acceptDocs){

                    @Override
                    protected boolean matchDoc(int doc) {
                        int value = values.get(doc);
                        return value >= inclusiveLowerPoint && value <= inclusiveUpperPoint;
                    }
                };
            }
        };
    }

    public static FieldCacheRangeFilter<Long> newLongRange(String field, Long lowerVal, Long upperVal, boolean includeLower, boolean includeUpper) {
        return FieldCacheRangeFilter.newLongRange(field, null, lowerVal, upperVal, includeLower, includeUpper);
    }

    public static FieldCacheRangeFilter<Long> newLongRange(String field, FieldCache.LongParser parser, Long lowerVal, Long upperVal, boolean includeLower, boolean includeUpper) {
        return new FieldCacheRangeFilter<Long>(field, (FieldCache.Parser)parser, lowerVal, upperVal, includeLower, includeUpper){

            @Override
            public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
                long inclusiveUpperPoint;
                long inclusiveLowerPoint;
                long i;
                if (this.lowerVal != null) {
                    i = (Long)this.lowerVal;
                    if (!this.includeLower && i == Long.MAX_VALUE) {
                        return null;
                    }
                    inclusiveLowerPoint = this.includeLower ? i : i + 1L;
                } else {
                    inclusiveLowerPoint = Long.MIN_VALUE;
                }
                if (this.upperVal != null) {
                    i = (Long)this.upperVal;
                    if (!this.includeUpper && i == Long.MIN_VALUE) {
                        return null;
                    }
                    inclusiveUpperPoint = this.includeUpper ? i : i - 1L;
                } else {
                    inclusiveUpperPoint = Long.MAX_VALUE;
                }
                if (inclusiveLowerPoint > inclusiveUpperPoint) {
                    return null;
                }
                final FieldCache.Longs values = FieldCache.DEFAULT.getLongs(context.reader(), this.field, (FieldCache.LongParser)this.parser, false);
                return new FieldCacheDocIdSet(context.reader().maxDoc(), acceptDocs){

                    @Override
                    protected boolean matchDoc(int doc) {
                        long value = values.get(doc);
                        return value >= inclusiveLowerPoint && value <= inclusiveUpperPoint;
                    }
                };
            }
        };
    }

    public static FieldCacheRangeFilter<Float> newFloatRange(String field, Float lowerVal, Float upperVal, boolean includeLower, boolean includeUpper) {
        return FieldCacheRangeFilter.newFloatRange(field, null, lowerVal, upperVal, includeLower, includeUpper);
    }

    public static FieldCacheRangeFilter<Float> newFloatRange(String field, FieldCache.FloatParser parser, Float lowerVal, Float upperVal, boolean includeLower, boolean includeUpper) {
        return new FieldCacheRangeFilter<Float>(field, (FieldCache.Parser)parser, lowerVal, upperVal, includeLower, includeUpper){

            @Override
            public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
                float inclusiveUpperPoint;
                float inclusiveLowerPoint;
                int i;
                float f;
                if (this.lowerVal != null) {
                    f = ((Float)this.lowerVal).floatValue();
                    if (!this.includeUpper && f > 0.0f && Float.isInfinite(f)) {
                        return null;
                    }
                    i = NumericUtils.floatToSortableInt(f);
                    inclusiveLowerPoint = NumericUtils.sortableIntToFloat(this.includeLower ? i : i + 1);
                } else {
                    inclusiveLowerPoint = Float.NEGATIVE_INFINITY;
                }
                if (this.upperVal != null) {
                    f = ((Float)this.upperVal).floatValue();
                    if (!this.includeUpper && f < 0.0f && Float.isInfinite(f)) {
                        return null;
                    }
                    i = NumericUtils.floatToSortableInt(f);
                    inclusiveUpperPoint = NumericUtils.sortableIntToFloat(this.includeUpper ? i : i - 1);
                } else {
                    inclusiveUpperPoint = Float.POSITIVE_INFINITY;
                }
                if (inclusiveLowerPoint > inclusiveUpperPoint) {
                    return null;
                }
                final FieldCache.Floats values = FieldCache.DEFAULT.getFloats(context.reader(), this.field, (FieldCache.FloatParser)this.parser, false);
                return new FieldCacheDocIdSet(context.reader().maxDoc(), acceptDocs){

                    @Override
                    protected boolean matchDoc(int doc) {
                        float value = values.get(doc);
                        return value >= inclusiveLowerPoint && value <= inclusiveUpperPoint;
                    }
                };
            }
        };
    }

    public static FieldCacheRangeFilter<Double> newDoubleRange(String field, Double lowerVal, Double upperVal, boolean includeLower, boolean includeUpper) {
        return FieldCacheRangeFilter.newDoubleRange(field, null, lowerVal, upperVal, includeLower, includeUpper);
    }

    public static FieldCacheRangeFilter<Double> newDoubleRange(String field, FieldCache.DoubleParser parser, Double lowerVal, Double upperVal, boolean includeLower, boolean includeUpper) {
        return new FieldCacheRangeFilter<Double>(field, (FieldCache.Parser)parser, lowerVal, upperVal, includeLower, includeUpper){

            @Override
            public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
                double inclusiveUpperPoint;
                double inclusiveLowerPoint;
                long i;
                double f;
                if (this.lowerVal != null) {
                    f = (Double)this.lowerVal;
                    if (!this.includeUpper && f > 0.0 && Double.isInfinite(f)) {
                        return null;
                    }
                    i = NumericUtils.doubleToSortableLong(f);
                    inclusiveLowerPoint = NumericUtils.sortableLongToDouble(this.includeLower ? i : i + 1L);
                } else {
                    inclusiveLowerPoint = Double.NEGATIVE_INFINITY;
                }
                if (this.upperVal != null) {
                    f = (Double)this.upperVal;
                    if (!this.includeUpper && f < 0.0 && Double.isInfinite(f)) {
                        return null;
                    }
                    i = NumericUtils.doubleToSortableLong(f);
                    inclusiveUpperPoint = NumericUtils.sortableLongToDouble(this.includeUpper ? i : i - 1L);
                } else {
                    inclusiveUpperPoint = Double.POSITIVE_INFINITY;
                }
                if (inclusiveLowerPoint > inclusiveUpperPoint) {
                    return null;
                }
                final FieldCache.Doubles values = FieldCache.DEFAULT.getDoubles(context.reader(), this.field, (FieldCache.DoubleParser)this.parser, false);
                return new FieldCacheDocIdSet(context.reader().maxDoc(), acceptDocs){

                    @Override
                    protected boolean matchDoc(int doc) {
                        double value = values.get(doc);
                        return value >= inclusiveLowerPoint && value <= inclusiveUpperPoint;
                    }
                };
            }
        };
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(this.field).append(":");
        return sb.append(this.includeLower ? (char)'[' : '{').append(this.lowerVal == null ? "*" : this.lowerVal.toString()).append(" TO ").append(this.upperVal == null ? "*" : this.upperVal.toString()).append(this.includeUpper ? (char)']' : '}').toString();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FieldCacheRangeFilter)) {
            return false;
        }
        FieldCacheRangeFilter other = (FieldCacheRangeFilter)o;
        if (!this.field.equals(other.field) || this.includeLower != other.includeLower || this.includeUpper != other.includeUpper) {
            return false;
        }
        if (this.lowerVal != null ? !this.lowerVal.equals(other.lowerVal) : other.lowerVal != null) {
            return false;
        }
        if (this.upperVal != null ? !this.upperVal.equals(other.upperVal) : other.upperVal != null) {
            return false;
        }
        return !(this.parser != null ? !this.parser.equals(other.parser) : other.parser != null);
    }

    public final int hashCode() {
        int h = this.field.hashCode();
        h ^= this.lowerVal != null ? this.lowerVal.hashCode() : 550356204;
        h = h << 1 | h >>> 31;
        h ^= this.upperVal != null ? this.upperVal.hashCode() : -1674416163;
        h ^= this.parser != null ? this.parser.hashCode() : -1572457324;
        return h ^= (this.includeLower ? 1549299360 : -365038026) ^ (this.includeUpper ? 1721088258 : 1948649653);
    }

    public String getField() {
        return this.field;
    }

    public boolean includesLower() {
        return this.includeLower;
    }

    public boolean includesUpper() {
        return this.includeUpper;
    }

    public T getLowerVal() {
        return this.lowerVal;
    }

    public T getUpperVal() {
        return this.upperVal;
    }

    public FieldCache.Parser getParser() {
        return this.parser;
    }
}

