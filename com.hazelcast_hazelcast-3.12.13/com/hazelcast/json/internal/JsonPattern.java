/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.json.internal;

import java.util.ArrayList;
import java.util.List;

public class JsonPattern {
    private final List<Integer> pattern;
    private boolean containsAny;

    public JsonPattern() {
        this(new ArrayList<Integer>());
    }

    public JsonPattern(List<Integer> list) {
        this.pattern = list;
    }

    public JsonPattern(JsonPattern other) {
        this(new ArrayList<Integer>(other.pattern));
    }

    public int get(int index) {
        return this.pattern.get(index);
    }

    public void add(int patternItem) {
        this.pattern.add(patternItem);
    }

    public void addAny() {
        this.containsAny = true;
    }

    public void add(JsonPattern other) {
        this.pattern.addAll(other.pattern);
    }

    public boolean hasAny() {
        return this.containsAny;
    }

    public int depth() {
        return this.pattern.size();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JsonPattern pattern1 = (JsonPattern)o;
        if (this.containsAny != pattern1.containsAny) {
            return false;
        }
        return this.pattern != null ? this.pattern.equals(pattern1.pattern) : pattern1.pattern == null;
    }

    public int hashCode() {
        int result = this.pattern != null ? this.pattern.hashCode() : 0;
        result = 31 * result + (this.containsAny ? 1 : 0);
        return result;
    }

    public String toString() {
        return "JsonPattern{pattern=" + this.pattern + ", containsAny=" + this.containsAny + '}';
    }
}

