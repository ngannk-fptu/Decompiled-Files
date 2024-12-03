/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.spans;

import java.io.IOException;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Spans {
    public abstract boolean next() throws IOException;

    public abstract boolean skipTo(int var1) throws IOException;

    public abstract int doc();

    public abstract int start();

    public abstract int end();

    public abstract Collection<byte[]> getPayload() throws IOException;

    public abstract boolean isPayloadAvailable();
}

