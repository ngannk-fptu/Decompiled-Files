/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.similarity;

import org.apache.commons.text.similarity.EditDistance;

public class HammingDistance
implements EditDistance<Integer> {
    @Override
    public Integer apply(CharSequence left, CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("CharSequences must not be null");
        }
        if (left.length() != right.length()) {
            throw new IllegalArgumentException("CharSequences must have the same length");
        }
        int distance = 0;
        for (int i = 0; i < left.length(); ++i) {
            if (left.charAt(i) == right.charAt(i)) continue;
            ++distance;
        }
        return distance;
    }
}

