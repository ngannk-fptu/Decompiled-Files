/*
 * Decompiled with CFR 0.152.
 */
package brave.sampler;

import brave.sampler.Sampler;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public final class CountingSampler
extends Sampler {
    private final AtomicInteger counter = new AtomicInteger();
    private final BitSet sampleDecisions;

    public static Sampler create(float probability) {
        if (probability == 0.0f) {
            return NEVER_SAMPLE;
        }
        if ((double)probability == 1.0) {
            return ALWAYS_SAMPLE;
        }
        if (probability < 0.01f || probability > 1.0f) {
            throw new IllegalArgumentException("probability should be between 0.01 and 1: was " + probability);
        }
        return new CountingSampler(probability);
    }

    CountingSampler(float probability) {
        this(probability, new Random());
    }

    CountingSampler(float probability, Random random) {
        int outOf100 = (int)(probability * 100.0f);
        this.sampleDecisions = CountingSampler.randomBitSet(100, outOf100, random);
    }

    @Override
    public boolean isSampled(long traceIdIgnored) {
        return this.sampleDecisions.get(CountingSampler.mod(this.counter.getAndIncrement(), 100));
    }

    public String toString() {
        return "CountingSampler()";
    }

    static int mod(int dividend, int divisor) {
        int result = dividend % divisor;
        return result >= 0 ? result : divisor + result;
    }

    static BitSet randomBitSet(int size, int cardinality, Random rnd) {
        int i;
        BitSet result = new BitSet(size);
        int[] chosen = new int[cardinality];
        for (i = 0; i < cardinality; ++i) {
            chosen[i] = i;
            result.set(i);
        }
        while (i < size) {
            int j = rnd.nextInt(i + 1);
            if (j < cardinality) {
                result.clear(chosen[j]);
                result.set(i);
                chosen[j] = i;
            }
            ++i;
        }
        return result;
    }
}

