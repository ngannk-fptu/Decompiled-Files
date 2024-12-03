/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;

public abstract class FieldInfosReader {
    protected FieldInfosReader() {
    }

    public abstract FieldInfos read(Directory var1, String var2, IOContext var3) throws IOException;
}

