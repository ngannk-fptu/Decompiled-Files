/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.document.NumericField;
import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermEnum;
import com.atlassian.lucene36.search.FilteredTermEnum;
import com.atlassian.lucene36.search.MultiTermQuery;
import com.atlassian.lucene36.util.NumericUtils;
import com.atlassian.lucene36.util.StringHelper;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class NumericRangeQuery<T extends Number>
extends MultiTermQuery {
    String field;
    final int precisionStep;
    final NumericField.DataType dataType;
    final T min;
    final T max;
    final boolean minInclusive;
    final boolean maxInclusive;
    static final long LONG_NEGATIVE_INFINITY = NumericUtils.doubleToSortableLong(Double.NEGATIVE_INFINITY);
    static final long LONG_POSITIVE_INFINITY = NumericUtils.doubleToSortableLong(Double.POSITIVE_INFINITY);
    static final int INT_NEGATIVE_INFINITY = NumericUtils.floatToSortableInt(Float.NEGATIVE_INFINITY);
    static final int INT_POSITIVE_INFINITY = NumericUtils.floatToSortableInt(Float.POSITIVE_INFINITY);

    private NumericRangeQuery(String field, int precisionStep, NumericField.DataType dataType, T min, T max, boolean minInclusive, boolean maxInclusive) {
        if (precisionStep < 1) {
            throw new IllegalArgumentException("precisionStep must be >=1");
        }
        this.field = StringHelper.intern(field);
        this.precisionStep = precisionStep;
        this.dataType = dataType;
        this.min = min;
        this.max = max;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
        switch (dataType) {
            case LONG: 
            case DOUBLE: {
                this.setRewriteMethod(precisionStep > 6 ? CONSTANT_SCORE_FILTER_REWRITE : CONSTANT_SCORE_AUTO_REWRITE_DEFAULT);
                break;
            }
            case INT: 
            case FLOAT: {
                this.setRewriteMethod(precisionStep > 8 ? CONSTANT_SCORE_FILTER_REWRITE : CONSTANT_SCORE_AUTO_REWRITE_DEFAULT);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid numeric DataType");
            }
        }
        if (min != null && min.equals(max)) {
            this.setRewriteMethod(CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE);
        }
    }

    public static NumericRangeQuery<Long> newLongRange(String field, int precisionStep, Long min, Long max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeQuery<Long>(field, precisionStep, NumericField.DataType.LONG, min, max, minInclusive, maxInclusive);
    }

    public static NumericRangeQuery<Long> newLongRange(String field, Long min, Long max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeQuery<Long>(field, 4, NumericField.DataType.LONG, min, max, minInclusive, maxInclusive);
    }

    public static NumericRangeQuery<Integer> newIntRange(String field, int precisionStep, Integer min, Integer max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeQuery<Integer>(field, precisionStep, NumericField.DataType.INT, min, max, minInclusive, maxInclusive);
    }

    public static NumericRangeQuery<Integer> newIntRange(String field, Integer min, Integer max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeQuery<Integer>(field, 4, NumericField.DataType.INT, min, max, minInclusive, maxInclusive);
    }

    public static NumericRangeQuery<Double> newDoubleRange(String field, int precisionStep, Double min, Double max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeQuery<Double>(field, precisionStep, NumericField.DataType.DOUBLE, min, max, minInclusive, maxInclusive);
    }

    public static NumericRangeQuery<Double> newDoubleRange(String field, Double min, Double max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeQuery<Double>(field, 4, NumericField.DataType.DOUBLE, min, max, minInclusive, maxInclusive);
    }

    public static NumericRangeQuery<Float> newFloatRange(String field, int precisionStep, Float min, Float max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeQuery<Float>(field, precisionStep, NumericField.DataType.FLOAT, min, max, minInclusive, maxInclusive);
    }

    public static NumericRangeQuery<Float> newFloatRange(String field, Float min, Float max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeQuery<Float>(field, 4, NumericField.DataType.FLOAT, min, max, minInclusive, maxInclusive);
    }

    @Override
    protected FilteredTermEnum getEnum(IndexReader reader) throws IOException {
        return new NumericRangeTermEnum(reader);
    }

    public String getField() {
        return this.field;
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
        if (!this.field.equals(field)) {
            sb.append(this.field).append(':');
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
            return this.field == q.field && (q.min == null ? this.min == null : q.min.equals(this.min)) && (q.max == null ? this.max == null : q.max.equals(this.max)) && this.minInclusive == q.minInclusive && this.maxInclusive == q.maxInclusive && this.precisionStep == q.precisionStep;
        }
        return false;
    }

    @Override
    public final int hashCode() {
        int hash = super.hashCode();
        hash += this.field.hashCode() ^ 1164311910 + this.precisionStep ^ 0x64365465;
        if (this.min != null) {
            hash += this.min.hashCode() ^ 0x14FA55FB;
        }
        if (this.max != null) {
            hash += this.max.hashCode() ^ 0x733FA5FE;
        }
        return hash + (Boolean.valueOf(this.minInclusive).hashCode() ^ 0x14FA55FB) + (Boolean.valueOf(this.maxInclusive).hashCode() ^ 0x733FA5FE);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.field = StringHelper.intern(this.field);
    }

    private final class NumericRangeTermEnum
    extends FilteredTermEnum {
        private final IndexReader reader;
        private final LinkedList<String> rangeBounds = new LinkedList();
        private final Term termTemplate;
        private String currentUpperBound;

        NumericRangeTermEnum(IndexReader reader) throws IOException {
            this.termTemplate = new Term(NumericRangeQuery.this.field);
            this.currentUpperBound = null;
            this.reader = reader;
            switch (NumericRangeQuery.this.dataType) {
                case LONG: 
                case DOUBLE: {
                    long maxBound;
                    long minBound;
                    if (NumericRangeQuery.this.dataType == NumericField.DataType.LONG) {
                        minBound = NumericRangeQuery.this.min == null ? Long.MIN_VALUE : ((Number)NumericRangeQuery.this.min).longValue();
                    } else {
                        assert (NumericRangeQuery.this.dataType == NumericField.DataType.DOUBLE);
                        long l = minBound = NumericRangeQuery.this.min == null ? LONG_NEGATIVE_INFINITY : NumericUtils.doubleToSortableLong(((Number)NumericRangeQuery.this.min).doubleValue());
                    }
                    if (!NumericRangeQuery.this.minInclusive && NumericRangeQuery.this.min != null) {
                        if (minBound == Long.MAX_VALUE) break;
                        ++minBound;
                    }
                    if (NumericRangeQuery.this.dataType == NumericField.DataType.LONG) {
                        maxBound = NumericRangeQuery.this.max == null ? Long.MAX_VALUE : ((Number)NumericRangeQuery.this.max).longValue();
                    } else {
                        assert (NumericRangeQuery.this.dataType == NumericField.DataType.DOUBLE);
                        long l = maxBound = NumericRangeQuery.this.max == null ? LONG_POSITIVE_INFINITY : NumericUtils.doubleToSortableLong(((Number)NumericRangeQuery.this.max).doubleValue());
                    }
                    if (!NumericRangeQuery.this.maxInclusive && NumericRangeQuery.this.max != null) {
                        if (maxBound == Long.MIN_VALUE) break;
                        --maxBound;
                    }
                    NumericUtils.splitLongRange(new NumericUtils.LongRangeBuilder(){

                        public final void addRange(String minPrefixCoded, String maxPrefixCoded) {
                            NumericRangeTermEnum.this.rangeBounds.add(minPrefixCoded);
                            NumericRangeTermEnum.this.rangeBounds.add(maxPrefixCoded);
                        }
                    }, NumericRangeQuery.this.precisionStep, minBound, maxBound);
                    break;
                }
                case INT: 
                case FLOAT: {
                    int maxBound;
                    int minBound;
                    if (NumericRangeQuery.this.dataType == NumericField.DataType.INT) {
                        minBound = NumericRangeQuery.this.min == null ? Integer.MIN_VALUE : ((Number)NumericRangeQuery.this.min).intValue();
                    } else {
                        assert (NumericRangeQuery.this.dataType == NumericField.DataType.FLOAT);
                        int n = minBound = NumericRangeQuery.this.min == null ? INT_NEGATIVE_INFINITY : NumericUtils.floatToSortableInt(((Number)NumericRangeQuery.this.min).floatValue());
                    }
                    if (!NumericRangeQuery.this.minInclusive && NumericRangeQuery.this.min != null) {
                        if (minBound == Integer.MAX_VALUE) break;
                        ++minBound;
                    }
                    if (NumericRangeQuery.this.dataType == NumericField.DataType.INT) {
                        maxBound = NumericRangeQuery.this.max == null ? Integer.MAX_VALUE : ((Number)NumericRangeQuery.this.max).intValue();
                    } else {
                        assert (NumericRangeQuery.this.dataType == NumericField.DataType.FLOAT);
                        int n = maxBound = NumericRangeQuery.this.max == null ? INT_POSITIVE_INFINITY : NumericUtils.floatToSortableInt(((Number)NumericRangeQuery.this.max).floatValue());
                    }
                    if (!NumericRangeQuery.this.maxInclusive && NumericRangeQuery.this.max != null) {
                        if (maxBound == Integer.MIN_VALUE) break;
                        --maxBound;
                    }
                    NumericUtils.splitIntRange(new NumericUtils.IntRangeBuilder(){

                        public final void addRange(String minPrefixCoded, String maxPrefixCoded) {
                            NumericRangeTermEnum.this.rangeBounds.add(minPrefixCoded);
                            NumericRangeTermEnum.this.rangeBounds.add(maxPrefixCoded);
                        }
                    }, NumericRangeQuery.this.precisionStep, minBound, maxBound);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invalid numeric DataType");
                }
            }
            this.next();
        }

        public float difference() {
            return 1.0f;
        }

        protected boolean endEnum() {
            throw new UnsupportedOperationException("not implemented");
        }

        protected void setEnum(TermEnum tenum) {
            throw new UnsupportedOperationException("not implemented");
        }

        protected boolean termCompare(Term term) {
            return term.field() == NumericRangeQuery.this.field && term.text().compareTo(this.currentUpperBound) <= 0;
        }

        public boolean next() throws IOException {
            if (this.currentTerm != null) {
                assert (this.actualEnum != null);
                if (this.actualEnum.next()) {
                    this.currentTerm = this.actualEnum.term();
                    if (this.termCompare(this.currentTerm)) {
                        return true;
                    }
                }
            }
            this.currentTerm = null;
            while (this.rangeBounds.size() >= 2) {
                assert (this.rangeBounds.size() % 2 == 0);
                if (this.actualEnum != null) {
                    this.actualEnum.close();
                    this.actualEnum = null;
                }
                String lowerBound = this.rangeBounds.removeFirst();
                this.currentUpperBound = this.rangeBounds.removeFirst();
                this.actualEnum = this.reader.terms(this.termTemplate.createTerm(lowerBound));
                this.currentTerm = this.actualEnum.term();
                if (this.currentTerm != null && this.termCompare(this.currentTerm)) {
                    return true;
                }
                this.currentTerm = null;
            }
            assert (this.rangeBounds.size() == 0 && this.currentTerm == null);
            return false;
        }

        public void close() throws IOException {
            this.rangeBounds.clear();
            this.currentUpperBound = null;
            super.close();
        }
    }
}

