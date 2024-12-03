/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.similarity;

import java.lang.invoke.LambdaMetafactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.text.similarity.IntersectionResult;
import org.apache.commons.text.similarity.SimilarityScore;

public class IntersectionSimilarity<T>
implements SimilarityScore<IntersectionResult> {
    private final Function<CharSequence, Collection<T>> converter;

    private static <T> int getIntersection(Set<T> setA, Set<T> setB) {
        int intersection = 0;
        for (T element : setA) {
            if (!setB.contains(element)) continue;
            ++intersection;
        }
        return intersection;
    }

    public IntersectionSimilarity(Function<CharSequence, Collection<T>> converter) {
        if (converter == null) {
            throw new IllegalArgumentException("Converter must not be null");
        }
        this.converter = converter;
    }

    @Override
    public IntersectionResult apply(CharSequence left, CharSequence right) {
        int intersection;
        int sizeB;
        if (left == null || right == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        Collection<T> objectsA = this.converter.apply(left);
        Collection<T> objectsB = this.converter.apply(right);
        int sizeA = objectsA.size();
        if (Math.min(sizeA, sizeB = objectsB.size()) == 0) {
            return new IntersectionResult(sizeA, sizeB, 0);
        }
        if (objectsA instanceof Set && objectsB instanceof Set) {
            intersection = sizeA < sizeB ? IntersectionSimilarity.getIntersection((Set)objectsA, (Set)objectsB) : IntersectionSimilarity.getIntersection((Set)objectsB, (Set)objectsA);
        } else {
            TinyBag bagA = this.toBag(objectsA);
            TinyBag bagB = this.toBag(objectsB);
            intersection = bagA.uniqueElementSize() < bagB.uniqueElementSize() ? this.getIntersection(bagA, bagB) : this.getIntersection(bagB, bagA);
        }
        return new IntersectionResult(sizeA, sizeB, intersection);
    }

    private int getIntersection(TinyBag bagA, TinyBag bagB) {
        int intersection = 0;
        for (Map.Entry entry : bagA.entrySet()) {
            Object element = entry.getKey();
            int count = entry.getValue().count;
            intersection += Math.min(count, bagB.getCount(element));
        }
        return intersection;
    }

    private TinyBag toBag(Collection<T> objects) {
        TinyBag bag = new TinyBag(objects.size());
        objects.forEach(bag::add);
        return bag;
    }

    private class TinyBag {
        private final Map<T, BagCount> map;

        TinyBag(int initialCapacity) {
            this.map = new HashMap(initialCapacity);
        }

        void add(T object) {
            ++this.map.computeIfAbsent(object, (Function<Object, BagCount>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$add$0(java.lang.Object ), (Ljava/lang/Object;)Lorg/apache/commons/text/similarity/IntersectionSimilarity$BagCount;)()).count;
        }

        Set<Map.Entry<T, BagCount>> entrySet() {
            return this.map.entrySet();
        }

        int getCount(Object object) {
            return this.map.getOrDefault((Object)object, (BagCount)BagCount.ZERO).count;
        }

        int uniqueElementSize() {
            return this.map.size();
        }

        private static /* synthetic */ BagCount lambda$add$0(Object k) {
            return new BagCount();
        }
    }

    private static final class BagCount {
        private static final BagCount ZERO = new BagCount();
        int count = 0;

        private BagCount() {
        }
    }
}

