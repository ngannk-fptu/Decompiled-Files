/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.IOException;
import org.apache.lucene.codecs.FieldInfosFormat;
import org.apache.lucene.codecs.FieldInfosReader;
import org.apache.lucene.codecs.FieldInfosWriter;
import org.apache.lucene.codecs.lucene40.Lucene40FieldInfosReader;

@Deprecated
public class Lucene40FieldInfosFormat
extends FieldInfosFormat {
    private final FieldInfosReader reader = new Lucene40FieldInfosReader();
    static final String FIELD_INFOS_EXTENSION = "fnm";
    static final String CODEC_NAME = "Lucene40FieldInfos";
    static final int FORMAT_START = 0;
    static final int FORMAT_CURRENT = 0;
    static final byte IS_INDEXED = 1;
    static final byte STORE_TERMVECTOR = 2;
    static final byte STORE_OFFSETS_IN_POSTINGS = 4;
    static final byte OMIT_NORMS = 16;
    static final byte STORE_PAYLOADS = 32;
    static final byte OMIT_TERM_FREQ_AND_POSITIONS = 64;
    static final byte OMIT_POSITIONS = -128;

    @Override
    public FieldInfosReader getFieldInfosReader() throws IOException {
        return this.reader;
    }

    @Override
    public FieldInfosWriter getFieldInfosWriter() throws IOException {
        throw new UnsupportedOperationException("this codec can only be used for reading");
    }
}

