/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import java.util.ArrayList;
import java.util.List;

public class MultiResult<T> {
    private List<T> results;
    private boolean nullOrEmptyTarget;

    public MultiResult() {
        this.results = new ArrayList<T>();
    }

    public MultiResult(List<T> results) {
        this.results = results;
    }

    public void add(T result) {
        this.results.add(result);
    }

    public void addNullOrEmptyTarget() {
        if (!this.nullOrEmptyTarget) {
            this.nullOrEmptyTarget = true;
            this.results.add(null);
        }
    }

    public List<T> getResults() {
        return this.results;
    }

    public boolean isEmpty() {
        return this.results.isEmpty();
    }

    public boolean isNullEmptyTarget() {
        return this.nullOrEmptyTarget;
    }

    public void setNullOrEmptyTarget(boolean nullOrEmptyTarget) {
        this.nullOrEmptyTarget = nullOrEmptyTarget;
    }
}

