/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.index.Fields;

public abstract class FieldsProducer
extends Fields
implements Closeable {
    protected FieldsProducer() {
    }

    @Override
    public abstract void close() throws IOException;
}

