/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.IOException;
import org.apache.lucene.codecs.TermVectorsFormat;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.codecs.TermVectorsWriter;
import org.apache.lucene.codecs.lucene40.Lucene40TermVectorsReader;
import org.apache.lucene.codecs.lucene40.Lucene40TermVectorsWriter;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;

public class Lucene40TermVectorsFormat
extends TermVectorsFormat {
    @Override
    public TermVectorsReader vectorsReader(Directory directory, SegmentInfo segmentInfo, FieldInfos fieldInfos, IOContext context) throws IOException {
        return new Lucene40TermVectorsReader(directory, segmentInfo, fieldInfos, context);
    }

    @Override
    public TermVectorsWriter vectorsWriter(Directory directory, SegmentInfo segmentInfo, IOContext context) throws IOException {
        return new Lucene40TermVectorsWriter(directory, segmentInfo.name, context);
    }
}

