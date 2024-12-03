/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.compressing;

import java.io.IOException;
import org.apache.lucene.store.DataOutput;

public abstract class Compressor {
    protected Compressor() {
    }

    public abstract void compress(byte[] var1, int var2, int var3, DataOutput var4) throws IOException;
}

