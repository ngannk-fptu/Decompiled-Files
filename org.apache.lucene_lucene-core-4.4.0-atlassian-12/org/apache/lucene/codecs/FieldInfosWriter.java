/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;

public abstract class FieldInfosWriter {
    protected FieldInfosWriter() {
    }

    public abstract void write(Directory var1, String var2, FieldInfos var3, IOContext var4) throws IOException;
}

