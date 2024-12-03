/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.SegmentWriteState;

abstract class DocConsumer {
    DocConsumer() {
    }

    abstract void processDocument(FieldInfos.Builder var1) throws IOException;

    abstract void finishDocument() throws IOException;

    abstract void flush(SegmentWriteState var1) throws IOException;

    abstract void abort();
}

