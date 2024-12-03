/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocConsumerPerThread;
import com.atlassian.lucene36.index.DocumentsWriterThreadState;
import com.atlassian.lucene36.index.SegmentWriteState;
import java.io.IOException;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class DocConsumer {
    DocConsumer() {
    }

    abstract DocConsumerPerThread addThread(DocumentsWriterThreadState var1) throws IOException;

    abstract void flush(Collection<DocConsumerPerThread> var1, SegmentWriteState var2) throws IOException;

    abstract void abort();

    abstract boolean freeRAM();
}

