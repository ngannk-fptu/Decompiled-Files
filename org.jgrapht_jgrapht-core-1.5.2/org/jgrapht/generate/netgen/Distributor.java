/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate.netgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import org.jgrapht.alg.util.Pair;

public class Distributor<K> {
    private final Random rng;
    private final List<Function<K, Integer>> lowerBounds;
    private final List<Function<K, Integer>> upperBounds;

    public Distributor() {
        this(System.nanoTime());
    }

    public Distributor(long seed) {
        this(new Random(seed));
    }

    public Distributor(Random rng) {
        this.rng = rng;
        this.lowerBounds = new ArrayList<Function<K, Integer>>();
        this.upperBounds = new ArrayList<Function<K, Integer>>();
    }

    public void addUpperBound(Function<K, Integer> upperBound) {
        this.upperBounds.add(upperBound);
    }

    public void addLowerBound(Function<K, Integer> lowerBound) {
        this.lowerBounds.add(lowerBound);
    }

    private List<Integer> computeLowerBounds(List<K> keys) {
        ArrayList<Integer> keyLowerBounds = new ArrayList<Integer>(keys.size());
        for (K key : keys) {
            int lowerBound = 0;
            for (Function<K, Integer> lowerBoundFunction : this.lowerBounds) {
                lowerBound = Math.max(lowerBound, lowerBoundFunction.apply(key));
            }
            keyLowerBounds.add(lowerBound);
        }
        return keyLowerBounds;
    }

    private List<Integer> computeUpperBounds(List<K> keys) {
        ArrayList<Integer> keyUpperBounds = new ArrayList<Integer>(keys.size());
        for (K key : keys) {
            int upperBound = Integer.MAX_VALUE;
            for (Function<K, Integer> upperBoundFunction : this.upperBounds) {
                upperBound = Math.min(upperBound, upperBoundFunction.apply(key));
            }
            keyUpperBounds.add(upperBound);
        }
        return keyUpperBounds;
    }

    private Pair<List<Integer>, Long> computeSuffixSum(List<Integer> bounds) {
        ArrayList<Integer> suffixSum = new ArrayList<Integer>(Collections.nCopies(bounds.size(), 0));
        long sum = 0L;
        for (int i = bounds.size() - 1; i >= 0; --i) {
            suffixSum.set(i, (int)Math.min(Integer.MAX_VALUE, sum));
            sum += (long)bounds.get(i).intValue();
        }
        return Pair.of(suffixSum, sum);
    }

    public List<Integer> getDistribution(List<K> keys, int valueNum) {
        List<Integer> keyLowerBounds = this.computeLowerBounds(keys);
        List<Integer> keyUpperBounds = this.computeUpperBounds(keys);
        Pair<List<Integer>, Long> lbSufSumP = this.computeSuffixSum(keyLowerBounds);
        Pair<List<Integer>, Long> ubSufSumP = this.computeSuffixSum(keyUpperBounds);
        List<Integer> lbSufSum = lbSufSumP.getFirst();
        List<Integer> ubSufSum = ubSufSumP.getFirst();
        long lbSum = lbSufSumP.getSecond();
        long ubSum = ubSufSumP.getSecond();
        if (lbSum > (long)valueNum) {
            throw new IllegalArgumentException("Can't distribute values among keys: the sum of lower bounds is greater than the number of values");
        }
        if (ubSum < (long)valueNum) {
            throw new IllegalArgumentException("Can't distribute values among keys: the sum of upper bounds is smaller than the number of values");
        }
        int remainingValues = valueNum;
        ArrayList<Integer> resultingDistribution = new ArrayList<Integer>();
        for (int i = 0; i < keyLowerBounds.size(); ++i) {
            int lowerBound = keyLowerBounds.get(i);
            int upperBound = keyUpperBounds.get(i);
            int valueNumUpperBound = remainingValues - lbSufSum.get(i);
            int valueNumLowerBound = remainingValues - ubSufSum.get(i);
            if ((lowerBound = Math.max(lowerBound, valueNumLowerBound)) > (upperBound = Math.min(upperBound, valueNumUpperBound))) {
                throw new IllegalArgumentException("Infeasible bound specified for the key: " + keys.get(i));
            }
            int allocatedValues = this.rng.nextInt(upperBound - lowerBound + 1) + lowerBound;
            resultingDistribution.add(allocatedValues);
            remainingValues -= allocatedValues;
        }
        return resultingDistribution;
    }
}

