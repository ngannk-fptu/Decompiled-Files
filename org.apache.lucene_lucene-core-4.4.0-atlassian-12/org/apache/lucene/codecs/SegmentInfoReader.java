/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;

public abstract class SegmentInfoReader {
    protected SegmentInfoReader() {
    }

    public abstract SegmentInfo read(Directory var1, String var2, IOContext var3) throws IOException;
}

