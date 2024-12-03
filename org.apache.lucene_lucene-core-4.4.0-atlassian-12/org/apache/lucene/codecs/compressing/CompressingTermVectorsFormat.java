/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.compressing;

import java.io.IOException;
import org.apache.lucene.codecs.TermVectorsFormat;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.codecs.TermVectorsWriter;
import org.apache.lucene.codecs.compressing.CompressingTermVectorsReader;
import org.apache.lucene.codecs.compressing.CompressingTermVectorsWriter;
import org.apache.lucene.codecs.compressing.CompressionMode;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;

public class CompressingTermVectorsFormat
extends TermVectorsFormat {
    private final String formatName;
    private final String segmentSuffix;
    private final CompressionMode compressionMode;
    private final int chunkSize;

    public CompressingTermVectorsFormat(String formatName, String segmentSuffix, CompressionMode compressionMode, int chunkSize) {
        this.formatName = formatName;
        this.segmentSuffix = segmentSuffix;
        this.compressionMode = compressionMode;
        if (chunkSize < 1) {
            throw new IllegalArgumentException("chunkSize must be >= 1");
        }
        this.chunkSize = chunkSize;
    }

    @Override
    public final TermVectorsReader vectorsReader(Directory directory, SegmentInfo segmentInfo, FieldInfos fieldInfos, IOContext context) throws IOException {
        return new CompressingTermVectorsReader(directory, segmentInfo, this.segmentSuffix, fieldInfos, context, this.formatName, this.compressionMode);
    }

    @Override
    public final TermVectorsWriter vectorsWriter(Directory directory, SegmentInfo segmentInfo, IOContext context) throws IOException {
        return new CompressingTermVectorsWriter(directory, segmentInfo, this.segmentSuffix, context, this.formatName, this.compressionMode, this.chunkSize);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(compressionMode=" + this.compressionMode + ", chunkSize=" + this.chunkSize + ")";
    }
}

