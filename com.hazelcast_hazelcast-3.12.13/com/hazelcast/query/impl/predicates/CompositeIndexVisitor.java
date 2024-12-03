/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.AbstractIndex;
import com.hazelcast.query.impl.CompositeValue;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.query.impl.predicates.AbstractVisitor;
import com.hazelcast.query.impl.predicates.AndPredicate;
import com.hazelcast.query.impl.predicates.CompositeEqualPredicate;
import com.hazelcast.query.impl.predicates.CompositeRangePredicate;
import com.hazelcast.query.impl.predicates.EqualPredicate;
import com.hazelcast.query.impl.predicates.PredicateUtils;
import com.hazelcast.query.impl.predicates.RangePredicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CompositeIndexVisitor
extends AbstractVisitor {
    @Override
    public Predicate visit(AndPredicate andPredicate, Indexes indexes) {
        int originalSize = andPredicate.predicates.length;
        if (originalSize < 2) {
            return andPredicate;
        }
        InternalIndex[] compositeIndexes = indexes.getCompositeIndexes();
        if (compositeIndexes.length == 0) {
            return andPredicate;
        }
        Map<String, EqualPredicate> prefixes = null;
        Map<String, RangePredicate> comparisons = null;
        Output output = null;
        for (Predicate predicate : andPredicate.predicates) {
            RangePredicate replaced;
            if (PredicateUtils.isEqualPredicate(predicate)) {
                EqualPredicate equalPredicate = (EqualPredicate)predicate;
                replaced = (prefixes = CompositeIndexVisitor.obtainHashMap(prefixes, originalSize)).put(equalPredicate.attributeName, equalPredicate);
                if (replaced == null) continue;
                output = CompositeIndexVisitor.obtainOutput(output, originalSize);
                output.add(replaced);
                continue;
            }
            if (PredicateUtils.isRangePredicate(predicate)) {
                RangePredicate rangePredicate = (RangePredicate)predicate;
                replaced = (comparisons = CompositeIndexVisitor.obtainHashMap(comparisons, originalSize)).put(rangePredicate.getAttribute(), rangePredicate);
                if (replaced == null) continue;
                output = CompositeIndexVisitor.obtainOutput(output, originalSize);
                output.add(replaced);
                continue;
            }
            output = CompositeIndexVisitor.obtainOutput(output, originalSize);
            output.add(predicate);
        }
        if (prefixes == null || comparisons == null && prefixes.size() == 1) {
            return andPredicate;
        }
        assert (!prefixes.isEmpty());
        while (!prefixes.isEmpty()) {
            Predicate generated;
            int equalPrefixLength;
            int bestPrefix = 0;
            InternalIndex bestIndex = null;
            RangePredicate bestComparison = null;
            for (InternalIndex index : compositeIndexes) {
                int prefix;
                String[] components = index.getComponents();
                if (components.length < bestPrefix || !index.isOrdered() && prefixes.size() < components.length) continue;
                for (prefix = 0; prefix < components.length && prefixes.containsKey(components[prefix]); ++prefix) {
                }
                if (prefix == 0) continue;
                if (index.isOrdered()) {
                    RangePredicate comparison;
                    RangePredicate rangePredicate = comparison = prefix < components.length && comparisons != null ? comparisons.get(components[prefix]) : null;
                    if (comparison != null) {
                        ++prefix;
                    }
                    if (prefix > bestPrefix) {
                        bestPrefix = prefix;
                        bestIndex = index;
                        bestComparison = comparison;
                        continue;
                    }
                    if (prefix != bestPrefix) continue;
                    assert (bestIndex != null);
                    if (!bestIndex.isOrdered() || bestIndex.getComponents().length <= components.length) continue;
                    bestIndex = index;
                    bestComparison = comparison;
                    continue;
                }
                if (prefix != components.length || prefix < bestPrefix) continue;
                bestPrefix = prefix;
                bestIndex = index;
                bestComparison = null;
            }
            if (bestIndex == null || bestPrefix == 1) break;
            int n = equalPrefixLength = bestComparison == null ? bestPrefix : bestPrefix - 1;
            if (output == null && (generated = CompositeIndexVisitor.tryGenerateFast(prefixes, comparisons, equalPrefixLength, bestComparison, bestIndex)) != null) {
                return generated;
            }
            output = CompositeIndexVisitor.obtainOutput(output, originalSize);
            CompositeIndexVisitor.addToOutput(prefixes, comparisons, output, equalPrefixLength, bestComparison, bestIndex);
        }
        return output == null ? andPredicate : output.generate(prefixes, comparisons, andPredicate);
    }

    private static Predicate tryGenerateFast(Map<String, EqualPredicate> prefixes, Map<String, RangePredicate> comparisons, int prefixLength, RangePredicate comparison, InternalIndex index) {
        if (index.isOrdered()) {
            assert (prefixLength <= index.getComponents().length);
            return CompositeIndexVisitor.tryGenerateFastOrdered(prefixes, comparisons, prefixLength, comparison, index);
        }
        assert (comparison == null);
        assert (prefixLength == index.getComponents().length);
        return CompositeIndexVisitor.tryGenerateFastUnordered(prefixes, comparisons, prefixLength, index);
    }

    private static Predicate tryGenerateFastOrdered(Map<String, EqualPredicate> prefixes, Map<String, RangePredicate> comparisons, int prefixLength, RangePredicate comparison, InternalIndex index) {
        assert (index.isOrdered());
        String[] components = index.getComponents();
        if (prefixes.size() != prefixLength) {
            return null;
        }
        if (comparison == null) {
            if (comparisons != null) {
                assert (!comparisons.isEmpty());
                return null;
            }
            if (prefixLength == components.length) {
                return CompositeIndexVisitor.generateEqualPredicate(index, prefixes, true);
            }
            return CompositeIndexVisitor.generateRangePredicate(index, prefixes, prefixLength, true);
        }
        if (comparisons.size() != 1) {
            return null;
        }
        return CompositeIndexVisitor.generateRangePredicate(index, prefixes, prefixLength, comparison, true);
    }

    private static Predicate tryGenerateFastUnordered(Map<String, EqualPredicate> prefixes, Map<String, RangePredicate> comparisons, int prefixLength, InternalIndex index) {
        assert (!index.isOrdered());
        if (comparisons != null) {
            assert (!comparisons.isEmpty());
            return null;
        }
        if (prefixLength != prefixes.size()) {
            return null;
        }
        return CompositeIndexVisitor.generateEqualPredicate(index, prefixes, true);
    }

    private static void addToOutput(Map<String, EqualPredicate> prefixes, Map<String, RangePredicate> comparisons, Output output, int prefixLength, RangePredicate comparison, InternalIndex index) {
        if (index.isOrdered()) {
            assert (prefixLength <= index.getComponents().length);
            CompositeIndexVisitor.addToOutputOrdered(prefixes, comparisons, output, prefixLength, comparison, index);
        } else {
            assert (comparison == null);
            assert (prefixLength == index.getComponents().length);
            CompositeIndexVisitor.addToOutputUnordered(prefixes, output, index);
        }
    }

    private static void addToOutputOrdered(Map<String, EqualPredicate> prefixes, Map<String, RangePredicate> comparisons, Output output, int prefixLength, RangePredicate comparison, InternalIndex index) {
        assert (index.isOrdered());
        String[] components = index.getComponents();
        if (prefixLength == components.length) {
            output.addGenerated(CompositeIndexVisitor.generateEqualPredicate(index, prefixes, false));
            return;
        }
        if (comparison == null) {
            output.addGenerated(CompositeIndexVisitor.generateRangePredicate(index, prefixes, prefixLength, false));
        } else {
            comparisons.remove(comparison.getAttribute());
            output.addGenerated(CompositeIndexVisitor.generateRangePredicate(index, prefixes, prefixLength, comparison, false));
        }
    }

    private static void addToOutputUnordered(Map<String, EqualPredicate> prefixes, Output output, InternalIndex index) {
        assert (!index.isOrdered());
        output.addGenerated(CompositeIndexVisitor.generateEqualPredicate(index, prefixes, false));
    }

    private static Predicate generateEqualPredicate(InternalIndex index, Map<String, EqualPredicate> prefixes, boolean fast) {
        String[] components = index.getComponents();
        Comparable[] values = new Comparable[components.length];
        for (int i = 0; i < components.length; ++i) {
            values[i] = fast ? prefixes.get((Object)components[i]).value : prefixes.remove((Object)components[i]).value;
        }
        return new CompositeEqualPredicate(index, new CompositeValue(values));
    }

    private static Predicate generateRangePredicate(InternalIndex index, Map<String, EqualPredicate> prefixes, int prefixLength, boolean fast) {
        int i;
        String[] components = index.getComponents();
        Comparable[] from = new Comparable[components.length];
        Comparable[] to = new Comparable[components.length];
        for (i = 0; i < prefixLength; ++i) {
            Comparable value;
            from[i] = value = fast ? prefixes.get((Object)components[i]).value : prefixes.remove((Object)components[i]).value;
            to[i] = value;
        }
        for (i = prefixLength; i < components.length; ++i) {
            from[i] = CompositeValue.NEGATIVE_INFINITY;
            to[i] = CompositeValue.POSITIVE_INFINITY;
        }
        return new CompositeRangePredicate(index, new CompositeValue(from), false, new CompositeValue(to), false, prefixLength);
    }

    private static Predicate generateRangePredicate(InternalIndex index, Map<String, EqualPredicate> prefixes, int prefixLength, RangePredicate comparison, boolean fast) {
        int i;
        boolean hasTo;
        assert (!(comparison instanceof EqualPredicate));
        assert (comparison.getFrom() != AbstractIndex.NULL && comparison.getTo() != AbstractIndex.NULL);
        String[] components = index.getComponents();
        boolean fullyMatched = components.length == prefixLength + 1;
        boolean hasFrom = comparison.getFrom() != null;
        boolean bl = hasTo = comparison.getTo() != null;
        assert (hasFrom || hasTo);
        assert (hasFrom || !comparison.isFromInclusive());
        assert (hasTo || !comparison.isToInclusive());
        Comparable[] from = new Comparable[components.length];
        Comparable[] to = new Comparable[components.length];
        for (i = 0; i < prefixLength; ++i) {
            Comparable value;
            from[i] = value = fast ? prefixes.get((Object)components[i]).value : prefixes.remove((Object)components[i]).value;
            to[i] = value;
        }
        from[prefixLength] = hasFrom ? comparison.getFrom() : AbstractIndex.NULL;
        to[prefixLength] = hasTo ? comparison.getTo() : CompositeValue.POSITIVE_INFINITY;
        for (i = prefixLength + 1; i < components.length; ++i) {
            from[i] = !hasFrom || comparison.isFromInclusive() ? CompositeValue.NEGATIVE_INFINITY : CompositeValue.POSITIVE_INFINITY;
            to[i] = !hasTo || comparison.isToInclusive() ? CompositeValue.POSITIVE_INFINITY : CompositeValue.NEGATIVE_INFINITY;
        }
        return new CompositeRangePredicate(index, new CompositeValue(from), fullyMatched && comparison.isFromInclusive(), new CompositeValue(to), fullyMatched && comparison.isToInclusive(), prefixLength);
    }

    private static <K, V> Map<K, V> obtainHashMap(Map<K, V> map, int capacity) {
        return map == null ? new HashMap(capacity) : map;
    }

    private static Output obtainOutput(Output output, int capacity) {
        return output == null ? new Output(capacity) : output;
    }

    private static class Output
    extends ArrayList<Predicate> {
        private boolean requiresGeneration;

        public Output(int capacity) {
            super(capacity);
        }

        public void addGenerated(Predicate predicate) {
            this.add(predicate);
            this.requiresGeneration = true;
        }

        public Predicate generate(Map<String, EqualPredicate> prefixes, Map<String, RangePredicate> comparisons, AndPredicate andPredicate) {
            if (!this.requiresGeneration) {
                return andPredicate;
            }
            int newSize = this.size() + prefixes.size() + (comparisons == null ? 0 : comparisons.size());
            assert (newSize > 0);
            Predicate[] predicates = new Predicate[newSize];
            int index = 0;
            for (Predicate predicate : this) {
                predicates[index++] = predicate;
            }
            if (!prefixes.isEmpty()) {
                for (Predicate predicate : prefixes.values()) {
                    predicates[index++] = predicate;
                }
            }
            if (comparisons != null && !comparisons.isEmpty()) {
                for (Predicate predicate : comparisons.values()) {
                    predicates[index++] = predicate;
                }
            }
            assert (index == newSize);
            return new AndPredicate(predicates);
        }
    }
}

