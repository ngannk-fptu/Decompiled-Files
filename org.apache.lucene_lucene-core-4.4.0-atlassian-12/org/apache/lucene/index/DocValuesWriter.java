/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.index.SegmentWriteState;

abstract class DocValuesWriter {
    DocValuesWriter() {
    }

    abstract void abort() throws IOException;

    abstract void finish(int var1);

    abstract void flush(SegmentWriteState var1, DocValuesConsumer var2) throws IOException;
}

