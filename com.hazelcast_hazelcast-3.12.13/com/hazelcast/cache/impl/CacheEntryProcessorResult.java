/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.processor.EntryProcessorException
 *  javax.cache.processor.EntryProcessorResult
 */
package com.hazelcast.cache.impl;

import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;

public class CacheEntryProcessorResult<T>
implements EntryProcessorResult<T> {
    private T result;
    private Throwable exception;

    public CacheEntryProcessorResult(T result) {
        this.result = result;
    }

    public CacheEntryProcessorResult(Throwable exception) {
        this.exception = exception;
    }

    public T get() throws EntryProcessorException {
        if (this.result != null) {
            return this.result;
        }
        throw new EntryProcessorException(this.exception);
    }
}

