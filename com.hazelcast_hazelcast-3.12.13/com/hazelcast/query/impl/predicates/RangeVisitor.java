/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.core.TypeConverter;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.AbstractIndex;
import com.hazelcast.query.impl.Comparables;
import com.hazelcast.query.impl.FalsePredicate;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.TypeConverters;
import com.hazelcast.query.impl.predicates.AbstractVisitor;
import com.hazelcast.query.impl.predicates.AndPredicate;
import com.hazelcast.query.impl.predicates.BetweenPredicate;
import com.hazelcast.query.impl.predicates.BoundedRangePredicate;
import com.hazelcast.query.impl.predicates.EqualPredicate;
import com.hazelcast.query.impl.predicates.GreaterLessPredicate;
import com.hazelcast.query.impl.predicates.PredicateUtils;
import com.hazelcast.query.impl.predicates.RangePredicate;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashMap;

public class RangeVisitor
extends AbstractVisitor {
    @Override
    public Predicate visit(AndPredicate predicate, Indexes indexes) {
        Predicate[] predicates = predicate.predicates;
        Ranges ranges = null;
        for (int i = 0; i < predicates.length; ++i) {
            if ((ranges = RangeVisitor.intersect(predicates, i, ranges, indexes)) != Ranges.UNSATISFIABLE) continue;
            return FalsePredicate.INSTANCE;
        }
        return ranges == null ? predicate : ranges.generate(predicate);
    }

    @Override
    public Predicate visit(BetweenPredicate predicate, Indexes indexes) {
        TypeConverter converter = indexes.getConverter(predicate.attributeName);
        if (converter == null) {
            return predicate;
        }
        Comparable from = converter.convert(predicate.from);
        Comparable to = converter.convert(predicate.to);
        Order order = RangeVisitor.compare(from, to);
        switch (order) {
            case LESS: {
                return predicate;
            }
            case EQUAL: {
                return new EqualPredicate(predicate.attributeName, from);
            }
            case GREATER: {
                return FalsePredicate.INSTANCE;
            }
        }
        throw new IllegalStateException("Unexpected order: " + (Object)((Object)order));
    }

    private static Ranges intersect(Predicate[] predicates, int predicateIndex, Ranges ranges, Indexes indexes) {
        Predicate predicate = predicates[predicateIndex];
        if (predicate instanceof FalsePredicate) {
            return Ranges.UNSATISFIABLE;
        }
        if (!RangeVisitor.isSupportedPredicate(predicate)) {
            return ranges;
        }
        RangePredicate rangePredicate = (RangePredicate)predicate;
        String attribute = rangePredicate.getAttribute();
        Range existingRange = Ranges.getRange(attribute, ranges);
        Range range = RangeVisitor.intersect(rangePredicate, existingRange, indexes);
        if (range == Range.UNKNOWN) {
            return ranges;
        }
        if (range == Range.UNSATISFIABLE) {
            return Ranges.UNSATISFIABLE;
        }
        if (ranges == null) {
            ranges = new Ranges(predicates.length);
        }
        ranges.addRange(attribute, range, existingRange, predicateIndex);
        return ranges;
    }

    private static boolean isSupportedPredicate(Predicate predicate) {
        if (!PredicateUtils.isRangePredicate(predicate)) {
            return false;
        }
        RangePredicate rangePredicate = (RangePredicate)predicate;
        return !rangePredicate.getAttribute().contains("[any]");
    }

    private static Range intersect(RangePredicate predicate, Range range, Indexes indexes) {
        if (range == null) {
            TypeConverter converter = indexes.getConverter(predicate.getAttribute());
            if (converter == null) {
                return Range.UNKNOWN;
            }
            return new Range(predicate, converter);
        }
        return range.intersect(predicate);
    }

    private static Order compare(Comparable lhs, Comparable rhs) {
        int order = Comparables.compare(lhs, rhs);
        if (order < 0) {
            return Order.LESS;
        }
        if (order == 0) {
            return Order.EQUAL;
        }
        return Order.GREATER;
    }

    private static class Range {
        public static final Range UNKNOWN = new Range();
        public static final Range UNSATISFIABLE = new Range();
        private final String attribute;
        private final TypeConverter converter;
        private Comparable from;
        private boolean fromInclusive;
        private Comparable to;
        private boolean toInclusive;
        private boolean intersected;
        private boolean generated;

        public Range(RangePredicate predicate, TypeConverter converter) {
            this.attribute = predicate.getAttribute();
            this.converter = converter;
            this.from = this.convert(predicate.getFrom(), predicate.isFromInclusive());
            this.fromInclusive = predicate.isFromInclusive();
            this.to = this.convert(predicate.getTo(), predicate.isToInclusive());
            this.toInclusive = predicate.isToInclusive();
            assert (this.isNullnessCheck() || this.from != AbstractIndex.NULL && this.to != AbstractIndex.NULL);
        }

        private Range() {
            this.attribute = null;
            this.converter = TypeConverters.IDENTITY_CONVERTER;
        }

        public Range intersect(RangePredicate predicate) {
            this.intersected = true;
            Comparable from = this.convert(predicate.getFrom(), predicate.isFromInclusive());
            boolean fromInclusive = predicate.isFromInclusive();
            Comparable to = this.convert(predicate.getTo(), predicate.isToInclusive());
            boolean toInclusive = predicate.isToInclusive();
            if (PredicateUtils.isNull(from) && PredicateUtils.isNull(to)) {
                assert (fromInclusive && toInclusive);
                return this.isNullnessCheck() ? this : UNSATISFIABLE;
            }
            if (this.isNullnessCheck()) {
                return UNSATISFIABLE;
            }
            assert (from != AbstractIndex.NULL && to != AbstractIndex.NULL);
            if (this.from == null) {
                this.from = from;
                this.fromInclusive = fromInclusive;
            } else if (from != null) {
                switch (RangeVisitor.compare(this.from, from)) {
                    case LESS: {
                        this.from = from;
                        this.fromInclusive = fromInclusive;
                        break;
                    }
                    case EQUAL: {
                        this.fromInclusive &= fromInclusive;
                        break;
                    }
                    case GREATER: {
                        break;
                    }
                    default: {
                        throw new IllegalStateException("unexpected order");
                    }
                }
            }
            if (this.to == null) {
                this.to = to;
                this.toInclusive = toInclusive;
            } else if (to != null) {
                switch (RangeVisitor.compare(this.to, to)) {
                    case LESS: {
                        break;
                    }
                    case EQUAL: {
                        this.toInclusive &= toInclusive;
                        break;
                    }
                    case GREATER: {
                        this.to = to;
                        this.toInclusive = toInclusive;
                        break;
                    }
                    default: {
                        throw new IllegalStateException("unexpected order");
                    }
                }
            }
            if (this.from != null && this.to != null) {
                switch (RangeVisitor.compare(this.from, this.to)) {
                    case LESS: {
                        return this;
                    }
                    case EQUAL: {
                        return this.fromInclusive && this.toInclusive ? this : UNSATISFIABLE;
                    }
                    case GREATER: {
                        return UNSATISFIABLE;
                    }
                }
                throw new IllegalStateException("unexpected order");
            }
            return this;
        }

        public Predicate generate(Predicate originalPredicate) {
            if (this.generated) {
                return null;
            }
            this.generated = true;
            if (!this.intersected) {
                return originalPredicate;
            }
            if (this.isNullnessCheck()) {
                return new EqualPredicate(this.attribute, AbstractIndex.NULL);
            }
            assert (this.from != AbstractIndex.NULL && this.to != AbstractIndex.NULL);
            if (this.from == null) {
                return new GreaterLessPredicate(this.attribute, this.to, this.toInclusive, true);
            }
            if (this.to == null) {
                return new GreaterLessPredicate(this.attribute, this.from, this.fromInclusive, false);
            }
            if (this.from == this.to || Comparables.compare(this.from, this.to) == 0) {
                assert (this.fromInclusive && this.toInclusive);
                return new EqualPredicate(this.attribute, this.from);
            }
            if (this.fromInclusive && this.toInclusive) {
                return new BetweenPredicate(this.attribute, this.from, this.to);
            }
            return new BoundedRangePredicate(this.attribute, this.from, this.fromInclusive, this.to, this.toInclusive);
        }

        private Comparable convert(Comparable value, boolean convertNull) {
            if (value == null) {
                return convertNull ? this.converter.convert(null) : null;
            }
            return this.converter.convert(value);
        }

        private boolean isNullnessCheck() {
            if (PredicateUtils.isNull(this.from) && PredicateUtils.isNull(this.to)) {
                assert (this.fromInclusive && this.toInclusive);
                return true;
            }
            return false;
        }
    }

    @SuppressFBWarnings(value={"SE_BAD_FIELD"})
    private static class Ranges
    extends HashMap<String, Range> {
        public static final Ranges UNSATISFIABLE = new Ranges();
        private final Range[] rangesByPredicateIndex;
        private int reduction;

        public Ranges(int predicateCount) {
            super(predicateCount);
            this.rangesByPredicateIndex = new Range[predicateCount];
        }

        private Ranges() {
            this.rangesByPredicateIndex = null;
        }

        public static Range getRange(String attribute, Ranges ranges) {
            return ranges == null ? null : ranges.getRange(attribute);
        }

        public Range getRange(String attribute) {
            assert (this.rangesByPredicateIndex != null);
            return (Range)this.get(attribute);
        }

        public void addRange(String attribute, Range range, Range existingRange, int predicateIndex) {
            assert (this.rangesByPredicateIndex != null);
            this.put(attribute, range);
            this.rangesByPredicateIndex[predicateIndex] = range;
            if (existingRange != null) {
                ++this.reduction;
            }
        }

        public Predicate generate(AndPredicate originalAndPredicate) {
            assert (this.rangesByPredicateIndex != null);
            if (this.reduction == 0) {
                return originalAndPredicate;
            }
            Predicate[] originalPredicates = originalAndPredicate.predicates;
            int predicateCount = originalPredicates.length - this.reduction;
            assert (predicateCount > 0);
            Predicate[] predicates = new Predicate[predicateCount];
            int generated = 0;
            for (int i = 0; i < originalPredicates.length; ++i) {
                Range range = this.rangesByPredicateIndex[i];
                if (range == null) {
                    predicates[generated++] = originalPredicates[i];
                    continue;
                }
                Predicate predicate = range.generate(originalPredicates[i]);
                if (predicate == null) continue;
                predicates[generated++] = predicate;
            }
            assert (generated == predicateCount);
            return predicateCount == 1 ? predicates[0] : new AndPredicate(predicates);
        }
    }

    private static enum Order {
        LESS,
        EQUAL,
        GREATER;

    }
}

