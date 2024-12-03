/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.FormatPostingsPositionsConsumer;
import java.io.IOException;

abstract class FormatPostingsDocsConsumer {
    FormatPostingsDocsConsumer() {
    }

    abstract FormatPostingsPositionsConsumer addDoc(int var1, int var2) throws IOException;

    abstract void finish() throws IOException;
}

