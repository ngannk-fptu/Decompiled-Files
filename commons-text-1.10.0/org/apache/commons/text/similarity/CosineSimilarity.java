/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.similarity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CosineSimilarity {
    public Double cosineSimilarity(Map<CharSequence, Integer> leftVector, Map<CharSequence, Integer> rightVector) {
        if (leftVector == null || rightVector == null) {
            throw new IllegalArgumentException("Vectors must not be null");
        }
        Set<CharSequence> intersection = this.getIntersection(leftVector, rightVector);
        double dotProduct = this.dot(leftVector, rightVector, intersection);
        double d1 = 0.0;
        for (Integer value : leftVector.values()) {
            d1 += Math.pow(value.intValue(), 2.0);
        }
        double d2 = 0.0;
        for (Integer value : rightVector.values()) {
            d2 += Math.pow(value.intValue(), 2.0);
        }
        double cosineSimilarity = d1 <= 0.0 || d2 <= 0.0 ? 0.0 : dotProduct / (Math.sqrt(d1) * Math.sqrt(d2));
        return cosineSimilarity;
    }

    private double dot(Map<CharSequence, Integer> leftVector, Map<CharSequence, Integer> rightVector, Set<CharSequence> intersection) {
        long dotProduct = 0L;
        for (CharSequence key : intersection) {
            dotProduct += (long)leftVector.get(key).intValue() * (long)rightVector.get(key).intValue();
        }
        return dotProduct;
    }

    private Set<CharSequence> getIntersection(Map<CharSequence, Integer> leftVector, Map<CharSequence, Integer> rightVector) {
        HashSet<CharSequence> intersection = new HashSet<CharSequence>(leftVector.keySet());
        intersection.retainAll(rightVector.keySet());
        return intersection;
    }
}

