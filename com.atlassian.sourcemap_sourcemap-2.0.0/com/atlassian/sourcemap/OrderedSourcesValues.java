/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sourcemap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderedSourcesValues {
    private final List<String> values = new ArrayList<String>();
    private final Map<String, Integer> indexLookup = new HashMap<String, Integer>();
    private int nextIndex;

    void add(String value) {
        if (value != null && this.values.contains(value)) {
            return;
        }
        this.values.add(value);
        this.indexLookup.put(value, this.nextIndex);
        ++this.nextIndex;
    }

    void replaceAt(int index, String value) {
        this.values.set(index, value);
        this.indexLookup.put(value, index);
    }

    boolean hasValue(String value) {
        return this.indexLookup.containsKey(value);
    }

    String getValueAtIndex(int index) {
        return this.values.get(index);
    }

    Integer getIndex(String value) {
        return this.indexLookup.get(value);
    }

    List<String> getValues() {
        return Collections.unmodifiableList(this.values);
    }
}

