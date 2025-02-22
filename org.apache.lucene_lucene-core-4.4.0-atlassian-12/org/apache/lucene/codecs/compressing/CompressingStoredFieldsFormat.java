/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.compressing;

import java.io.IOException;
import org.apache.lucene.codecs.StoredFieldsFormat;
import org.apache.lucene.codecs.StoredFieldsReader;
import org.apache.lucene.codecs.StoredFieldsWriter;
import org.apache.lucene.codecs.compressing.CompressingStoredFieldsReader;
import org.apache.lucene.codecs.compressing.CompressingStoredFieldsWriter;
import org.apache.lucene.codecs.compressing.CompressionMode;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;

public class CompressingStoredFieldsFormat
extends StoredFieldsFormat {
    private final String formatName;
    private final String segmentSuffix;
    private final CompressionMode compressionMode;
    private final int chunkSize;

    public CompressingStoredFieldsFormat(String formatName, CompressionMode compressionMode, int chunkSize) {
        this(formatName, "", compressionMode, chunkSize);
    }

    public CompressingStoredFieldsFormat(String formatName, String segmentSuffix, CompressionMode compressionMode, int chunkSize) {
        this.formatName = formatName;
        this.segmentSuffix = segmentSuffix;
        this.compressionMode = compressionMode;
        if (chunkSize < 1) {
            throw new IllegalArgumentException("chunkSize must be >= 1");
        }
        this.chunkSize = chunkSize;
    }

    @Override
    public StoredFieldsReader fieldsReader(Directory directory, SegmentInfo si, FieldInfos fn, IOContext context) throws IOException {
        return new CompressingStoredFieldsReader(directory, si, this.segmentSuffix, fn, context, this.formatName, this.compressionMode);
    }

    @Override
    public StoredFieldsWriter fieldsWriter(Directory directory, SegmentInfo si, IOContext context) throws IOException {
        return new CompressingStoredFieldsWriter(directory, si, this.segmentSuffix, context, this.formatName, this.compressionMode, this.chunkSize);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(compressionMode=" + this.compressionMode + ", chunkSize=" + this.chunkSize + ")";
    }
}

