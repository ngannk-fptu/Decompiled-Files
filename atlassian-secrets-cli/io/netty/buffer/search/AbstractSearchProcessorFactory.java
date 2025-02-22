/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer.search;

import io.netty.buffer.search.BitapSearchProcessorFactory;
import io.netty.buffer.search.KmpSearchProcessorFactory;
import io.netty.buffer.search.SearchProcessorFactory;

public abstract class AbstractSearchProcessorFactory
implements SearchProcessorFactory {
    public static KmpSearchProcessorFactory newKmpSearchProcessorFactory(byte[] needle) {
        return new KmpSearchProcessorFactory(needle);
    }

    public static BitapSearchProcessorFactory newBitapSearchProcessorFactory(byte[] needle) {
        return new BitapSearchProcessorFactory(needle);
    }
}

