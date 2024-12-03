/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.processor;

import javax.cache.processor.EntryProcessorException;

public interface EntryProcessorResult<T> {
    public T get() throws EntryProcessorException;
}

