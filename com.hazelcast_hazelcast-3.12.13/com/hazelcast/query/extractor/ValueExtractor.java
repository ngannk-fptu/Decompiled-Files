/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.extractor;

import com.hazelcast.query.extractor.ValueCollector;

public abstract class ValueExtractor<T, A> {
    public abstract void extract(T var1, A var2, ValueCollector var3);
}

