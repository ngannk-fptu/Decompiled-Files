/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.DocIdSet;
import com.atlassian.lucene36.search.FieldCache;
import com.atlassian.lucene36.search.FieldCacheDocIdSet;
import com.atlassian.lucene36.search.Filter;
import com.atlassian.lucene36.util.NumericUtils;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
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
    public abstract DocIdSet getDocIdSet(IndexReader var1) throws IOException;

    public static FieldCacheRangeFilter<String> newStringRange(String field, String lowerVal, String upperVal, boolean includeLower, boolean includeUpper) {
        return new FieldCacheRangeFilter<String>(field, null, lowerVal, upperVal, includeLower, includeUpper){

            @Override
            public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
                int inclusiveUpperPoint;
                int inclusiveLowerPoint;
                final FieldCache.StringIndex fcsi = FieldCache.DEFAULT.getStringIndex(reader, this.field);
                int lowerPoint = fcsi.binarySearchLookup((String)this.lowerVal);
                int upperPoint = fcsi.binarySearchLookup((String)this.upperVal);
                if (lowerPoint == 0) {
                    assert (this.lowerVal == null);
                    inclusiveLowerPoint = 1;
                } else {
                    inclusiveLowerPoint = this.includeLower && lowerPoint > 0 ? lowerPoint : (lowerPoint > 0 ? lowerPoint + 1 : Math.max(1, -lowerPoint - 1));
                }
                if (upperPoint == 0) {
                    assert (this.upperVal == null);
                    inclusiveUpperPoint = Integer.MAX_VALUE;
                } else {
                    inclusiveUpperPoint = this.includeUpper && upperPoint > 0 ? upperPoint : (upperPoint > 0 ? upperPoint - 1 : -upperPoint - 2);
                }
                if (inclusiveUpperPoint <= 0 || inclusiveLowerPoint > inclusiveUpperPoint) {
                    return DocIdSet.EMPTY_DOCIDSET;
                }
                assert (inclusiveLowerPoint > 0 && inclusiveUpperPoint > 0);
                return new FieldCacheDocIdSet(reader){

                    protected final boolean matchDoc(int doc) {
                        return fcsi.order[doc] >= inclusiveLowerPoint && fcsi.order[doc] <= inclusiveUpperPoint;
                    }
                };
            }
        };
    }

    public static FieldCacheRangeFilter<Byte> newByteRange(String field, Byte lowerVal, Byte upperVal, boolean includeLower, boolean includeUpper) {
        return FieldCacheRangeFilter.newByteRange(field, null, lowerVal, upperVal, includeLower, includeUpper);
    }

    public static FieldCacheRangeFilter<Byte> newByteRange(String field, FieldCache.ByteParser parser, Byte lowerVal, Byte upperVal, boolean includeLower, boolean includeUpper) {
        return new FieldCacheRangeFilter<Byte>(field, (FieldCache.Parser)parser, lowerVal, upperVal, includeLower, includeUpper){

            @Override
            public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
                byte inclusiveUpperPoint;
                byte inclusiveLowerPoint;
                int i;
                if (this.lowerVal != null) {
                    i = ((Byte)this.lowerVal).byteValue();
                    if (!this.includeLower && i == 127) {
                        return DocIdSet.EMPTY_DOCIDSET;
                    }
                    inclusiveLowerPoint = (byte)(this.includeLower ? i : i + 1);
                } else {
                    inclusiveLowerPoint = -128;
                }
                if (this.upperVal != null) {
                    i = ((Byte)this.upperVal).byteValue();
                    if (!this.includeUpper && i == -128) {
                        return DocIdSet.EMPTY_DOCIDSET;
                    }
                    inclusiveUpperPoint = (byte)(this.includeUpper ? i : i - 1);
                } else {
                    inclusiveUpperPoint = 127;
                }
                if (inclusiveLowerPoint > inclusiveUpperPoint) {
                    return DocIdSet.EMPTY_DOCIDSET;
                }
                final byte[] values = FieldCache.DEFAULT.getBytes(reader, this.field, (FieldCache.ByteParser)this.parser);
                return new FieldCacheDocIdSet(reader){

                    protected boolean matchDoc(int doc) {
                        return values[doc] >= inclusiveLowerPoint && values[doc] <= inclusiveUpperPoint;
                    }
                };
            }
        };
    }

    public static FieldCacheRangeFilter<Short> newShortRange(String field, Short lowerVal, Short upperVal, boolean includeLower, boolean includeUpper) {
        return FieldCacheRangeFilter.newShortRange(field, null, lowerVal, upperVal, includeLower, includeUpper);
    }

    public static FieldCacheRangeFilter<Short> newShortRange(String field, FieldCache.ShortParser parser, Short lowerVal, Short upperVal, boolean includeLower, boolean includeUpper) {
        return new FieldCacheRangeFilter<Short>(field, (FieldCache.Parser)parser, lowerVal, upperVal, includeLower, includeUpper){

            @Override
            public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
                short inclusiveUpperPoint;
                short inclusiveLowerPoint;
                int i;
                if (this.lowerVal != null) {
                    i = ((Short)this.lowerVal).shortValue();
                    if (!this.includeLower && i == Short.MAX_VALUE) {
                        return DocIdSet.EMPTY_DOCIDSET;
                    }
                    inclusiveLowerPoint = (short)(this.includeLower ? i : i + 1);
                } else {
                    inclusiveLowerPoint = Short.MIN_VALUE;
                }
                if (this.upperVal != null) {
                    i = ((Short)this.upperVal).shortValue();
                    if (!this.includeUpper && i == Short.MIN_VALUE) {
                        return DocIdSet.EMPTY_DOCIDSET;
                    }
                    inclusiveUpperPoint = (short)(this.includeUpper ? i : i - 1);
                } else {
                    inclusiveUpperPoint = Short.MAX_VALUE;
                }
                if (inclusiveLowerPoint > inclusiveUpperPoint) {
                    return DocIdSet.EMPTY_DOCIDSET;
                }
                final short[] values = FieldCache.DEFAULT.getShorts(reader, this.field, (FieldCache.ShortParser)this.parser);
                return new FieldCacheDocIdSet(reader){

                    protected boolean matchDoc(int doc) {
                        return values[doc] >= inclusiveLowerPoint && values[doc] <= inclusiveUpperPoint;
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
            public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
                int inclusiveUpperPoint;
                int inclusiveLowerPoint;
                int i;
                if (this.lowerVal != null) {
                    i = (Integer)this.lowerVal;
                    if (!this.includeLower && i == Integer.MAX_VALUE) {
                        return DocIdSet.EMPTY_DOCIDSET;
                    }
                    inclusiveLowerPoint = this.includeLower ? i : i + 1;
                } else {
                    inclusiveLowerPoint = Integer.MIN_VALUE;
                }
                if (this.upperVal != null) {
                    i = (Integer)this.upperVal;
                    if (!this.includeUpper && i == Integer.MIN_VALUE) {
                        return DocIdSet.EMPTY_DOCIDSET;
                    }
                    inclusiveUpperPoint = this.includeUpper ? i : i - 1;
                } else {
                    inclusiveUpperPoint = Integer.MAX_VALUE;
                }
                if (inclusiveLowerPoint > inclusiveUpperPoint) {
                    return DocIdSet.EMPTY_DOCIDSET;
                }
                final int[] values = FieldCache.DEFAULT.getInts(reader, this.field, (FieldCache.IntParser)this.parser);
                return new FieldCacheDocIdSet(reader){

                    protected boolean matchDoc(int doc) {
                        return values[doc] >= inclusiveLowerPoint && values[doc] <= inclusiveUpperPoint;
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
            public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
                long inclusiveUpperPoint;
                long inclusiveLowerPoint;
                long i;
                if (this.lowerVal != null) {
                    i = (Long)this.lowerVal;
                    if (!this.includeLower && i == Long.MAX_VALUE) {
                        return DocIdSet.EMPTY_DOCIDSET;
                    }
                    inclusiveLowerPoint = this.includeLower ? i : i + 1L;
                } else {
                    inclusiveLowerPoint = Long.MIN_VALUE;
                }
                if (this.upperVal != null) {
                    i = (Long)this.upperVal;
                    if (!this.includeUpper && i == Long.MIN_VALUE) {
                        return DocIdSet.EMPTY_DOCIDSET;
                    }
                    inclusiveUpperPoint = this.includeUpper ? i : i - 1L;
                } else {
                    inclusiveUpperPoint = Long.MAX_VALUE;
                }
                if (inclusiveLowerPoint > inclusiveUpperPoint) {
                    return DocIdSet.EMPTY_DOCIDSET;
                }
                final long[] values = FieldCache.DEFAULT.getLongs(reader, this.field, (FieldCache.LongParser)this.parser);
                return new FieldCacheDocIdSet(reader){

                    protected boolean matchDoc(int doc) {
                        return values[doc] >= inclusiveLowerPoint && values[doc] <= inclusiveUpperPoint;
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
            public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
                float inclusiveUpperPoint;
                float inclusiveLowerPoint;
                int i;
                float f;
                if (this.lowerVal != null) {
                    f = ((Float)this.lowerVal).floatValue();
                    if (!this.includeUpper && f > 0.0f && Float.isInfinite(f)) {
                        return DocIdSet.EMPTY_DOCIDSET;
                    }
                    i = NumericUtils.floatToSortableInt(f);
                    inclusiveLowerPoint = NumericUtils.sortableIntToFloat(this.includeLower ? i : i + 1);
                } else {
                    inclusiveLowerPoint = Float.NEGATIVE_INFINITY;
                }
                if (this.upperVal != null) {
                    f = ((Float)this.upperVal).floatValue();
                    if (!this.includeUpper && f < 0.0f && Float.isInfinite(f)) {
                        return DocIdSet.EMPTY_DOCIDSET;
                    }
                    i = NumericUtils.floatToSortableInt(f);
                    inclusiveUpperPoint = NumericUtils.sortableIntToFloat(this.includeUpper ? i : i - 1);
                } else {
                    inclusiveUpperPoint = Float.POSITIVE_INFINITY;
                }
                if (inclusiveLowerPoint > inclusiveUpperPoint) {
                    return DocIdSet.EMPTY_DOCIDSET;
                }
                final float[] values = FieldCache.DEFAULT.getFloats(reader, this.field, (FieldCache.FloatParser)this.parser);
                return new FieldCacheDocIdSet(reader){

                    protected boolean matchDoc(int doc) {
                        return values[doc] >= inclusiveLowerPoint && values[doc] <= inclusiveUpperPoint;
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
            public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
                double inclusiveUpperPoint;
                double inclusiveLowerPoint;
                long i;
                double f;
                if (this.lowerVal != null) {
                    f = (Double)this.lowerVal;
                    if (!this.includeUpper && f > 0.0 && Double.isInfinite(f)) {
                        return DocIdSet.EMPTY_DOCIDSET;
                    }
                    i = NumericUtils.doubleToSortableLong(f);
                    inclusiveLowerPoint = NumericUtils.sortableLongToDouble(this.includeLower ? i : i + 1L);
                } else {
                    inclusiveLowerPoint = Double.NEGATIVE_INFINITY;
                }
                if (this.upperVal != null) {
                    f = (Double)this.upperVal;
                    if (!this.includeUpper && f < 0.0 && Double.isInfinite(f)) {
                        return DocIdSet.EMPTY_DOCIDSET;
                    }
                    i = NumericUtils.doubleToSortableLong(f);
                    inclusiveUpperPoint = NumericUtils.sortableLongToDouble(this.includeUpper ? i : i - 1L);
                } else {
                    inclusiveUpperPoint = Double.POSITIVE_INFINITY;
                }
                if (inclusiveLowerPoint > inclusiveUpperPoint) {
                    return DocIdSet.EMPTY_DOCIDSET;
                }
                final double[] values = FieldCache.DEFAULT.getDoubles(reader, this.field, (FieldCache.DoubleParser)this.parser);
                return new FieldCacheDocIdSet(reader){

                    protected boolean matchDoc(int doc) {
                        return values[doc] >= inclusiveLowerPoint && values[doc] <= inclusiveUpperPoint;
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

