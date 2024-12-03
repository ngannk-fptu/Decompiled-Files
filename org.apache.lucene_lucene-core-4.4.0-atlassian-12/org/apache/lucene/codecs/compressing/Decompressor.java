/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.compressing;

import java.io.IOException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.BytesRef;

public abstract class Decompressor
implements Cloneable {
    protected Decompressor() {
    }

    public abstract void decompress(DataInput var1, int var2, int var3, int var4, BytesRef var5) throws IOException;

    public abstract Decompressor clone();
}

