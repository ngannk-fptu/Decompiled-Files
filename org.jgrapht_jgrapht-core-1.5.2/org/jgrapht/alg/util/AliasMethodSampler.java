/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.util;

import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import org.jgrapht.alg.util.ToleranceDoubleComparator;

public class AliasMethodSampler {
    private final Random rng;
    private Comparator<Double> comparator;
    private final double[] prob;
    private final int[] alias;

    public AliasMethodSampler(double[] p) {
        this(p, new Random(), 1.0E-9);
    }

    public AliasMethodSampler(double[] p, long seed) {
        this(p, new Random(seed), 1.0E-9);
    }

    public AliasMethodSampler(double[] p, Random rng) {
        this(p, rng, 1.0E-9);
    }

    public AliasMethodSampler(double[] p, Random rng, double epsilon) {
        int j;
        this.rng = Objects.requireNonNull(rng, "Random number generator cannot be null");
        this.comparator = new ToleranceDoubleComparator(epsilon);
        if (p == null || p.length < 1) {
            throw new IllegalArgumentException("Probabilities cannot be empty");
        }
        double sum = 0.0;
        for (int i = 0; i < p.length; ++i) {
            if (this.comparator.compare(p[i], 0.0) < 0) {
                throw new IllegalArgumentException("Non valid probability distribution");
            }
            sum += p[i];
        }
        if (this.comparator.compare(sum, 1.0) != 0) {
            throw new IllegalArgumentException("Non valid probability distribution");
        }
        int n = p.length;
        int[] large = new int[n];
        int[] small = new int[n];
        double threshold = 1.0 / (double)n;
        int l = 0;
        int s = 0;
        for (j = 0; j < n; ++j) {
            if (this.comparator.compare(p[j], threshold) > 0) {
                large[l++] = j;
                continue;
            }
            small[s++] = j;
        }
        this.prob = new double[n];
        this.alias = new int[n];
        while (s != 0 && l != 0) {
            j = small[--s];
            int k = large[--l];
            this.prob[j] = (double)n * p[j];
            this.alias[j] = k;
            int n2 = k;
            p[n2] = p[n2] + (p[j] - threshold);
            if (this.comparator.compare(p[k], threshold) > 0) {
                large[l++] = k;
                continue;
            }
            small[s++] = k;
        }
        while (s > 0) {
            this.prob[small[--s]] = 1.0;
        }
        while (l > 0) {
            this.prob[large[--l]] = 1.0;
        }
    }

    public int next() {
        int j;
        double u = this.rng.nextDouble() * (double)this.prob.length;
        if (this.comparator.compare(u - (double)(j = (int)Math.floor(u)), this.prob[j]) <= 0) {
            return j;
        }
        return this.alias[j];
    }
}

