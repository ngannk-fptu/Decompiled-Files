/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocumentsWriter;
import java.io.IOException;

abstract class DocConsumerPerThread {
    DocConsumerPerThread() {
    }

    abstract DocumentsWriter.DocWriter processDocument() throws IOException;

    abstract void abort();
}

