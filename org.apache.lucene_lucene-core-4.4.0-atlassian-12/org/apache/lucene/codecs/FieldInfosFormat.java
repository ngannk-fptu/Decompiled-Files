/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.codecs.FieldInfosReader;
import org.apache.lucene.codecs.FieldInfosWriter;

public abstract class FieldInfosFormat {
    protected FieldInfosFormat() {
    }

    public abstract FieldInfosReader getFieldInfosReader() throws IOException;

    public abstract FieldInfosWriter getFieldInfosWriter() throws IOException;
}

