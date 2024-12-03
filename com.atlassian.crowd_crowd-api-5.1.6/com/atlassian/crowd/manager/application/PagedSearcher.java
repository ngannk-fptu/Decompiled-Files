/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.google.common.collect.AbstractIterator
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.annotations.ExperimentalApi;
import com.google.common.collect.AbstractIterator;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@ExperimentalApi
public interface PagedSearcher<T> {
    public List<T> fetchNextBatch(int var1);

    default public Iterable<T> asIterable(final int batchSize) {
        return () -> new AbstractIterator<T>(){
            Iterator current = Collections.emptyIterator();

            protected T computeNext() {
                if (!this.current.hasNext()) {
                    this.current = PagedSearcher.this.fetchNextBatch(batchSize).iterator();
                }
                return this.current.hasNext() ? this.current.next() : this.endOfData();
            }
        };
    }
}

