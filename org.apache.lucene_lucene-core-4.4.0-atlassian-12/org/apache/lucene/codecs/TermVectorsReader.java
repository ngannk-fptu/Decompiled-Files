/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.index.Fields;

public abstract class TermVectorsReader
implements Cloneable,
Closeable {
    protected TermVectorsReader() {
    }

    public abstract Fields get(int var1) throws IOException;

    public abstract TermVectorsReader clone();
}

