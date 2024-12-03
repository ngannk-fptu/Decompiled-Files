/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;

abstract class InvertedDocEndConsumerPerField {
    InvertedDocEndConsumerPerField() {
    }

    abstract void finish() throws IOException;

    abstract void abort();
}

