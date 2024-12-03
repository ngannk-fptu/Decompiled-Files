/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.impl;

import java.util.Collections;
import java.util.List;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.SearchException;

public class ResultsImpl
implements Results {
    private final List<Result> results;
    private final boolean hasKeys;
    private final boolean hasAttributes;
    private final boolean hasAggregators;
    private final boolean hasValues;
    private final boolean empty;
    private final Runnable discardHook;

    public ResultsImpl(List<? extends Result> results, boolean hasKeys, boolean hasValues, boolean hasAttributes, boolean hasAggregators) {
        this(results, hasKeys, hasValues, hasAttributes, hasAggregators, null);
    }

    public ResultsImpl(List<? extends Result> results, boolean hasKeys, boolean hasValues, boolean hasAttributes, boolean hasAggregators, Runnable discardCallback) {
        this.hasKeys = hasKeys;
        this.hasValues = hasValues;
        this.hasAttributes = hasAttributes;
        this.hasAggregators = hasAggregators;
        this.results = Collections.unmodifiableList(results);
        this.empty = results.isEmpty();
        this.discardHook = discardCallback;
    }

    public String toString() {
        return "Results(size=" + this.size() + ", hasKeys=" + this.hasKeys() + ", hasValues=" + this.hasValues() + ", hasAttributes=" + this.hasAttributes() + ", hasAggregators=" + this.hasAggregators() + ")";
    }

    @Override
    public void discard() {
        if (this.discardHook != null) {
            this.discardHook.run();
        }
    }

    @Override
    public List<Result> all() throws SearchException {
        return this.results;
    }

    @Override
    public List<Result> range(int start, int length) throws SearchException {
        if (start < 0) {
            throw new IllegalArgumentException("start: " + start);
        }
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length);
        }
        int size = this.results.size();
        if (start > size - 1 || length == 0) {
            return Collections.emptyList();
        }
        int end = start + length;
        if (end > size) {
            end = size;
        }
        return this.results.subList(start, end);
    }

    @Override
    public int size() {
        return this.results.size();
    }

    @Override
    public boolean hasKeys() {
        if (this.empty) {
            return false;
        }
        return this.hasKeys;
    }

    @Override
    public boolean hasValues() {
        if (this.empty) {
            return false;
        }
        return this.hasValues;
    }

    @Override
    public boolean hasAttributes() {
        if (this.empty) {
            return false;
        }
        return this.hasAttributes;
    }

    @Override
    public boolean hasAggregators() {
        if (this.empty) {
            return false;
        }
        return this.hasAggregators;
    }
}

