/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import java.util.Random;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.Policy;

public abstract class AbstractPolicy
implements Policy {
    static final int DEFAULT_SAMPLE_SIZE = 30;
    static final Random RANDOM = new Random();

    public static int calculateSampleSize(int populationSize) {
        if (populationSize < 30) {
            return populationSize;
        }
        return 30;
    }

    @Override
    public Element selectedBasedOnPolicy(Element[] sampledElements, Element justAdded) {
        if (sampledElements.length == 1) {
            return sampledElements[0];
        }
        Element lowestElement = null;
        for (Element element : sampledElements) {
            if (element == null) continue;
            if (lowestElement == null) {
                if (element.equals(justAdded)) continue;
                lowestElement = element;
                continue;
            }
            if (!this.compare(lowestElement, element) || element.equals(justAdded)) continue;
            lowestElement = element;
        }
        return lowestElement;
    }

    public static int[] generateRandomSample(int populationSize) {
        int sampleSize = AbstractPolicy.calculateSampleSize(populationSize);
        int[] offsets = new int[sampleSize];
        if (sampleSize != 0) {
            int maxOffset = 0;
            maxOffset = populationSize / sampleSize;
            for (int i = 0; i < sampleSize; ++i) {
                offsets[i] = RANDOM.nextInt(maxOffset);
            }
        }
        return offsets;
    }
}

