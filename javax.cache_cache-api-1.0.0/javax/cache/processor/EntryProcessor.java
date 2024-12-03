/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.processor;

import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;

public interface EntryProcessor<K, V, T> {
    public T process(MutableEntry<K, V> var1, Object ... var2) throws EntryProcessorException;
}

