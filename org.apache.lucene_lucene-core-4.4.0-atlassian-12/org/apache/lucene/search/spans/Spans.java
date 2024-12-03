/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.Collection;

public abstract class Spans {
    public abstract boolean next() throws IOException;

    public abstract boolean skipTo(int var1) throws IOException;

    public abstract int doc();

    public abstract int start();

    public abstract int end();

    public abstract Collection<byte[]> getPayload() throws IOException;

    public abstract boolean isPayloadAvailable() throws IOException;

    public abstract long cost();
}

