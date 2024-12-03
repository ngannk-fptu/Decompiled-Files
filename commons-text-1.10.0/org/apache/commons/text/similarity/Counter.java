/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.similarity;

import java.util.HashMap;
import java.util.Map;

final class Counter {
    public static Map<CharSequence, Integer> of(CharSequence[] tokens) {
        HashMap<CharSequence, Integer> innerCounter = new HashMap<CharSequence, Integer>();
        for (CharSequence token : tokens) {
            innerCounter.put(token, innerCounter.containsKey(token) ? (Integer)innerCounter.get(token) + 1 : 1);
        }
        return innerCounter;
    }

    private Counter() {
    }
}

