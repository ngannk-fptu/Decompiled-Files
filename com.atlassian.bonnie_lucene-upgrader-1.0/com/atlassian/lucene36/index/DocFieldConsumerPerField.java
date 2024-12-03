/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Fieldable;
import java.io.IOException;

abstract class DocFieldConsumerPerField {
    DocFieldConsumerPerField() {
    }

    abstract void processFields(Fieldable[] var1, int var2) throws IOException;

    abstract void abort();

    abstract void close();
}

