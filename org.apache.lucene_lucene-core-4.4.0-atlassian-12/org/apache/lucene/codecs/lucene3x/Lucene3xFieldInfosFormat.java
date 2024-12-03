/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import java.io.IOException;
import org.apache.lucene.codecs.FieldInfosFormat;
import org.apache.lucene.codecs.FieldInfosReader;
import org.apache.lucene.codecs.FieldInfosWriter;
import org.apache.lucene.codecs.lucene3x.Lucene3xFieldInfosReader;

@Deprecated
class Lucene3xFieldInfosFormat
extends FieldInfosFormat {
    private final FieldInfosReader reader = new Lucene3xFieldInfosReader();

    Lucene3xFieldInfosFormat() {
    }

    @Override
    public FieldInfosReader getFieldInfosReader() throws IOException {
        return this.reader;
    }

    @Override
    public FieldInfosWriter getFieldInfosWriter() throws IOException {
        throw new UnsupportedOperationException("this codec can only be used for reading");
    }
}

