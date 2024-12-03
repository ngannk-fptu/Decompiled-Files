/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.extractor;

import com.hazelcast.query.extractor.ValueCallback;
import com.hazelcast.query.extractor.ValueCollector;
import com.hazelcast.query.extractor.ValueReadingException;

public abstract class ValueReader {
    public abstract <T> void read(String var1, ValueCallback<T> var2) throws ValueReadingException;

    public abstract <T> void read(String var1, ValueCollector<T> var2) throws ValueReadingException;
}

