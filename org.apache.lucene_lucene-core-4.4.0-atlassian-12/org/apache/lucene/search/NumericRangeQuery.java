/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.ToStringUtils;

public final class NumericRangeQuery<T extends Number>
extends MultiTermQuery {
    final int precisionStep;
    final FieldType.NumericType dataType;
    final T min;
    final T max;
    final boolean minInclusive;
    final boolean maxInclusive;
    static final long LONG_NEGATIVE_INFINITY = NumericUtils.doubleToSortableLong(Double.NEGATIVE_INFINITY);
    static final long LONG_POSITIVE_INFINITY = NumericUtils.doubleToSortableLong(Double.POSITIVE_INFINITY);
    static final int INT_NEGATIVE_INFINITY = NumericUtils.floatToSortableInt(Float.NEGATIVE_INFINITY);
    static final int INT_POSITIVE_INFINITY = NumericUtils.floatToSortableInt(Float.POSITIVE_INFINITY);

    private NumericRangeQuery(String field, int precisionStep, FieldType.NumericType dataType, T min, T max, boolean minInclusive, boolean maxInclusive) {
        super(field);
        if (precisionStep < 1) {
            throw new IllegalArgumentException("precisionStep must be >=1");
        }
        this.precisionStep = precisionStep;
        this.dataType = dataType;
        this.min = min;
        this.max = max;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    public static NumericRangeQuery<Long> newLongRange(String field, int precisionStep, Long min, Long max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeQuery<Long>(field, precisionStep, FieldType.NumericType.LONG, min, max, minInclusive, maxInclusive);
    }

    public static NumericRangeQuery<Long> newLongRange(String field, Long min, Long max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeQuery<Long>(field, 4, FieldType.NumericType.LONG, min, max, minInclusive, maxInclusive);
    }

    public static NumericRangeQuery<Integer> newIntRange(String field, int precisionStep, Integer min, Integer max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeQuery<Integer>(field, precisionStep, FieldType.NumericType.INT, min, max, minInclusive, maxInclusive);
    }

    public static NumericRangeQuery<Integer> newIntRange(String field, Integer min, Integer max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeQuery<Integer>(field, 4, FieldType.NumericType.INT, min, max, minInclusive, maxInclusive);
    }

    public static NumericRangeQuery<Double> newDoubleRange(String field, int precisionStep, Double min, Double max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeQuery<Double>(field, precisionStep, FieldType.NumericType.DOUBLE, min, max, minInclusive, maxInclusive);
    }

    public static NumericRangeQuery<Double> newDoubleRange(String field, Double min, Double max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeQuery<Double>(field, 4, FieldType.NumericType.DOUBLE, min, max, minInclusive, maxInclusive);
    }

    public static NumericRangeQuery<Float> newFloatRange(String field, int precisionStep, Float min, Float max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeQuery<Float>(field, precisionStep, FieldType.NumericType.FLOAT, min, max, minInclusive, maxInclusive);
    }

    public static NumericRangeQuery<Float> newFloatRange(String field, Float min, Float max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeQuery<Float>(field, 4, FieldType.NumericType.FLOAT, min, max, minInclusive, maxInclusive);
    }

    @Override
    protected TermsEnum getTermsEnum(Terms terms, AttributeSource atts) throws IOException {
        if (this.min != null && this.max != null && ((Comparable)this.min).compareTo(this.max) > 0) {
            return TermsEnum.EMPTY;
        }
        return new NumericRangeTermsEnum(terms.iterator(null));
    }

    public boolean includesMin() {
        return this.minInclusive;
    }

    public boolean includesMax() {
        return this.maxInclusive;
    }

    public T getMin() {
        return this.min;
    }

    public T getMax() {
        return this.max;
    }

    public int getPrecisionStep() {
        return this.precisionStep;
    }

    @Override
    public String toString(String field) {
        StringBuilder sb = new StringBuilder();
        if (!this.getField().equals(field)) {
            sb.append(this.getField()).append(':');
        }
        return sb.append(this.minInclusive ? (char)'[' : '{').append(this.min == null ? "*" : this.min.toString()).append(" TO ").append(this.max == null ? "*" : this.max.toString()).append(this.maxInclusive ? (char)']' : '}').append(ToStringUtils.boost(this.getBoost())).toString();
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (o instanceof NumericRangeQuery) {
            NumericRangeQuery q = (NumericRangeQuery)o;
            return (q.min == null ? this.min == null : q.min.equals(this.min)) && (q.max == null ? this.max == null : q.max.equals(this.max)) && this.minInclusive == q.minInclusive && this.maxInclusive == q.maxInclusive && this.precisionStep == q.precisionStep;
        }
        return false;
    }

    @Override
    public final int hashCode() {
        int hash = super.hashCode();
        hash += this.precisionStep ^ 0x64365465;
        if (this.min != null) {
            hash += this.min.hashCode() ^ 0x14FA55FB;
        }
        if (this.max != null) {
            hash += this.max.hashCode() ^ 0x733FA5FE;
        }
        return hash + (Boolean.valueOf(this.minInclusive).hashCode() ^ 0x14FA55FB) + (Boolean.valueOf(this.maxInclusive).hashCode() ^ 0x733FA5FE);
    }

    private final class NumericRangeTermsEnum
    extends FilteredTermsEnum {
        private BytesRef currentLowerBound;
        private BytesRef currentUpperBound;
        private final LinkedList<BytesRef> rangeBounds;
        private final Comparator<BytesRef> termComp;

        NumericRangeTermsEnum(TermsEnum tenum) {
            super(tenum);
            this.rangeBounds = new LinkedList();
            switch (NumericRangeQuery.this.dataType) {
                case LONG: 
                case DOUBLE: {
                    long maxBound;
                    long minBound;
                    if (NumericRangeQuery.this.dataType == FieldType.NumericType.LONG) {
                        minBound = NumericRangeQuery.this.min == null ? Long.MIN_VALUE : ((Number)NumericRangeQuery.this.min).longValue();
                    } else {
                        assert (NumericRangeQuery.this.dataType == FieldType.NumericType.DOUBLE);
                        long l = minBound = NumericRangeQuery.this.min == null ? LONG_NEGATIVE_INFINITY : NumericUtils.doubleToSortableLong(((Number)NumericRangeQuery.this.min).doubleValue());
                    }
                    if (!NumericRangeQuery.this.minInclusive && NumericRangeQuery.this.min != null) {
                        if (minBound == Long.MAX_VALUE) break;
                        ++minBound;
                    }
                    if (NumericRangeQuery.this.dataType == FieldType.NumericType.LONG) {
                        maxBound = NumericRangeQuery.this.max == null ? Long.MAX_VALUE : ((Number)NumericRangeQuery.this.max).longValue();
                    } else {
                        assert (NumericRangeQuery.this.dataType == FieldType.NumericType.DOUBLE);
                        long l = maxBound = NumericRangeQuery.this.max == null ? LONG_POSITIVE_INFINITY : NumericUtils.doubleToSortableLong(((Number)NumericRangeQuery.this.max).doubleValue());
                    }
                    if (!NumericRangeQuery.this.maxInclusive && NumericRangeQuery.this.max != null) {
                        if (maxBound == Long.MIN_VALUE) break;
                        --maxBound;
                    }
                    NumericUtils.splitLongRange(new NumericUtils.LongRangeBuilder(){

                        @Override
                        public final void addRange(BytesRef minPrefixCoded, BytesRef maxPrefixCoded) {
                            NumericRangeTermsEnum.this.rangeBounds.add(minPrefixCoded);
                            NumericRangeTermsEnum.this.rangeBounds.add(maxPrefixCoded);
                        }
                    }, NumericRangeQuery.this.precisionStep, minBound, maxBound);
                    break;
                }
                case INT: 
                case FLOAT: {
                    int maxBound;
                    int minBound;
                    if (NumericRangeQuery.this.dataType == FieldType.NumericType.INT) {
                        minBound = NumericRangeQuery.this.min == null ? Integer.MIN_VALUE : ((Number)NumericRangeQuery.this.min).intValue();
                    } else {
                        assert (NumericRangeQuery.this.dataType == FieldType.NumericType.FLOAT);
                        int n = minBound = NumericRangeQuery.this.min == null ? INT_NEGATIVE_INFINITY : NumericUtils.floatToSortableInt(((Number)NumericRangeQuery.this.min).floatValue());
                    }
                    if (!NumericRangeQuery.this.minInclusive && NumericRangeQuery.this.min != null) {
                        if (minBound == Integer.MAX_VALUE) break;
                        ++minBound;
                    }
                    if (NumericRangeQuery.this.dataType == FieldType.NumericType.INT) {
                        maxBound = NumericRangeQuery.this.max == null ? Integer.MAX_VALUE : ((Number)NumericRangeQuery.this.max).intValue();
                    } else {
                        assert (NumericRangeQuery.this.dataType == FieldType.NumericType.FLOAT);
                        int n = maxBound = NumericRangeQuery.this.max == null ? INT_POSITIVE_INFINITY : NumericUtils.floatToSortableInt(((Number)NumericRangeQuery.this.max).floatValue());
                    }
                    if (!NumericRangeQuery.this.maxInclusive && NumericRangeQuery.this.max != null) {
                        if (maxBound == Integer.MIN_VALUE) break;
                        --maxBound;
                    }
                    NumericUtils.splitIntRange(new NumericUtils.IntRangeBuilder(){

                        @Override
                        public final void addRange(BytesRef minPrefixCoded, BytesRef maxPrefixCoded) {
                            NumericRangeTermsEnum.this.rangeBounds.add(minPrefixCoded);
                            NumericRangeTermsEnum.this.rangeBounds.add(maxPrefixCoded);
                        }
                    }, NumericRangeQuery.this.precisionStep, minBound, maxBound);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invalid NumericType");
                }
            }
            this.termComp = this.getComparator();
        }

        private void nextRange() {
            assert (this.rangeBounds.size() % 2 == 0);
            this.currentLowerBound = this.rangeBounds.removeFirst();
            assert (this.currentUpperBound == null || this.termComp.compare(this.currentUpperBound, this.currentLowerBound) <= 0) : "The current upper bound must be <= the new lower bound";
            this.currentUpperBound = this.rangeBounds.removeFirst();
        }

        @Override
        protected final BytesRef nextSeekTerm(BytesRef term) {
            while (this.rangeBounds.size() >= 2) {
                this.nextRange();
                if (term != null && this.termComp.compare(term, this.currentUpperBound) > 0) continue;
                return term != null && this.termComp.compare(term, this.currentLowerBound) > 0 ? term : this.currentLowerBound;
            }
            assert (this.rangeBounds.isEmpty());
            this.currentUpperBound = null;
            this.currentLowerBound = null;
            return null;
        }

        @Override
        protected final FilteredTermsEnum.AcceptStatus accept(BytesRef term) {
            while (this.currentUpperBound == null || this.termComp.compare(term, this.currentUpperBound) > 0) {
                if (this.rangeBounds.isEmpty()) {
                    return FilteredTermsEnum.AcceptStatus.END;
                }
                if (this.termComp.compare(term, this.rangeBounds.getFirst()) < 0) {
                    return FilteredTermsEnum.AcceptStatus.NO_AND_SEEK;
                }
                this.nextRange();
            }
            return FilteredTermsEnum.AcceptStatus.YES;
        }
    }
}

